package de.bht.chatbot.messenger.facebook;

import com.google.gson.Gson;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.messenger.facebook.model.FacebookBotMessage;
import de.bht.chatbot.messenger.facebook.model.FacebookInput;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Map;

import static de.bht.chatbot.messenger.facebook.FacebookSendAdapter.activateWebhook;

/**
 * Created by Oliver on 14.05.2017.
 */

@Path("/webhook")
public class FacebookReceiveAdapter {

    @Inject
    private MessageQueue messageQueue;

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

    String accessToken= MessengerUtils.getProperties().getProperty("FACEBOOK_BOT_TOKEN");
    String webhookToken = MessengerUtils.getProperties().getProperty("FACEBOOK_WEBHOOK_TOKEN");

    @POST
    @Path("/facebook")
    @Consumes("application/json")
    public String ReceiveMessage(String InputMessage) throws IOException {

        System.out.println("FACEBOOK_RECEIVE:Input:"+InputMessage);

        Boolean isEcho;
        JSONObject obj;

        //message object
        FacebookInput gs=new Gson().fromJson(InputMessage, FacebookInput.class);
        FacebookBotMessage msg = new FacebookBotMessage(gs.getEntry().get(0));

        if(gs.getEntry().get(0).getMessaging().get(0).getMessage()!=null) {
            if (!gs.getEntry().get(0).getMessaging().get(0).getMessage().getIsEcho()) {
                messageQueue.addInMessage(msg);
            }
        }

        return "\nReceived\n";

    }

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

            if("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))){
                System.out.println("FACEBOOK_WEBHOOK:VERIFIED");

                activateWebhook();

                return request.getParameter("hub.challenge");
            }
        }else{
            System.out.println("FACEBOOK_WEBHOOK:No request parameters were given.");
        }

        return "Webhook FAILED";
    }









}
