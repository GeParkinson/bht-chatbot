package de.bht.beuthbot.nlp.rasa;

import com.google.gson.Gson;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.TaskMessage;
import de.bht.beuthbot.nlp.rasa.model.RasaMessage;
import de.bht.beuthbot.nlp.rasa.model.RasaResponse;
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
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@MessageDriven(
        name = "RasaConnector",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "NLP IS NOT NULL AND RASA IS NOT NULL")
        }
)
public class RasaConnector implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(RasaConnector.class);
    private RasaRESTServiceInterface rasaProxy;
    private Gson gson;

    @Resource(lookup = "java:global/global/ProcessQueueBean")
    private ProcessQueue processQueue;

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;


    @PostConstruct
    public void init() {
        gson = new Gson();
        ResteasyClient client = new ResteasyClientBuilder().build();
        final String localRasaURL = application.getConfiguration(Configuration.RASA_URL);
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(localRasaURL));
        rasaProxy = target.proxy(RasaRESTServiceInterface.class);
    }


    @Override
    public void onMessage(final Message message) {
        try {
            ProcessQueueMessageProtocol incomingChatMessage = message.getBody(TaskMessage.class);
            String messageText = incomingChatMessage.getText();

            Response response = rasaProxy.processText(messageText);
            String responseAsString = response.readEntity(String.class);

            logger.debug("{}: {}", response.getStatus(), responseAsString);

            RasaResponse rasaResponse = gson.fromJson(responseAsString, RasaResponse.class);
            TaskMessage messageToMainBot = new TaskMessage(new RasaMessage(incomingChatMessage, rasaResponse));
            processQueue.route(messageToMainBot);
        } catch (JMSException e) {
            logger.error("Error while processing message.", e);
        }
    }
}
