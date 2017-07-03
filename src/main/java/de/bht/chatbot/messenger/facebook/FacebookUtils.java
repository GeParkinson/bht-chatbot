package de.bht.chatbot.messenger.facebook;

import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Properties;

/**
 * Created by oliver on 03.07.2017.
 */
public class FacebookUtils {
    /**
     * get and return token from properties
     * @return String Facebook-Message token
     */
    public String token(){
        Properties properties = MessengerUtils.getProperties();
        return properties.getProperty("FACEBOOK_BOT_TOKEN");
    }

    /**
     * activate Webhook:
     * it is necessary to activate the Facebook Webhook before usage
     */
    public void activateWebhook() {

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

    /**
     * uses the FacebookRESTServiceInterface to post the JSON data to Facebook
     * @param requestUrl the url to send the json to
     * @param payload string which contains the payload in json structure
     * @param token facebook API token
     * @return response from web request
     */
    public String sendPostRequest(String requestUrl, String payload, String token) {

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(requestUrl));
        FacebookRESTServiceInterface facebookProxy = target.proxy(FacebookRESTServiceInterface.class);

        Response response = facebookProxy.sendMessage(payload, token);
        String responseAsString = response.readEntity(String.class);


        return responseAsString;

    }
}
