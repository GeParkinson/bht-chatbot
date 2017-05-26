package jms;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
public class MessageQueue {

    @Inject
    private MessageQueueManager messageQueueManager;

    public boolean addMessage(final String message) {
        final JMSContext context = messageQueueManager.getContext();
        Message message = context.createTextMessage("Whats up?");
        context.createProducer().send(messageQueueManager.getTopic(), message);
        return true;
    }
}
