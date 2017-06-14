package message;

import attachments.AttachmentStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by Chris on 5/14/2017.
 */
public class BotMessage implements Serializable {
    private Long id;
    private Long messageID;
    private Long senderID;
    private Messenger messenger;
    private String text;
    private Long[] attachements;
    private String intent;
    private Map<String, String> entities;
    private String answer;

    @Inject
    private transient AttachmentStore attachmentStore;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID) {
        this.messageID = messageID;
    }

    public Long getSenderID() {
        return senderID;
    }

    public void setSenderID(Long senderID) {
        this.senderID = senderID;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Attachment[] getAttachements() {
        Attachment[] fileAttachments = new Attachment[attachements.length];
        for (int i = 0; i < attachements.length; i++) {
            fileAttachments[i] = attachmentStore.loadAttachment(attachements[i]);
        }
        return fileAttachments;
    }

    public void setAttachements(final Attachment[] attachements) {
        this.attachements = new Long[attachements.length];
        for (int i = 0; i < attachements.length; i++) {
            this.attachements[i] = attachmentStore.storeAttachment(attachements[i]);
        }
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, String> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
