package nlu.apiai.model;

import message.Attachment;
import message.BotMessage;
import message.Messenger;
import nlu.NLUResponse;

/**
 * Created by oliver on 19.06.2017.
 */
public class ApiAiMessage implements BotMessage {

    BotMessage bm;
    String content;

    public ApiAiMessage(BotMessage bm, NLUResponse nluResponse, String content){
        this.bm=bm;
        this.content=content;
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
        return content;
    }

    @Override
    public boolean hasAttachements() {
        return bm.hasAttachements();
    }

    @Override
    public Attachment[] getAttachements() {
        return bm.getAttachements();
    }
}
