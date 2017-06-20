package de.bht.chatbot.drools.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.message.NLUBotMessage;

import java.util.Map;

/**
 * @Author: Christopher Kümmel on 6/19/2017.
 */
public class DroolsMessage implements NLUBotMessage {

    private Long messageID;
    private Long senderID;
    private Messenger messenger;
    private String text;
    private String intent;
    private Map<String,String> entities;

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public Long getMessageID() {
        return messageID;
    }

    public void setMessageID(Long messageID){
        this.messageID = messageID;
    }

    @Override
    public Long getSenderID() {
        return senderID;
    }

    public void setSenderID(Long senderID){
        this.senderID = senderID;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    public void setMessenger(Messenger messenger){
        this.messenger = messenger;
    }

    @Override
    public String getText() {
        return text+"---went through drools---";
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public boolean hasAttachements() {
        return false;
    }

    @Override
    public Attachment[] getAttachements() {
        return null;
    }


    @Override
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent){
        this.intent = intent;
    }

    @Override
    public Map<String,String> getEntities() {
        return entities;
    }

    public void setEntity(Map<String,String> entities){
        this.entities = entities;
    }
}
