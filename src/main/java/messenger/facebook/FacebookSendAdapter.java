package messenger.facebook;

import message.BotMessage;
import messenger.utils.MessengerUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by oliver on 22.05.2017.
 */
@MessageDriven(
        name = "OutboxTelegramProcessor",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "maxSession", propertyValue = "1"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "Facebook = 'out'"
                )
        }
)
public class FacebookSendAdapter implements MessageListener {
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


    /** Send Text Message */
    private static void sendMessage(Long recipient, String messageJson) {
        String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \""+messageJson+"\"}}";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + token();
        try {
            sendPostRequest(requestUrl, payload);
        }
        catch(Exception ex){
        }
    }

    /** Send Photo Method */
    private static void sendMedia(BotMessage message,String mediaType){
        String payload = "{recipient: { id: "+message.getSenderID()+" }, message: { attachment: { type: \""+mediaType+"\", payload: { url: \""+message.getAttachements()[0].getFileUrl()+"\"  } }   }} ";
        System.out.println("FACEBOOK_SEND:Output:"+payload);
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=" + token();
        try {
            sendPostRequest(requestUrl, payload);
        }
        catch(Exception ex){
        }
    }

    public static String sendPostRequest(String requestUrl, String payload) {

        Entity<String> jsonInput = Entity.entity(payload, MediaType.APPLICATION_JSON);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(requestUrl);

        Invocation.Builder invocationBuilder = target.request("text/plain").header("Accept", "application/json").header("Content-Type", "application/json; charset=UTF-8");
        Invocation invocation = invocationBuilder.buildPost(jsonInput);
        invocation.invoke();

        return "";

    }

    @Override
    public void onMessage(Message messageIn) {
        BotMessage message = null;
        try {
            message = messageIn.getBody(BotMessage.class);

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
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
