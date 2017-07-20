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
 * <p>
 * ApiAi-specific class of the NLUBotMessage Interface
 */
public class ApiAiMessage implements ProcessQueueMessageProtocol {

    //store BotMessage and NLUResponse to query necessary information
    ProcessQueueMessageProtocol botMessage;
    NLUResponse nluResponse;

    public ApiAiMessage(ProcessQueueMessageProtocol botMessage, NLUResponse nluResponse){
        this.botMessage=botMessage;
        this.nluResponse=nluResponse;
    }

    @Override
    public Long getId() {
        return botMessage.getId();
    }

    @Override
    public Target getTarget() {
        return Target.MAINBOT;
    }

    @Override
    public Long getMessageID() {
        return botMessage.getMessageID();
    }

    @Override
    public Long getSenderID() {
        return botMessage.getSenderID();
    }

    @Override
    public Messenger getMessenger() {
        return botMessage.getMessenger();
    }

    @Override
    public String getText() {
        return botMessage.getText();
    }

    @Override
    public boolean hasAttachments() {
        return botMessage.hasAttachments();
    }

    @Override
    public List<Attachment> getAttachments() {
        return botMessage.getAttachments();
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
