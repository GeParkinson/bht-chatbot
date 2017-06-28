package de.bht.chatbot.messenger.facebook;

import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Properties;

/**
 * Created by oliver on 22.05.2017.
 */
@MessageDriven(
        name = "OutboxFacebookProcessor",
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

    /**
     * get and return token from properties
     * @return String Facebook-Message token
     */
    static String token(){
        Properties properties = MessengerUtils.getProperties();
        return properties.getProperty("FACEBOOK_BOT_TOKEN");
    }

    /**
     * activate Webhook:
     * it is necessary to activate the Facebook Webhook before usage
     */
    public static void activateWebhook() {

        //Start function to activate webhook after 5 seconds (to ensure the activation starts after the verification is finished)
        Runnable activation = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendPostRequest("https://graph.facebook.com/v2.9/me/subscribed_apps","",token());
            }
        };
        new Thread(activation).start();
    }


    /** Send Text Message
     * build a payload from the given message and send it to the facebook url
     */
    private static void sendMessage(Long recipient, String messageJson) {
        String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \""+messageJson+"\"}}";
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages" ;
        try {
            sendPostRequest(requestUrl, payload, token());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /** Send Media Method
     * fill payload with media information and send it to facebook
     */
    private static void sendMedia(BotMessage message,String mediaType){
        String payload = "{recipient: { id: "+message.getSenderID()+" }, message: { attachment: { type: \""+mediaType+"\", payload: { url: \""+message.getAttachments()[0].getFileURI()+"\"  } }   }} ";
        System.out.println("FACEBOOK_SEND:Output:"+payload);
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages" ;
        try {
            sendPostRequest(requestUrl, payload,token());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * uses the FacebookRESTServiceInterface to post the JSON data to Facebook
     * @param requestUrl the url to send the json to
     * @param payload string which contains the payload in json structure
     * @param token facebook API token
     * @return response from web request
     */
    public static String sendPostRequest(String requestUrl, String payload, String token) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(requestUrl));
        FacebookRESTServiceInterface facebookProxy = target.proxy(FacebookRESTServiceInterface.class);

        Response response = facebookProxy.sendMessage(payload, token);
        String responseAsString = response.readEntity(String.class);


        return responseAsString;

    }

    /**
     * receive messages from JMS and forward them to Facebook
     * @param messageIn message from JMS
     */
    @Override
    public void onMessage(Message messageIn) {
        BotMessage message = null;
        try {
            message = messageIn.getBody(BotMessage.class);

        // if message has attachment(s), use sendMedia function depending on type
        if(message.hasAttachments()) {
            switch (message.getAttachments()[0].getAttachmentType()) {
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
        // else use simple text message sending
        else{
            sendMessage(message.getSenderID(), message.getText());
        }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
