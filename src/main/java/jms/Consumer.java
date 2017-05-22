package jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
@MessageDriven(
        name = "InboxProcessor",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox")
        }
)
public class Consumer implements MessageListener {

    Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Override
    public void onMessage(final Message message) {
         try {
             logger.info(((TextMessage) message).getText());
         } catch (JMSException e) {
             logger.error("Could not receive message.", e);
         }
    }
}
