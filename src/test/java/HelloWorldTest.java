import jms.MessageQueue;
import message.BotMessage;
import message.Messenger;
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
                .addPackages(true, "attachments","jms", "message")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    MessageQueue messageQueue;

    @Test
    public void should_create_greeting() {
        BotMessage emptyMessage = new BotMessage();
        emptyMessage.setId(1L);
        emptyMessage.setText("Hello World!");
        emptyMessage.setMessenger(Messenger.TELEGRAM);
        messageQueue.addInMessage(emptyMessage);
        Assert.assertTrue("Arquillian Test", true);
    }
}
