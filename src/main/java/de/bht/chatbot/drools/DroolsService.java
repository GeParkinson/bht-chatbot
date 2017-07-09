package de.bht.chatbot.drools;


import de.bht.chatbot.canteen.model.CanteenData;
import de.bht.chatbot.canteen.Parser;

import com.google.gson.Gson;

import de.bht.chatbot.drools.model.DroolsMessage;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.NLUBotMessage;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    @Inject
    private MessageQueue messageQueue;

    @Inject
    private Parser parser;

    @Override
    public void onMessage(Message message) {
        try {
            NLUBotMessage botMessage = message.getBody(NLUBotMessage.class);

            botMessage = doRules(botMessage);

            logger.debug("ANSWER: " + botMessage.getText());

            messageQueue.addOutMessage(botMessage);
        } catch (JMSException e) {
            logger.error("Exception while setting bot message to the queue.", e);
        }
    }

    /**
     * Gets a botMessage and processes all fitting rules an it.
     * @param botMessage
     * @returns the botMessage with a new created answer text
     */
    private NLUBotMessage doRules(final NLUBotMessage botMessage){

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