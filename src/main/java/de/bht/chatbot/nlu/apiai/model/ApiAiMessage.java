package de.bht.chatbot.nlu.apiai.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.message.NLUBotMessage;
import de.bht.chatbot.nlu.NLUResponse;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by oliver on 19.06.2017.
 * <p>
 * ApiAi-specific class of the NLUBotMessage Interface
 */
public class ApiAiMessage implements NLUBotMessage, Serializable {

    //store BotMessage and NLUResponse to query necessary information
    BotMessage botMessage;
    NLUResponse nluResponse;

    public ApiAiMessage(BotMessage botMessage, NLUResponse nluResponse) {
        this.botMessage = botMessage;
        this.nluResponse = nluResponse;
    }

    @Override
    public Long getId() {
        return botMessage.getId();
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
    public Attachment[] getAttachments() {
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
