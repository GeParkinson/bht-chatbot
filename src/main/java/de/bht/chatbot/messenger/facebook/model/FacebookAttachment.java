
package de.bht.chatbot.messenger.facebook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;

/**
 * Created by oliver on 15.06.2017.
 */
public class FacebookAttachment implements Attachment {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("payload")
    @Expose
    private FacebookPayload payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FacebookPayload getPayload() {
        return payload;
    }

    public void setPayload(FacebookPayload payload) {
        this.payload = payload;
    }

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public String getFileURI() {
        return payload.getUrl();
    }

    @Override
    public AttachmentType getAttachmentType() {
        switch (type) {
            case "audio":
                return AttachmentType.AUDIO;
            case "video":
                return AttachmentType.VIDEO;
            case "file":
                return AttachmentType.DOCUMENT;
            case "image":
                return AttachmentType.PHOTO;
            default:
                return null;
        }


    }

    @Override
    public String getCaption() {
        return "Caption";
    }
}
