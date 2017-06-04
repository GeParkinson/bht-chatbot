package jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
public class MessageQueue {

    private Logger logger = LoggerFactory.getLogger(MessageQueue.class);

    @Inject
    private MessageQueueManager messageQueueManager;

    /**
     * Add new arrived message to system inbox queue
     * @param messageObject message from Telegram or Facebook
     * @return
     */
    public boolean addInMessage(final message.Message messageObject) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createObjectMessage(messageObject);
        try {
            switch (messageObject.getMessenger()) {
                case TELEGRAM:
                    message.setStringProperty("Telegram", "in");
                    break;
                case FACEBOOK:
                    message.setStringProperty("Facebook", "in");
                    break;
                default:
                    logger.error("Message isn't assign to a messenger: {}", messageObject);
                    return false;
            }
            context.createProducer().send(messageQueueManager.getTopic(), message);
            return true;
        } catch (JMSException e) {
            logger.error("Could not add message to inbox: {}", messageObject, e);
            return false;
        }
    }

    /**
     * Add message to system outbox queue
     * @param messageObject message to Telegram or Facebook
     * @return
     */
    public boolean addOutMessage(final message.Message messageObject) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createObjectMessage(messageObject);
        try {
            switch (messageObject.getMessenger()) {
                case TELEGRAM:
                    message.setStringProperty("Telegram", "out");
                    break;
                case FACEBOOK:
                    message.setStringProperty("Facebook", "out");
                    break;
                default:
                    logger.error("Message isn't assign to a messenger: {}", messageObject);
                    return false;
            }
            context.createProducer().send(messageQueueManager.getTopic(), message);
            return true;
        } catch (JMSException e) {
            logger.error("Could not add message to outbox: {}", messageObject, e);
            return false;
        }
    }
}
