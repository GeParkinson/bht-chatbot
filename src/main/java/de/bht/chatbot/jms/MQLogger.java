package de.bht.chatbot.jms;

import de.bht.chatbot.message.BotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
@MessageDriven(
        name = "InboxLogger",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "maxSession", propertyValue = "1")
        }
)
public class MQLogger implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(MQLogger.class);

    @Override
    public void onMessage(final Message message) {
         try {
             logger.info("Inbox receive message [{}]: {}", message.getJMSMessageID(), ((TextMessage) message).getText());
         } catch (JMSException e) {
             logger.error("Error while interpreting message.", e);
         }
    }
}
