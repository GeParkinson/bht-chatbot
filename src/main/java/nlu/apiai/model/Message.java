
package nlu.apiai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * @author: Oliver
 * Date: 19.06.17
 */
public class Message {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("speech")
    @Expose
    private String speech;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

}
