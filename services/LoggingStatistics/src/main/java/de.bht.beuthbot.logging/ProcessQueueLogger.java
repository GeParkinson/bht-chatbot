package de.bht.beuthbot.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
public class ProcessQueueLogger implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(ProcessQueueLogger.class);

    @Override
    public void onMessage(final Message message) {
         try {
             Map<String, String> customMessageHeaders = new HashMap<>();
             Enumeration propertyNames = message.getPropertyNames();
             while (propertyNames.hasMoreElements()) {
                 String propertyName = propertyNames.nextElement().toString();
                 if (!propertyName.startsWith("JMS")) {
                     customMessageHeaders.put(propertyName, message.getStringProperty(propertyName));
                 }
             }
             logger.info("Message [{}] with header {}: {}", message.getJMSMessageID(), customMessageHeaders, ((TextMessage) message).getText());
         } catch (JMSException e) {
             logger.error("Error while interpreting message.", e);
         }
    }
}
