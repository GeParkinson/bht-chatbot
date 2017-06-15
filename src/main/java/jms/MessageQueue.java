package jms;

import message.Attachment;
import message.BotMessage;
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
     * @param botMessageObject message from Telegram or Facebook
     * @return
     */
    public boolean addInMessage(final BotMessage botMessageObject) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createObjectMessage(botMessageObject);
        try {
            if (botMessageObject.hasAttachements()) {
                for (Attachment attachment : botMessageObject.getAttachements()) {
                    //TODO: different Attachementtypes -> different botMessageObjects
                    switch (attachment.getAttachmentType()) {
                        case AUDIO:
                            message.setStringProperty("BingConnector", "in");
                            break;
                        case VOICE:
                            message.setStringProperty("BingConnector", "in");
                            break;
                        default:
                            logger.error("new InMessage has Attachements but no defined case.");
                    }
                }
            } else {
                message.setStringProperty("NLU", "in");
            }
            context.createProducer().send(messageQueueManager.getTopic(), message);
            return true;
        } catch (JMSException e) {
            logger.error("Could not add message to inbox: {}", botMessageObject, e);
            return false;
        }
    }

    /**
     * Add message to system outbox queue
     * @param botMessageObject message to Telegram or Facebook
     * @return
     */
    public boolean addOutMessage(final BotMessage botMessageObject) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createObjectMessage(botMessageObject);
        try {
            switch (botMessageObject.getMessenger()) {
                case TELEGRAM:
                    message.setStringProperty("Telegram", "out");
                    break;
                case FACEBOOK:
                    message.setStringProperty("Facebook", "out");
                    break;
                default:
                    logger.error("BotMessage isn't assign to a messenger: {}", botMessageObject);
                    return false;
            }
            context.createProducer().send(messageQueueManager.getTopic(), message);
            return true;
        } catch (JMSException e) {
            logger.error("Could not add message to outbox: {}", botMessageObject, e);
            return false;
        }
    }
}
