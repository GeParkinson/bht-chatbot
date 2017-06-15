package messenger.facebook;

import jms.MessageQueue;
import message.Attachment;
import message.AttachmentType;
import message.BotMessage;
import message.Messenger;
import messenger.utils.MessengerUtils;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Map;

import static messenger.facebook.FacebookSendAdapter.activateWebhook;

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
        BotMessage msg = new BotMessage();

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

                    //messageQueue.addInMessage(msg);

                    //test out
                    messageQueue.addOutMessage(msg);
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

    String getSender(JSONObject obj) {
        //sender
        String sender = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
        System.out.println("FACEBOOK_RECEIVE:sender:"+sender);
        return sender;
    }

    String getMessage(JSONObject obj) {
        //message text
        String message = obj.getString("text");
        System.out.println("FACEBOOK_RECEIVE:message:"+message);
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
        System.out.println("FACEBOOK_RECEIVE:attachment:"+attType+"-"+url);
        return atts;

    }







}
