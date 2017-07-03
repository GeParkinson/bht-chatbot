package de.bht.beuthbot.model;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 26.06.17
 */
public class BotMessageImpl implements BotMessage {
    private Long id, messageID, senderID;
    private Messenger messenger;
    private String text;
    private Attachment[] attachments;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getMessageID() {
        return messageID;
    }

    @Override
    public Long getSenderID() {
        return senderID;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean hasAttachments() {
        return false;
    }

    @Override
    public Attachment[] getAttachments() {
        return new Attachment[0];
    }
}
