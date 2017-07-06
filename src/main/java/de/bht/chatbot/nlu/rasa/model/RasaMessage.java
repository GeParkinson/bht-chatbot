package de.bht.chatbot.nlu.rasa.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.message.NLUBotMessage;

import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 26.06.17
 */
public class RasaMessage implements NLUBotMessage {

    private Long id, messageID, senderID;
    private Messenger messenger;
    private String text, intent;
    private Attachment[] attachments;
    private Map<String, String> entities;
    boolean isAttachment = false;


    public RasaMessage(BotMessage botMessage, RasaResponse rasaResponse) {
        this.id = botMessage.getId();
        this.messageID = botMessage.getMessageID();
        this.senderID = botMessage.getSenderID();
        this.messenger = botMessage.getMessenger();
        this.text = botMessage.getText();
        this.isAttachment = botMessage.hasAttachments();
        this.attachments = botMessage.getAttachments();
        this.intent = rasaResponse.getIntent();
        this.entities = rasaResponse.getEntities();
    }

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
        return isAttachment;
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
