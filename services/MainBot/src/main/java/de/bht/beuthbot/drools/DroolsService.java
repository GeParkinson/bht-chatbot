package de.bht.beuthbot.drools;




import com.google.gson.Gson;


import de.bht.beuthbot.attachments.AttachmentStore;
import de.bht.beuthbot.canteen.Parser;
import de.bht.beuthbot.canteen.model.CanteenData;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.drools.model.DroolsMessage;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.TaskMessage;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by sJantzen on 13.06.2017.
 */
@MessageDriven(
        name = "DroolsProcessor",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "maxSession", propertyValue = "1"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "Drools = 'in'"
                )
        }
)
public class DroolsService implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(DroolsService.class);
    private Gson gson = new Gson();


    /** Injected JMS MessageQueue */
    @Resource(lookup = "java:global/global/ProcessQueueBean")
    private ProcessQueue processQueue;

    /** Injected AttachmentStore */
    @Resource(lookup = "java:global/global/AttachmentStoreBean")
    private AttachmentStore attachmentStore;

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    @Inject
    private Parser parser;

    @Override
    public void onMessage(Message message) {
        try {
            ProcessQueueMessageProtocol botMessage = message.getBody(TaskMessage.class);

            botMessage = doRules(botMessage);

            logger.debug("ANSWER: " + botMessage.getText());

            processQueue.route(botMessage);
        } catch (JMSException e) {
            logger.error("Exception while setting bot message to the queue.", e);
        }
    }

    /**
     * Gets a botMessage and processes all fitting rules an it.
     * @param botMessage
     * @returns the botMessage with a new created answer text
     */
    private ProcessQueueMessageProtocol doRules(final ProcessQueueMessageProtocol botMessage){

        // KieServices is the factory for all KIE services
        KieServices ks = KieServices.Factory.get();

        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();

        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession("CanteenKS");

        // Once the session is created, the application can interact with it
        // In this case it is setting a global as defined in the
        // org/drools/examples/helloworld/HelloWorld.drl file
        CanteenData canteenData = parser.parse();
        ksession.setGlobal("canteenData", canteenData);

        // The application can insert facts into the session
        logger.debug("Intent: " + botMessage.getIntent() + " entities: " + botMessage.getEntities());

        // Map incoming ApiAiMessages and RasaMessages to DroolsMessage
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(botMessage.getIntent());
        droolsMessage.setEntities(botMessage.getEntities());
        droolsMessage.setText(botMessage.getText());
        droolsMessage.setMessenger(botMessage.getMessenger());
        droolsMessage.setMessageID(botMessage.getMessageID());
        droolsMessage.setSenderID(botMessage.getSenderID());

        logger.debug("Text: " + droolsMessage.getText());

        ksession.insert(droolsMessage);

        // and fire the rules
        ksession.fireAllRules();

        // and then dispose the session
        ksession.dispose();

        return droolsMessage;
    }
}