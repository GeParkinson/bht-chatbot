package de.bht.chatbot.drools;

import de.bht.chatbot.drools.model.DroolsMessage;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.EntityName;
import de.bht.chatbot.message.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sJantzen on 25.06.2017.
 */
@Path("/drools/")
public class DroolsTestSenderService {

    private Logger logger = LoggerFactory.getLogger(DroolsTestSenderService.class);

    @Inject
    private MessageQueue messageQueue;

    @GET
    @Path("/test0")
    public String getDroolsTest0() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(Intent.GREETING.getText());

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test1")
    public String getDroolsTest1() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(Intent.SAYING_GOODBYE.getText());

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2")
    public String getDroolsTest2() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(Intent.SHOW_FOOD.getText());

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test3")
    public String getDroolsTest3() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(Intent.SHOW_FOOD.getText());

        Map<String, String> entities = new HashMap<>();
        entities.put(EntityName.DATE.getText(), "today");

        droolsMessage.setEntities(entities);

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test4")
    public String getDroolsTest4() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent(Intent.SHOW_FOOD.getText());

        Map<String, String> entities = new HashMap<>();
        entities.put(EntityName.DATE.getText(), "tomorrow");
        entities.put(EntityName.DISH_TYPE.getText(), "vegan");
        entities.put(EntityName.HEALTHY.getText(), "green");

        droolsMessage.setEntities(entities);

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

}
