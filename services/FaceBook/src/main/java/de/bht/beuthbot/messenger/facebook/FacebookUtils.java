package de.bht.beuthbot.messenger.facebook;

import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 03.07.2017.
 */
public class FacebookUtils {

    /**
     * slf4j Logger
     */
    private final Logger logger = LoggerFactory.getLogger(FacebookUtils.class);

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

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

                sendPostRequest("https://graph.facebook.com/v2.9/me/subscribed_apps","", application.getConfiguration(Configuration.FACEBOOK_BOT_TOKEN));

            }
        };
        new Thread(activation).start();
    }

    /**
     * uses the FacebookRESTServiceInterface to post the JSON data to Facebook
     *
     * @param requestUrl the url to send the json to
     * @param payload    string which contains the payload in json structure
     * @param token      facebook API token
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

    /**
     * splits a Facebook output messages into smaller messages due to the charlimit set by facebook
     *
     * @param messageJson      long input message
     * @param charLimit        the maximum amount of characters
     * @param seperateMessages boolean whether you want to split at a specific string
     * @param seperator        specific string to split message at
     * @return list of small messages
     */
    public List<String> splitIntoMultipleMessages(String messageJson, int charLimit, Boolean seperateMessages, String seperator) {
        List<String> messages = new ArrayList<String>();

        //facebook allows a maximum of 640 characters, message must be split if necessary:
        String linesOfMessage[] = messageJson.split("\\r?\\n");

        String currentOutput = "";
        for (int i = 0; i < linesOfMessage.length; i++) {
            String line = linesOfMessage[i];
            if ((currentOutput + "\\n" + line).length() > charLimit || (line.contains(seperator) && seperateMessages)) {
                //if appending new line would increase the chars over charLimit, put current output and start new one
                messages.add(currentOutput);

                //hide seperator if message is split by entries
                if (line.contains(seperator) && seperateMessages) {
                    line = "";
                }

                //begin a new output with the current line (which was not send because 640 chars would have been reached)
                currentOutput = line;
            } else {
                //append line if char limit not reached
                currentOutput = currentOutput + "\\n" + line;
            }


        }
        messages.add(currentOutput);


        return messages;
    }
}
