package de.bht.beuthbot.drools;

import de.bht.beuthbot.drools.model.DroolsMessage;
import de.bht.beuthbot.jms.ProcessQueue;
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
    private ProcessQueue processQueue;

    @GET
    @Path("/test0")
    public String getDroolsTest0() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser0");

        processQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test1")
    public String getDroolsTest1() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser1");

        processQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test2")
    public String getDroolsTest2() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser2");

        processQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test3")
    public String getDroolsTest3() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser3");

        processQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

    @GET
    @Path("/test4")
    public String getDroolsTest4() {
        DroolsMessage droolsMessage = new DroolsMessage();
        droolsMessage.setIntent("Test Parser4");

        processQueue.addMessage(droolsMessage, "Drools", "in");

        return "Test Ende";
    }

}
