package nsp.bing.model;

import message.Attachment;
import message.BotMessage;
import message.Messenger;
import nsp.NSPResponse;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingMessage implements BotMessage{

    private NSPResponse nspResponse;

    public BingMessage(NSPResponse nspResponse){
        this.nspResponse = nspResponse;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Long getMessageID() {
        return null;
    }

    @Override
    public Long getSenderID() {
        return null;
    }

    @Override
    public Messenger getMessenger() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public boolean hasAttachements() {
        return false;
    }

    @Override
    public Attachment[] getAttachements() {
        return new Attachment[0];
    }
}
