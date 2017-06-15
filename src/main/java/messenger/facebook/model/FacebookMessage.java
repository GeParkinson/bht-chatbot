
package messenger.facebook.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by oliver on 15.06.2017.
 */
public class FacebookMessage implements Serializable {

    @SerializedName("is_echo")
    @Expose
    private Boolean isEcho;
    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("seq")
    @Expose
    private Integer seq;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("attachments")
    @Expose
    private List<FacebookAttachment> attachments = null;

    public Boolean getIsEcho() {
        if(isEcho!=null) {
            return isEcho;
        }
        else {
            return false;
        }
    }

    public void setIsEcho(Boolean isEcho) {
        this.isEcho = isEcho;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<FacebookAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FacebookAttachment> attachments) {
        this.attachments = attachments;
    }

}
