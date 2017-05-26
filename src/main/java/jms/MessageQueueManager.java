package jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.*;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 18.05.17
 */
@Startup
@Singleton
public class MessageQueueManager {

    @Resource(lookup = "java:/jboss/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/messages/inbox")
    private Topic messageInbox;

    private JMSContext context;
    

    /**
     *
     */
    @PostConstruct
    public void initialize() {
        context = connectionFactory.createContext();
    }

    @PreDestroy
    public void terminate() {
            context.close();
    }

    public JMSContext getContext() {
        return context;
    }

    public Topic getTopic() {
        return messageInbox;
    }
}
