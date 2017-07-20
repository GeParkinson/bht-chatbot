package de.bht.chatbot.messenger.facebook;

import de.bht.chatbot.attachments.AttachmentStore;
import de.bht.chatbot.attachments.model.AttachmentStoreMode;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

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
     * slf4j Logger
     */
    private final Logger logger = LoggerFactory.getLogger(FacebookSendAdapter.class);

    @Inject
    private AttachmentStore attachmentStore;

    @Inject
    private FacebookUtils facebookUtils;

    /**
     * Send Text Message
     * build a payload from the given message and send it to the facebook url
     */
    private void sendMessage(Long recipient, String messageJson) {
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages";

        //do you want to put each entry (e.g. dish) into a seperate message? - this is suggested due to facebooks 640 characters limit
        Boolean seperateMessages = true;
        String seperator = ", --------------------------";

        //split (maybe) long message into multiple messages of sendable size
        for (String currentMessage : facebookUtils.splitIntoMultipleMessages(messageJson, 600, seperateMessages, seperator)) {
            String payload = "{\"recipient\": {\"id\": \"" + recipient + "\"}, \"message\": { \"text\": \"" + currentMessage + "\"}}";
            try {
                //send message
                facebookUtils.sendPostRequest(requestUrl, payload, facebookUtils.token());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Send Media Method
     * fill payload with media information and send it to facebook
     */
    private void sendMedia(BotMessage message, String mediaType) {
        String fileURL = attachmentStore.loadAttachmentPath(message.getAttachments()[0].getId(), AttachmentStoreMode.FILE_URI);
        String payload = "{recipient: { id: " + message.getSenderID() + " }, message: { attachment: { type: \"" + mediaType + "\", payload: { url: \"" + fileURL + "\"  } }   }} ";
        logger.debug("FACEBOOK_SEND:Output:" + payload);
        String requestUrl = "https://graph.facebook.com/v2.6/me/messages";
        try {
            facebookUtils.sendPostRequest(requestUrl, payload, facebookUtils.token());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * receive messages from JMS and forward them to Facebook
     *
     * @param messageIn message from JMS
     */
    @Override
    public void onMessage(Message messageIn) {
        try {
            BotMessage message = messageIn.getBody(BotMessage.class);

            // if message has attachment(s), use sendMedia function depending on type
            //mediaType represents the possible media types for outgoing Facebook Messages
            if (message.hasAttachments()) {
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
                    case UNKOWN:
                        sendMessage(message.getSenderID(), "Sorry! I'm just a bot and my developers just implemented audio and voice attachments...");
                        break;
                }
            }
            // else use simple text message sending
            else {
                sendMessage(message.getSenderID(), message.getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
