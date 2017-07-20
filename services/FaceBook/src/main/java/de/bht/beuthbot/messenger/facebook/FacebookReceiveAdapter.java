package de.bht.beuthbot.messenger.facebook;

import com.google.gson.Gson;

import de.bht.beuthbot.attachments.AttachmentStore;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.jms.TaskMessage;
import de.bht.beuthbot.messenger.facebook.model.FacebookBotMessage;
import de.bht.beuthbot.messenger.facebook.model.FacebookInput;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Map;


/**
 * Created by Oliver on 14.05.2017.
 */
@Path("")
public class FacebookReceiveAdapter {


    /**
     * slf4j Logger
     */
    private final Logger logger = LoggerFactory.getLogger(FacebookReceiveAdapter.class);


    /**
     * Injected JMS MessageQueue
     */
    @Resource(lookup = "java:global/global/ProcessQueueBean")
    private ProcessQueue processQueue;

    /**
     * Injected AttachmentStore
     */
    @Resource(lookup = "java:global/global/AttachmentStoreBean")
    private AttachmentStore attachmentStore;

    /**
     * BeuthBot Application Bean
     */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    @Inject
    private FacebookUtils facebookUtils;

    private String webhookToken;

    /**
     * Initialize TelegramBot with Token
     */
    @PostConstruct
    public void init() {
        webhookToken = application.getConfiguration(Configuration.FACEBOOK_WEBHOOK_TOKEN);
    }

    /**
     * listen to POST requests from facebook (which contain the received messages) and react to them
     *
     * @param InputMessage message send by facebook
     * @return request-answer to return to facebook
     * @throws IOException
     */
    @POST
    @Path("/getUpdates")
    @Consumes("application/json")
    public String ReceiveMessage(String InputMessage) {

        logger.debug("FACEBOOK_RECEIVE:Input:" + InputMessage);

        Boolean isEcho;
        JSONObject obj;

        //parse the incoming json to a FacebookInput-object
        FacebookInput facebookInput = new Gson().fromJson(InputMessage, FacebookInput.class);

        //iterate through all entries of the input (usually it's just 1) and create FacebookBotMessages
        for (int i = 0; i < facebookInput.getEntry().size(); i++) {
            //create a new FacebookBotMessage from the Entry object of the FacebookInput
            FacebookBotMessage facebookBotMessage = new FacebookBotMessage(facebookInput.getEntry().get(i));

            if (facebookBotMessage.isIncomingMessage()) {

                //generate ID if message has attachment
                facebookBotMessage.generateAttachmentID(attachmentStore);

                //put message into JMS-queue

                processQueue.route(new TaskMessage(facebookBotMessage));

            }

        }

        return "\nReceived\n";

    }

    /**
     * function to listen to webhook verification tries and verify and activate them if the webhook token is correct
     *
     * @param request request from facebook to verify webhook
     * @return return hub.challenge to facebook to inform facebook about successful verification
     */
    @GET
    @Path("/getUpdates")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request) {


        logger.debug("FACEBOOK_WEBHOOK:request: " + request);
        Map<String, String[]> parametersMap = request.getParameterMap();
        if (parametersMap.size() > 0) {
            logger.debug("FACEBOOK_WEBHOOK:HUB_MODE: " + request.getParameter("hub.mode"));
            logger.debug("FACEBOOK_WEBHOOK:HUB_VERIFY_TOKEN: " + request.getParameter("hub.verify_token"));
            logger.debug("FACEBOOK_WEBHOOK:HUB_CHALLENGE: " + request.getParameter("hub.challenge"));

            //compare verification token in properties with token received from facebook
            if ("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))) {
                logger.debug("FACEBOOK_WEBHOOK:VERIFIED");

                //if all conditions apply, finish verification by returning hub-challenge and activate token after 5s delay
                facebookUtils.activateWebhook();
                return request.getParameter("hub.challenge");
            }
        } else {
            logger.debug("FACEBOOK_WEBHOOK:No request parameters were given.");
        }

        return "Webhook FAILED";
    }

    /**
     * makes it possible to set the Facebook webhook to the current server adress
     * just open /rest/facebook/setWebhook to update your hook
     * you need an APP! access token from https://developers.facebook.com/tools/explorer/
     *
     * @param request starts when user navigates to setWebhook page
     * @return answer from Facebook "{"success":true}" if worked
     */
    @GET
    @Path("/setWebhook")
    @Produces("text/plain")
    public String setWebhook(@Context HttpServletRequest request) {

        String access_token = application.getConfiguration(Configuration.FACEBOOK_ACCESS_TOKEN);

        //access token contains the App-ID, which we need for our call
        String appid = access_token.substring(0, access_token.indexOf("|"));

        String registrationAdress = "https://graph.facebook.com/v2.9/" + appid + "/subscriptions";
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(registrationAdress));
        FacebookRESTServiceInterface facebookProxy = target.proxy(FacebookRESTServiceInterface.class);

        String fields = "messages, messaging_postbacks, messaging_optins, message_deliveries, message_reads, messaging_payments, messaging_pre_checkouts, messaging_checkout_updates, messaging_account_linking, messaging_referrals, message_echoes";

        String callback_url = application.getConfiguration(Configuration.WEB_URL) + "/getUpdates";
        Response response = facebookProxy.sendHook("page", callback_url, fields, webhookToken, access_token);

        String responseAsString = response.readEntity(String.class);


        return responseAsString;
    }


}
