package messenger.facebook;

import message.Attachment;
import message.FileType;
import messenger.utils.MessengerUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by oliver on 22.05.2017.
 */
public class FacebookSendAdapter {
    static String token(){
        Properties properties = MessengerUtils.getProperties();
        return properties.getProperty("FACEBOOK_BOT_TOKEN");
    }

    /** activate Webhook */
    public static void activateWebhook() {
        //Start function to activate webhook
        Runnable activation = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                sendPostRequest("https://graph.facebook.com/v2.9/me/subscribed_apps?access_token="+token(),"");
            }
        };
        new Thread(activation).start();//Call it when you need to run the function
    }


    public static void sendMessage(message.Message message){
        if(message.getAttachements()!=null) {
            switch (message.getAttachements()[0].getAttachmentType()) {
                case AUDIO:
                    sendMedia(message, "audio");
                    break;
                case VOICE:
                    sendMedia(message, "audio");
                    break;
                case VIDEO:
                    sendMedia(message, "video");
                    break;
                case DOCUMENT:
                    sendMedia(message, "file");
                    break;
                case PHOTO:
                    sendMedia(message, "image");
                    break;
            }
        }
        else{
                sendMessage(message.getSenderID(), message.getText());
        }
    }

    /** Send Text Message */
    private static void sendMessage(Long recipient, String messageJson) {
        String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \""+messageJson+"\"}}";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + token();
        try {
            sendPostRequest(requestUrl, payload);
        }
        catch(Exception ex){
            //System.out.println(ex.getMessage()+" ERROR");
        }
        //System.out.println(payload+"------"+requestUrl);
    }

    /** Send Photo Method */
    private static void sendMedia(message.Message message,String mediaType){
        String payload = "{recipient: { id: "+message.getSenderID()+" }, message: { attachment: { type: \""+mediaType+"\", payload: { url: \""+message.getAttachements()[0].getFileUrl()+"\"  } }   }} ";
        System.out.println("Output:"+payload);
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + token();
        try {
            sendPostRequest(requestUrl, payload);
        }
        catch(Exception ex){
            //System.out.println(ex.getMessage()+" ERROR");
        }
        //System.out.println(payload+"------"+requestUrl);
    }

    //CURL
    public static String sendPostRequest(String requestUrl, String payload) {
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();

            //System.out.println(connection.getResponseMessage());

            connection.disconnect();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }

}
