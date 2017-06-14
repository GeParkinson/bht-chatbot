package drools;

import message.BotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by sJantzen on 14.06.2017.
 */
@MessageDriven(
        name = "DroolsReceiver",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "maxSession", propertyValue = "1"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "Drools = 'out'"
                )
        }
)
public class DroolsReceiver implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(DroolsReceiver.class);

    @Override
    public void onMessage(Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
            logger.info("Created answer: " + botMessage.getAnswer());

        } catch (JMSException e) {
            logger.error("Exception while setting bot message to the queue.", e);
        }
    }

}
