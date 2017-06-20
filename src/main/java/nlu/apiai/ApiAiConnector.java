package nlu.apiai;

import com.google.gson.Gson;
import jms.MessageQueue;
import message.BotMessage;
import messenger.utils.MessengerUtils;
import nlu.apiai.model.ApiAiMessage;
import nlu.apiai.model.ApiAiResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Properties;

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

    @Inject
    private MessageQueue messageQueue;

    private final Logger logger = LoggerFactory.getLogger(ApiAiConnector.class);
    private ApiAiRESTServiceInterface apiaiProxy;

    @PostConstruct
    public void init() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("https://api.api.ai/api/query"));
        apiaiProxy = target.proxy(ApiAiRESTServiceInterface.class);
    }


    @Override
    public void onMessage(final Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);

            Properties properties = MessengerUtils.getProperties();
            String token = properties.getProperty("API_AI_TOKEN");

            String sessionID = String.valueOf(botMessage.getMessageID());
            String language = "en";

            Response response = apiaiProxy.processText(botMessage.getText(), language, sessionID,"BHT-Chatbot","Bearer " + token);
            String responseAsString = response.readEntity(String.class);

            ApiAiResponse gs=new Gson().fromJson(responseAsString, ApiAiResponse.class);
            ApiAiMessage msg = new ApiAiMessage(botMessage,gs);

            //System.out.println("API.AI RESPONSE:"+responseAsString);
            messageQueue.addMessage(msg, "Drools", "in");

        } catch (JMSException e) {
            logger.error("Could not process message.", e);
        }
    }
}
