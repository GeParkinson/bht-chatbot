package de.bht.beuthbot.drools.model;

import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Christopher Kümmel on 6/19/2017.
 */
public class DroolsMessage implements ProcessQueueMessageProtocol {

    private Long messageID;
    private Long senderID;
    private Messenger messenger;
    private String text;
    private String intent;
    private Map<String,String> entities;
    private boolean asVoiceMessage;

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public Target getTarget() {
        return Target.MESSENGER;
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
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public boolean hasAttachments() {
        return false;
    }

    @Override
    public List<Attachment> getAttachments() {
        return Collections.emptyList();
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

    public void setEntities(Map<String,String> entities){
        this.entities = entities;
    }

    public boolean isAsVoiceMessage() {
        return asVoiceMessage;
    }

    public void setAsVoiceMessage(boolean asVoiceMessage) {
        this.asVoiceMessage = asVoiceMessage;
    }
}
