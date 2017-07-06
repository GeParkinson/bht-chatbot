package de.bht.beuthbot.messenger.facebook;

import com.google.gson.Gson;
import de.bht.beuthbot.attachments.AttachmentStore;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.messenger.facebook.model.FacebookAttachment;
import de.bht.beuthbot.messenger.facebook.model.FacebookBotMessage;
import de.bht.beuthbot.messenger.facebook.model.FacebookInput;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Map;


/**
 * Created by Oliver on 14.05.2017.
 */

@Path("/webhook")
public class FacebookReceiveAdapter {

    @Inject
    private ProcessQueue processQueue;

    @Inject
    private AttachmentStore attachmentStore;

    @Inject
    private FacebookUtils facebookUtils;

    /** BeuthBot Application Bean */
    @Inject
    private Application application;

    //---------------------------------------
    //Testing:
    //install localtunnel (npm install -g localtunnel) and connect via 'lt --port 8080' *
    //*doesnt work in some networks like eduroam
    //
    //get your accessToken in the messenger product of your facebook app
    //
    //create webhook with:
    //URL:  https://XXXXXXXXXXXXX.localtunnel.me/bht-chatbot/rest/webhook/facebook
    //token: set in config.properties
    //---------------------------------------

    private String accessToken;
    private String webhookToken;

    public FacebookReceiveAdapter() {
        this.accessToken = application.getConfiguration(Configuration.FACEBOOK_BOT_TOKEN);
        this.webhookToken = application.getConfiguration(Configuration.FACEBOOK_WEBHOOK_TOKEN);
    }

    /**
     * listen to POST requests from facebook (which contain the received messages) and react to them
     * @param InputMessage message send by facebook
     * @return request-answer to return to facebook
     * @throws IOException
     */
    @POST
    @Path("/facebook")
    @Consumes("application/json")
    public String ReceiveMessage(String InputMessage) throws IOException {

        System.out.println("FACEBOOK_RECEIVE:Input:"+InputMessage);

        Boolean isEcho;
        JSONObject obj;

        //parse the message to a FacebookInput-object, which represents the Json-structure of Facebook messages
        FacebookInput gs=new Gson().fromJson(InputMessage, FacebookInput.class);

        //create a new FacebookBotMessage from the Entry object of the FacebookInput
        FacebookBotMessage msg = new FacebookBotMessage(gs.getEntry().get(0));

        //check if Message-node of BotMessage object exists
        if(gs.getEntry().get(0).getMessaging().get(0).getMessage()!=null) {
            // 'IsEcho' means whether the message is a new incoming message or just an 'echo' of a message the bot sends out
            if (!gs.getEntry().get(0).getMessaging().get(0).getMessage().getIsEcho()) {

                //if Attachment --> get ID via AttachmentStore and put it into attachment
                if(gs.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments()!=null) {
                    FacebookAttachment att=gs.getEntry().get(0).getMessaging().get(0).getMessage().getAttachments().get(0);
                    Long id = attachmentStore.storeAttachment(att.getFileURI(), att.getAttachmentType());
                    att.setID(id);
                }

                //put message into JMS-queue
                processQueue.addInMessage(msg);
            }
        }

        return "\nReceived\n";

    }

    /**
     * function to listen to webhook verification tries and verify and activate them if the webhook token is correct
     * @param request request from facebook to verify webhook
     * @return return hub.challenge to facebook to inform facebook about successful verification
     */
    @GET
    @Path("/facebook")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request){


        System.out.println("FACEBOOK_WEBHOOK:request: " + request);
        Map<String, String[]> parametersMap = request.getParameterMap();
        if (parametersMap.size() > 0) {
            System.out.println("FACEBOOK_WEBHOOK:HUB_MODE: " + request.getParameter("hub.mode"));
            System.out.println("FACEBOOK_WEBHOOK:HUB_VERIFY_TOKEN: " + request.getParameter("hub.verify_token"));
            System.out.println("FACEBOOK_WEBHOOK:HUB_CHALLENGE: " + request.getParameter("hub.challenge"));

            //compare verification token in properties with token received from facebook
            if("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))){
                System.out.println("FACEBOOK_WEBHOOK:VERIFIED");

                //if all conditions apply, finish verification by returning hub-challenge and activate token after 5s delay
                facebookUtils.activateWebhook();
                return request.getParameter("hub.challenge");
            }
        }else{
            System.out.println("FACEBOOK_WEBHOOK:No request parameters were given.");
        }

        return "Webhook FAILED";
    }









}
