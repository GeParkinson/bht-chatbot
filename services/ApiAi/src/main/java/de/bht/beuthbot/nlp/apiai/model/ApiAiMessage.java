package de.bht.beuthbot.nlp.apiai.model;

import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;
import de.bht.beuthbot.model.nlp.NLUResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by oliver on 19.06.2017.
 *
 * ApiAi-specific class of the NLUBotMessage Interface
 */
public class ApiAiMessage implements ProcessQueueMessageProtocol {

    //store BotMessage and NLUResponse to query necessary information
    ProcessQueueMessageProtocol bm;
    NLUResponse nluResponse;

    public ApiAiMessage(ProcessQueueMessageProtocol bm, NLUResponse nluResponse){
        this.bm=bm;
        this.nluResponse=nluResponse;
    }

    @Override
    public Long getId() {
        return bm.getId();
    }

    @Override
    public Target getTarget() {
        return Target.MAINBOT;
    }

    @Override
    public Long getMessageID() {
        return bm.getMessageID();
    }

    @Override
    public Long getSenderID() {
        return bm.getSenderID();
    }

    @Override
    public Messenger getMessenger() {
        return bm.getMessenger();
    }

    @Override
    public String getText() {
        return bm.getText()+"-Entities:"+getEntities().toString()+"-Intent:"+getIntent();
    }

    @Override
    public boolean hasAttachments() {
        return bm.hasAttachments();
    }

    @Override
    public List<Attachment> getAttachments() {
        return bm.getAttachments();
    }

    @Override
    public String getIntent() {
        return nluResponse.getIntent();
    }

    @Override
    public Map<String, String> getEntities() {
        return nluResponse.getEntities();
    }
}
