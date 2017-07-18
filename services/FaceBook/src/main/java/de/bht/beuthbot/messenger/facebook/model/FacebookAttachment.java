
package de.bht.beuthbot.messenger.facebook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.AttachmentType;

/**
 * Created by oliver on 15.06.2017.
 *
 * Facebook-specific class of the Attachment Interface which is also the class which represents the Attachment-node in the message-Json
 */
public class FacebookAttachment implements Attachment {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("payload")
    @Expose
    private FacebookPayload payload;
    private Long id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FacebookPayload getPayload() {
        return payload;
    }

    public void setPayload(FacebookPayload payload) { this.payload = payload; }

    public void setID(Long id){ this.id=id; }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFileURI() {
        return payload.getUrl();
    }

    /**
     * parse facebook specific types to the types the Chatbot implements
     * @return Bot-intern Attachment type
     */
    @Override
    public AttachmentType getAttachmentType() {
        switch (type) {
            case "audio":
                return AttachmentType.AUDIO;
            //TODO: handle all attachment types; at the moment MessageQueue only allows unknown, audio, voice
            //case "video":
            //    return AttachmentType.VIDEO;
            //case "file":
            //    return AttachmentType.DOCUMENT;
            //case "image":
            //    return AttachmentType.PHOTO;
            default:
                return AttachmentType.UNKOWN;
        }


    }

    @Override
    public String getCaption() {
        return "Caption";
    }
}
