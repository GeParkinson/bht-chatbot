package messenger.facebook;

import message.Attachment;
import message.AttachmentType;
import message.Messenger;
import messenger.utils.MessengerUtils;
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
    public String ReceiveMessage(String InputMessage) throws IOException {

        System.out.println("Input:"+InputMessage);

        String sender ="";
        String message="";
        Attachment attach = null;
        Boolean isEcho;
        JSONObject obj;

        //message object
        message.Message msg = new message.Message();

        obj = new JSONObject(InputMessage);

        //check if message
        if(obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message")!=null) {


                //sender
                try {

                    sender = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id");
                } catch (Exception ex) {}

                //message text
            try {

                message = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getString("text");
            } catch (Exception ex) {}

            //attachements
            try {
                String attType = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getJSONArray("attachments").getJSONObject(0).getString("type");
                String url = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getJSONArray("attachments").getJSONObject(0).getJSONObject("payload").getString("url");

                attach=new Attachment();
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

                message="attached "+attType;

                Attachment[] atts= new Attachment[1];
                atts[0]=attach;
                msg.setAttachements(atts);

                } catch (Exception ex) {}

            //is echo? --> echo message for send messages
                try {
                    obj = new JSONObject(InputMessage);
                    isEcho = obj.getJSONArray("entry").getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getBoolean("is_echo");
                } catch (Exception ex) {
                    isEcho = false;
                }

                if ((message.length() > 0) && isEcho == false) {

                    msg.setText(message);
                    msg.setMessenger(Messenger.FACEBOOK);
                    msg.setMessageID(Long.valueOf("85753757347"));
                    msg.setSenderID(Long.valueOf(sender));

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







}
