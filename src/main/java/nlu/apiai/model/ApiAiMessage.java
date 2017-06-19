package nlu.apiai.model;

import message.Attachment;
import message.BotMessage;
import message.Messenger;
import message.NLUBotMessage;
import nlu.NLUResponse;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by oliver on 19.06.2017.
 */
public class ApiAiMessage implements NLUBotMessage, Serializable {

    BotMessage bm;
    NLUResponse nluResponse;

    public ApiAiMessage(BotMessage bm, NLUResponse nluResponse){
        this.bm=bm;
        this.nluResponse=nluResponse;
    }

    @Override
    public Long getId() {
        return bm.getId();
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
        return bm.getText()+"-Entities:"+getEntities().toString()+"-Intend:"+getIntent();
    }

    @Override
    public boolean hasAttachements() {
        return bm.hasAttachements();
    }

    @Override
    public Attachment[] getAttachements() {
        return bm.getAttachements();
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
