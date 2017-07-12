package de.bht.chatbot.drools;

import de.bht.chatbot.drools.model.DroolsMessage;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.EntityName;
import de.bht.chatbot.message.Intent;
import de.bht.chatbot.message.NLUBotMessage;
import de.bht.chatbot.nlu.apiai.model.ApiAiMessage;
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
        droolsMessage.setIntent("Hello");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test00")
    public String getDroolsTest00() {

        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("greet");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test000")
    public String getDroolsTest000() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Hello");
        droolsMessage.setMessageID(Long.valueOf("123456"));

        messageQueue.addMessage(droolsMessage, "NLU", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test1")
    public String getDroolsTest1() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Bye");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test11")
    public String getDroolsTest11() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Bye");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test111")
    public String getDroolsTest111() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Bye");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2")
    public String getDroolsTest2() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Was gibt es heute");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test21")
    public String getDroolsTest21() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Was gibt es morgen für vegane Gerichte?");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test211")
    public String getDroolsTest211() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Was gibt es veganes?");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2111")
    public String getDroolsTest2111() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Zeige gesunde Gerichte");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test22")
    public String getDroolsTest22() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Was gibt es heute in der Mensa?");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test222")
    public String getDroolsTest222() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Zeige Gerichte");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2222")
    public String getDroolsTest2222() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Ich habe Hunger");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test22222")
    public String getDroolsTest22222() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("Wie ist der aktuelle Speiseplan?");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test3")
    public String getDroolsTest3() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setText("start");

        messageQueue.addMessage(droolsMessage, "NLU_2", "in");

        return "Test Ende";
    }



}
