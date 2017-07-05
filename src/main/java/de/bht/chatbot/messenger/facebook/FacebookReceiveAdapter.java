package de.bht.chatbot.messenger.facebook;

import com.google.gson.Gson;
import de.bht.chatbot.attachments.AttachmentStore;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.messenger.facebook.model.FacebookAttachment;
import de.bht.chatbot.messenger.facebook.model.FacebookBotMessage;
import de.bht.chatbot.messenger.facebook.model.FacebookInput;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Map;


/**
 * Created by Oliver on 14.05.2017.
 */

@Path("/facebook")
public class FacebookReceiveAdapter {

    @Inject
    private MessageQueue messageQueue;

    @Inject
    private AttachmentStore attachmentStore;

    @Inject
    private FacebookUtils facebookUtils;

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

    String webhookToken = MessengerUtils.getProperties().getProperty("FACEBOOK_WEBHOOK_TOKEN");

    /**
     * listen to POST requests from facebook (which contain the received messages) and react to them
     * @param InputMessage message send by facebook
     * @return request-answer to return to facebook
     * @throws IOException
     */
    @POST
    @Path("/getUpdates")
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
                messageQueue.addInMessage(msg);
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
    @Path("/getUpdates")
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

    /**
     * makes it possible to set the Facebook webhook to the current server adress
     * just open /rest/facebook/setWebhook to update your hook
     * you need an APP! access token from https://developers.facebook.com/tools/explorer/
     * @param request starts when user navigates to setWebhook page
     * @return answer from Facebook "{"success":true}" if worked
     */
    @GET
    @Path("/setWebhook")
    @Produces("text/plain")
    public String setWebhook(@Context HttpServletRequest request){

        String access_token = facebookUtils.accessID();

        //access token contains the App-ID, which we need for our call
        String appid = access_token.substring(0 , access_token.indexOf("|"));

        String registrationAdress="https://graph.facebook.com/v2.9/"+appid+"/subscriptions";
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(registrationAdress));
        FacebookRESTServiceInterface facebookProxy = target.proxy(FacebookRESTServiceInterface.class);

        String fields ="messages, messaging_postbacks, messaging_optins, message_deliveries, message_reads, messaging_payments, messaging_pre_checkouts, messaging_checkout_updates, messaging_account_linking, messaging_referrals, message_echoes";

        String callback_url ="https://chatbot.ziemers.net/rest/webhook/facebook";//facebookUtils.webadress()+"/facebook/getUpdates";
        Response response = facebookProxy.sendText("page", callback_url, fields, webhookToken, access_token);

        String responseAsString = response.readEntity(String.class);


        return responseAsString;
    }






}
