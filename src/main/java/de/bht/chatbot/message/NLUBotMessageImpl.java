package de.bht.chatbot.message;

import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 26.06.17
 */
public class NLUBotMessageImpl implements NLUBotMessage {
    private Long id, messageID, senderID;
    private Messenger messenger;
    private String text, intent;
    private Attachment[] attachments;
    private Map<String, String> entities;

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
        return attachments;
    }

    @Override
    public String getIntent() {
        return intent;
    }

    @Override
    public Map<String, String> getEntities() {
        return entities;
    }
}
