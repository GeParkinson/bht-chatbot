package de.bht.beuthbot.nlp.apiai;

import com.google.gson.Gson;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.TaskMessage;
import de.bht.beuthbot.nlp.apiai.model.ApiAiMessage;
import de.bht.beuthbot.nlp.apiai.model.ApiAiResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
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

    /** Injected JMS MessageQueue */
    @Resource(lookup = "java:global/global/ProcessQueueBean")
    private ProcessQueue processQueue;

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    private final Logger logger = LoggerFactory.getLogger(ApiAiConnector.class);
    private ApiAiRESTServiceInterface apiaiProxy;

    @PostConstruct
    public void init() {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("https://api.api.ai/api/query"));
        apiaiProxy = target.proxy(ApiAiRESTServiceInterface.class);
    }

    /**
     * process messages received over the JMS
     *
     * @param message from JMS which contains the bot message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            ProcessQueueMessageProtocol botMessage = message.getBody(TaskMessage.class);
            logger.debug("Receive message: {}", botMessage.toString());

            String token = application.getConfiguration(Configuration.API_AI_TOKEN);

            String sessionID = String.valueOf(botMessage.getMessageID());
            String language = "de";

            //create a requests to te API.ai server
            Response response = apiaiProxy.processText(botMessage.getText(), language, sessionID, "BHT-Chatbot", "Bearer " + token);
            String responseAsString = response.readEntity(String.class);

            //parse the response into ApiAiResponse
            ApiAiResponse apiAiResponse = new Gson().fromJson(responseAsString, ApiAiResponse.class);

            //Create ApiAiMessage
            ApiAiMessage apiAiMessage = new ApiAiMessage(botMessage, apiAiResponse);

            //logger.debug("API.AI RESPONSE:"+responseAsString);

            //put ApiAiMessage into messageQueue
            processQueue.route(new TaskMessage(apiAiMessage));

        } catch (JMSException e) {
            logger.error("Could not process message.", e);
        }
    }
}
