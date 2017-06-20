import de.bht.chatbot.drools.DroolsService;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.message.NLUBotMessage;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by sJantzen on 13.06.2017.
 */
public class DroolsTest {

    @Test
    public void droolsRulesTest() {

        NLUBotMessage botMessage = new NLUBotMessage() {
            @Override
            public String getIntent() {
                return "Test";
            }

            @Override
            public Map<String, String> getEntities() {
                return null;
            }

            @Override
            public Long getId() {
                return null;
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
                return null;
            }

            @Override
            public String getText() {
                return null;
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
        botMessage = DroolsService.doRules(botMessage);

        Assert.assertEquals("Test successful", botMessage.getAnswer());
    }
}
