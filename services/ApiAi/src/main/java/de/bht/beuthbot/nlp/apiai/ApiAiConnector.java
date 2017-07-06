package de.bht.beuthbot.nlp.apiai;

import com.google.gson.Gson;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.model.BotMessage;
import de.bht.beuthbot.model.BotMessageImpl;
import de.bht.beuthbot.nlp.apiai.model.ApiAiMessage;
import de.bht.beuthbot.nlp.apiai.model.ApiAiResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @authors: georg.glossmann@adesso.de (template) + oliverdavid@hotmail.de (content)
 * Date: 04.06.17
 */
@MessageDriven(
        name = "ApiAiConnector",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "NLU = 'in'")
        }
)
public class ApiAiConnector implements MessageListener {
    private Gson gson;

    @Inject
    private ProcessQueue processQueue;

    @Inject
    private Application application;

    private final Logger logger = LoggerFactory.getLogger(ApiAiConnector.class);
    private ApiAiRESTServiceInterface apiaiProxy;

    @PostConstruct
    public void init() {
        gson = new Gson();
        
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("https://api.api.ai/api/query"));
        apiaiProxy = target.proxy(ApiAiRESTServiceInterface.class);
    }

    /**
     * process messages received over the JMS
     * @param message from JMS which contains the bot message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            BotMessage botMessage = gson.fromJson(((TextMessage) message).getText(), BotMessageImpl.class);

            String token = application.getConfiguration(Configuration.API_AI_TOKEN);

            String sessionID = String.valueOf(botMessage.getMessageID());
            String language = "en";

            //create a requests to te API.ai server
            Response response = apiaiProxy.processText(botMessage.getText(), language, sessionID,"BHT-Chatbot","Bearer " + token);
            String responseAsString = response.readEntity(String.class);

            //parse the response into ApiAiResponse
            ApiAiResponse gs=new Gson().fromJson(responseAsString, ApiAiResponse.class);

            //Create ApiAiMessage
            ApiAiMessage msg = new ApiAiMessage(botMessage,gs);

            //System.out.println("API.AI RESPONSE:"+responseAsString);

            //put ApiAiMessage into messageQueue
            processQueue.addMessage(msg, "Drools", "in");

        } catch (JMSException e) {
            logger.error("Could not process message.", e);
        }
    }
}
