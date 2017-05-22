package message;

/**
 * Created by Chris on 5/14/2017.
 */
public class Message {
    Long id;
    Long messageID;
    Long senderID;
    Messenger messenger;
    String text;
    Attachment[] attachements;


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
        return attachements;
    }

    public void setAttachements(Attachment[] attachements) {
        this.attachements = attachements;
    }
}
