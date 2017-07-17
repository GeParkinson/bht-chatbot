import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.jms.TaskMessage;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@RunWith(Arquillian.class)
public class DebugMessageGenerator {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "de.bht.beuthbot.jms", "de.bht.beuthbot.model")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Resource(lookup = "java:/jboss/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/messages/inbox")
    private Topic messageInbox;

    @Test
    public void should_create_greeting() throws JMSException {
        final JMSContext context = connectionFactory.createContext();

        Message message = context.createObjectMessage(new TaskMessage(new TestMessage()));
        message.setStringProperty("NLP", "nlp");
        message.setStringProperty("RASA", "rasa");
        context.createProducer().send(messageInbox, message);
    }

    private class TestMessage implements ProcessQueueMessageProtocol {

        @Override
        public Long getId() {
            return -1L;
        }

        @Override
        public Target getTarget() {
            return Target.NTSP;
        }

        @Override
        public Long getMessageID() {
            return -1L;
        }

        @Override
        public Long getSenderID() {
            return -1L;
        }

        @Override
        public Messenger getMessenger() {
            return Messenger.TELEGRAM;
        }

        @Override
        public String getText() {
            return "Was gibt's heute zu essen?";
        }

        @Override
        public boolean hasAttachments() {
            return false;
        }

        @Override
        public List<Attachment> getAttachments() {
            return Collections.emptyList();
        }

        @Override
        public String getIntent() {
            return null;
        }

        @Override
        public Map<String, String> getEntities() {
            return Collections.emptyMap();
        }
    }
}
