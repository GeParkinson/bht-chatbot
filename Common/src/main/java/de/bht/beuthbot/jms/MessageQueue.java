package de.bht.beuthbot.jms;

import com.google.gson.Gson;
import de.bht.beuthbot.message.Attachment;
import de.bht.beuthbot.message.BotMessage;
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

    private final Logger logger = LoggerFactory.getLogger(MessageQueue.class);
    private final Gson gson = new Gson();

    /** Injected JMS MessageQueueManager */
    @Inject
    private MessageQueueManager messageQueueManager;

    /**
     * Add new arrived message to system inbox queue
     * @param botMessageObject message from Telegram or Facebook
     * @return
     */
    public boolean addInMessage(final BotMessage botMessageObject) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createTextMessage(gson.toJson(botMessageObject));
        try {
            if (botMessageObject.hasAttachments()) {
                for (Attachment attachment : botMessageObject.getAttachments()) {
                    //TODO: different AttachmentTypes -> different botMessageObjects
                    switch (attachment.getAttachmentType()) {
                        case AUDIO:
                            message.setStringProperty("BingConnector", "in");
                            break;
                        case VOICE:
                            message.setStringProperty("BingConnector", "in");
                            break;
                        case UNKOWN:
                            addOutMessage(botMessageObject);
                        default:
                            logger.error("new InMessage has Attachements but no defined case. Type should be UNKNOWN.");
                    }
                }
            } else {
                message.setStringProperty("BingConnector", "in");
            }
            context.createProducer().send(messageQueueManager.getTopic(), message);
            return true;
        } catch (JMSException e) {
            logger.error("Could not add message to inbox: {}", botMessageObject, e);
            return false;
        }
    }

    /**
     * Adds a new message to the system inbox queue and sets a string property to the message with
     * the given property name and value.
     * @param botMessageObject
     * @param propertyName
     * @param propertyValue
     * @returns true, false if a given parameter is null or something went wrong.
     */
    public boolean addMessage(final BotMessage botMessageObject, final String propertyName, final String propertyValue) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createTextMessage(gson.toJson(botMessageObject));
        try {
            if (botMessageObject == null){
                logger.error("No bot message was given");
                return false;
            }

            if (propertyName == null || propertyName.equals("")){
                logger.error("No property name was given");
                return false;
            }

            if (propertyValue == null || propertyValue.equals("")){
                logger.error("No property value was given");
                return false;
            }

            message.setStringProperty(propertyName, propertyValue);
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
        Message message = context.createTextMessage(gson.toJson(botMessageObject));
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
