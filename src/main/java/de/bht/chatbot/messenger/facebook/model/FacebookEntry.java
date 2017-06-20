
package de.bht.chatbot.messenger.facebook.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by oliver on 15.06.2017.
 */
public class FacebookEntry implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("messaging")
    @Expose
    private List<FacebookMessaging> messaging = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<FacebookMessaging> getMessaging() {
        return messaging;
    }

    public void setMessaging(List<FacebookMessaging> messaging) {
        this.messaging = messaging;
    }

}
