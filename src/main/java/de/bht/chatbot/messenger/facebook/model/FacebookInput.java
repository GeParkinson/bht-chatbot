
package de.bht.chatbot.messenger.facebook.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by oliver on 15.06.2017.
 *
 * represents the highest node of the json-object received by facebook
 */
public class FacebookInput implements Serializable {

    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("entry")
    @Expose
    private List<FacebookEntry> entry = null;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<FacebookEntry> getEntry() {
        return entry;
    }

    public void setEntry(List<FacebookEntry> entry) {
        this.entry = entry;
    }

}
