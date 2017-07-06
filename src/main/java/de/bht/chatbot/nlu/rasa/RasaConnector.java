package de.bht.chatbot.nlu.rasa;

import com.google.gson.Gson;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.BotMessageImpl;
import de.bht.chatbot.nlu.rasa.model.RasaMessage;
import de.bht.chatbot.nlu.rasa.model.RasaResponse;
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
                        propertyName = "messageSelector", propertyValue = "NLU = 'in'")
        }
)
public class RasaConnector implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(RasaConnector.class);
    private RasaRESTServiceInterface rasaProxy;
    private Gson gson;

    @Inject
    private MessageQueue messageQueue;
    

    @PostConstruct
    public void init() {
        gson = new Gson();
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://rasa_nlu:5000"));
        rasaProxy = target.proxy(RasaRESTServiceInterface.class);
    }


    @Override
    public void onMessage(final Message message) {
        try {
            BotMessage incomingChatMessage = message.getBody(BotMessage.class);
            String messageText = incomingChatMessage.getText();

            Response response = rasaProxy.processText(messageText);
            String responseAsString = response.readEntity(String.class);

            logger.debug("{}: {}", response.getStatus(), responseAsString);

            RasaResponse rasaResponse = gson.fromJson(responseAsString, RasaResponse.class);


            messageQueue.addMessage(new RasaMessage(incomingChatMessage, rasaResponse), "Drools", "in");
        } catch (JMSException e) {
            logger.error("Error while processing message.", e);
        }
    }
}
