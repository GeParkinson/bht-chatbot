import de.bht.chatbot.jms.MessageQueue;
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

import javax.inject.Inject;


/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@RunWith(Arquillian.class)
public class HelloWorldTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "de/bht/chatbot/attachments", "de/bht/chatbot/jms", "de/bht/chatbot/message")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    MessageQueue messageQueue;

    @Test
    public void should_create_greeting() {
        BotMessage emptyMessage = new BotMessage() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public Long getMessageID() {
                return null;
            }

            @Override
            public Long getSenderID() {
                return null;
            }

            @Override
            public Messenger getMessenger() {
                return Messenger.TELEGRAM;
            }

            @Override
            public String getText() {
                return "Hello World!";
            }

            @Override
            public boolean hasAttachements() {
                return false;
            }

            @Override
            public Attachment[] getAttachements() {
                return new Attachment[0];
            }
        };
        messageQueue.addInMessage(emptyMessage);
        Assert.assertTrue("Arquillian Test", true);
    }
}
