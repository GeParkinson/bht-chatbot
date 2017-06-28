import com.google.gson.Gson;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.jms.MessageQueueManager;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.*;
import java.io.Serializable;


/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@RunWith(Arquillian.class)
public class MessageProcessingTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "de.bht.chatbot.message")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Resource(lookup = "java:/jboss/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/messages/inbox")
    private Topic messageInbox;

    @Test
    public void should_create_greeting() throws JMSException {
        final JMSContext context = connectionFactory.createContext();
        Message testMessage = context.createTextMessage("{messenger: \"TELEGRAM\", text: \"Hello World\"}");
        testMessage.setStringProperty("NLU", "in");
        context.createProducer().send(messageInbox, testMessage);
    }
}
