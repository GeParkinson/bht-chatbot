import message.BotMessage;
import org.junit.Test;
import drools.DroolsService;

import static org.junit.Assert.assertEquals;

/**
 * Created by sJantzen on 13.06.2017.
 */
public class DroolsTest {

    @Test
    public void droolsRulesTest() {

        BotMessage botMessage = new BotMessage();
        botMessage.setIntent("Test");
        botMessage = DroolsService.doRules(botMessage);

        assertEquals("Test successful", botMessage.getAnswer());
    }
}
