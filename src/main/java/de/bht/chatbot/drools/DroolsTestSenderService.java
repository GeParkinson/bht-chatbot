package de.bht.chatbot.drools;

import de.bht.chatbot.drools.model.DroolsMessage;
import de.bht.chatbot.jms.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
        droolsMessage.setIntent("Test Parser0");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test1")
    public String getDroolsTest1() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser1");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2")
    public String getDroolsTest2() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser2");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test3")
    public String getDroolsTest3() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser3");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test4")
    public String getDroolsTest4() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser4");

        messageQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

}
