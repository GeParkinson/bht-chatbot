
package messenger.facebook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oliver on 15.06.2017.
 */
public class FacebookMessaging implements Serializable {

    @SerializedName("sender")
    @Expose
    private FacebookSender sender;
    @SerializedName("recipient")
    @Expose
    private FacebookRecipient recipient;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("message")
    @Expose
    private FacebookMessage message;

    public FacebookSender getSender() {
        return sender;
    }

    public void setSender(FacebookSender sender) {
        this.sender = sender;
    }

    public FacebookRecipient getRecipient() {
        return recipient;
    }

    public void setRecipient(FacebookRecipient recipient) {
        this.recipient = recipient;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public FacebookMessage getMessage() {
        return message;
    }

    public void setMessage(FacebookMessage message) {
        this.message = message;
    }

}
