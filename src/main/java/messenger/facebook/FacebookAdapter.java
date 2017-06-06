package messenger.facebook;

import com.mashape.unirest.http.exceptions.UnirestException;
import message.Attachment;
import message.AttachmentType;
import message.Messenger;
import messenger.utils.MessengerUtils;
import nlp.APIai;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static messenger.facebook.FacebookSendAdapter.activateWebhook;

/**
 * Created by Oliver on 5/14/2017.
 */

@Path("/webhook")
public class FacebookAdapter {


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
    public String ReceiveMessage(String InputMessage) throws IOException, UnirestException {

        System.out.println("Input:"+InputMessage);

        Boolean isEcho;
        JSONObject obj;

        //message object
        message.Message msg = new message.Message();

        obj = new JSONObject(InputMessage);

        //check if message
        if(obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).has("message")) {

            //set object to message node
            JSONObject inputMsg=obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message");

            if(inputMsg.has("attachments")){
            JSONObject attachmentNode=inputMsg.getJSONArray("attachments").getJSONObject(0);
            msg.setAttachements(getAttachments(attachmentNode));
            }
            if(inputMsg.has("text")) {
                msg.setText(getMessage(inputMsg));
            }

            if(obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").has("id")) {
                msg.setSenderID(Long.valueOf(getSender(obj)));
            }

            //TODO: ID
            msg.setMessageID(Long.valueOf("85753757347"));
            msg.setMessenger(Messenger.FACEBOOK);

            //is echo? --> echo message for send messages
                try {
                    obj = new JSONObject(InputMessage);
                    isEcho = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getBoolean("is_echo");
                } catch (Exception ex) {
                    isEcho = false;
                }

                if (isEcho == false) {

                    //TODO: SEND MESSAGE VIA JMS....
                    FacebookSendAdapter.sendMessage(msg);
                }
            }

        return "\nReceived\n";

    }

    @GET
    @Path("/facebook")
    @Produces("text/plain")
    public String verification(@Context HttpServletRequest request){


        System.out.println("request: " + request);
        Map<String, String[]> parametersMap = request.getParameterMap();
        if (parametersMap.size() > 0) {
            System.out.println("HUB_MODE: " + request.getParameter("hub.mode"));
            System.out.println("HUB_VERIFY_TOKEN: " + request.getParameter("hub.verify_token"));
            System.out.println("HUB_CHALLENGE: " + request.getParameter("hub.challenge"));

            if("subscribe".equals(request.getParameter("hub.mode")) &&
                    webhookToken.equals(request.getParameter("hub.verify_token"))){
                System.out.println("VERIFIED");

                activateWebhook();

                return request.getParameter("hub.challenge");
            }
        }else{
            System.out.println("No request parameters were given.");
        }

        return "Webhook FAILED";
    }

    String getSender(JSONObject obj) {
        //sender
        String sender = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
        System.out.println(sender);
        return sender;
    }

    String getMessage(JSONObject obj) {
        //message text
        String message = obj.getString("text");
        System.out.println(message);
        return message;
    }

    Attachment[] getAttachments(JSONObject obj) {
        //attachements
        Attachment attach;

            String attType = obj.getString("type");
            String url = obj.getJSONObject("payload").getString("url");

            attach = new Attachment();
            attach.setFileUrl(url);

            switch (attType) {
                case "image":
                    attach.setAttachmentType(AttachmentType.PHOTO);
                    break;
                case "audio":
                    attach.setAttachmentType(AttachmentType.AUDIO);
                    break;
                case "fallback":
                    break;
                case "file":
                    attach.setAttachmentType(AttachmentType.DOCUMENT);
                    break;
                case "location":
                    break;
                case "video":
                    attach.setAttachmentType(AttachmentType.VIDEO);
                    break;
            }


            Attachment[] atts = new Attachment[1];
            atts[0] = attach;
        System.out.println(attType+"-"+url);
        return atts;

    }







}
