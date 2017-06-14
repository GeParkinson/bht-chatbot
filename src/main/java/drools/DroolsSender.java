package drools;

import jms.MessageQueue;
import message.BotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by sJantzen on 14.06.2017.
 */
@Path("/drools/")
public class DroolsSender {

    private Logger logger = LoggerFactory.getLogger(DroolsSender.class);

    @Inject
    private MessageQueue messageQueue;

    @GET
    @Path("/test")
    public String getDroolsTest() {

        BotMessage botMessage = new BotMessage();
        botMessage.setIntent("Test");

        messageQueue.addMessage(botMessage, "Drools", "in");

        /*
        botMessage = DroolsService.doRules(botMessage);
        if("Test successful".equals(botMessage.getAnswer())){
            return botMessage.getAnswer();
        }
        */

        return "Test end";
    }

}
