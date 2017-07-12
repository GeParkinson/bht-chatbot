package de.bht.beuthbot.jms;

import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.AttachmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

/**
 * @author: georg.parkinson@adesso.de
 * Date: 22.05.17
 */
@Stateful
@ApplicationScoped
public class ProcessQueueBean implements ProcessQueue {

    private final Logger logger = LoggerFactory.getLogger(ProcessQueueBean.class);

    @Resource(lookup = "java:/jboss/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/messages/inbox")
    private Topic topic;

    /**
     * BeuthBot Application Bean
     */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    private JMSContext context;


    /**
     * Initialize the JMS context.
     */
    @PostConstruct
    public void init() {
        context = connectionFactory.createContext();
    }


    @Override
    public void route(final ProcessQueueMessageProtocol processQueueMessage) {
        TaskMessage taskMessage = new TaskMessage(processQueueMessage);
        Message message = context.createObjectMessage(taskMessage);
        try {
            switch (taskMessage.getTarget()) {
                case NTSP:
                    if (!taskMessage.hasAttachments()) {
                        message.setStringProperty("NLP", "nlp");
                        if ("on".equals(application.getConfiguration(Configuration.APIAI_UNIT_ON))) {
                            message.setStringProperty("APIAI", "apiai");
                        }
                        if ("on".equals(application.getConfiguration(Configuration.RASA_UNIT_ON))) {
                            message.setStringProperty("RASA", "rasa");
                        }
                    } else {
                        Attachment attachment = taskMessage.getAttachments().get(0);
                        if (attachment.getAttachmentType() == AttachmentType.AUDIO || attachment.getAttachmentType() == AttachmentType.VOICE) {
                            message.setStringProperty("BingConnector", "in");
                        } else {
                            logger.warn("Unsupported attachment! Reroute message to {}.\nMessage: {}", Target.MAINBOT, taskMessage);
                            route(new TaskMessage(taskMessage, Target.MAINBOT));
                        }
                    }
                    break;
                case MAINBOT:
                    message.setStringProperty("DROOLS", "drools");
                    break;
                case MESSENGER:
                    switch (taskMessage.getMessenger()) {
                        case TELEGRAM:
                            if ("on".equals(application.getConfiguration(Configuration.TELEGRAM_UNIT_ON))) {
                                message.setStringProperty("TELEGRAM", "telegram");
                            }
                            break;
                        case FACEBOOK:
                            if ("on".equals(application.getConfiguration(Configuration.FACEBOOK_UNIT_ON))) {
                                message.setStringProperty("FACEBOOK", "facebook");
                            }
                            break;
                        default:
                            logger.error("No messenger assigned! Could not route internal message.\nMessage: {}", taskMessage);
                    }
                    break;
                default:
                    logger.error("Target unknown! Could not route internal message.\nMessage: {}", taskMessage);
                    return;
            }
        } catch (Exception exception) {
            logger.error("Could not route message.\nMessage: {}", taskMessage, exception);
            return;
        }
        context.createProducer().send(topic, message);
    }
}
