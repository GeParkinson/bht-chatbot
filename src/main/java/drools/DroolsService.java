package drools;

import jms.MessageQueue;
import message.BotMessage;
import message.NLUBotMessage;
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
import java.util.ArrayList;

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

    private Logger logger = LoggerFactory.getLogger(MessageQueue.class);

    @Inject
    private MessageQueue messageQueue;

    @Override
    public void onMessage(Message message) {
        try {
            NLUBotMessage botMessage = message.getBody(NLUBotMessage.class);

            botMessage = doRules(botMessage);

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
    public static NLUBotMessage doRules(final NLUBotMessage botMessage){

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
        // TODO Sja: ich glaube hier müsste man dann den Cache setzen, damit man ihn in den Regeln benutzen kann
        //ksession.setGlobal( "canteenCache", CACHE-Object);

        // The application can insert facts into the session
        ksession.insert(botMessage);

        // and fire the rules
        ksession.fireAllRules();

        // and then dispose the session
        ksession.dispose();

        return botMessage;
    }
}