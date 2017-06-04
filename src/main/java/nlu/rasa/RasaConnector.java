package nlu.rasa;

import com.google.gson.Gson;
import nlu.rasa.model.RasaResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
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
                        propertyName = "messageSelector", propertyValue = "Telegram = 'in'")
        }
)
public class RasaConnector implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(RasaConnector.class);
    private RasaRESTServiceInterface rasaProxy;
    

    @PostConstruct
    public void init() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://rasa_nlu:5000"));
        rasaProxy = target.proxy(RasaRESTServiceInterface.class);
    }


    @Override
    public void onMessage(final Message message) {
        try {
            message.Message incomingChatMessage = message.getBody(message.Message.class);
            String messageText = incomingChatMessage.getText();

            Response response = rasaProxy.processText(messageText);
            String responseAsString = response.readEntity(String.class);

            logger.debug("{}: {}", response.getStatus(), responseAsString);

            RasaResponse rasaResponse = new Gson().fromJson(responseAsString, RasaResponse.class);

        } catch (JMSException e) {
            logger.error("Error while processing message.", e);
        }
    }
}
