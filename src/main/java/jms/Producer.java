package jms;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
public class Producer {

    @Inject
    private JMSManager jmsManager;

    public boolean sendNewTextMessage(String text) {
        final JMSContext context = jmsManager.getContext();
        Message message = context.createTextMessage("Whats up?");
        context.createProducer().send(jmsManager.getTopic(), message);
        return true;
    }
}
