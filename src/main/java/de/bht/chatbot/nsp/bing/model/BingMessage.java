package de.bht.chatbot.nsp.bing.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.nsp.NSPResponse;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingMessage implements BotMessage{

    private NSPResponse nspResponse;
    private Long messageID;
    private Long senderID;
    private Messenger messenger;

    public BingMessage(NSPResponse nspResponse, BotMessage botMessage){
        this.nspResponse = nspResponse;
        this.messageID = botMessage.getMessageID();
        this.senderID = botMessage.getSenderID();
        this.messenger = botMessage.getMessenger();
    }

    @Override
    public Long getId() {
        return 1L;
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
        return nspResponse.getText();
    }

    @Override
    public boolean hasAttachments() {
        return false;
    }

    @Override
    public Attachment[] getAttachments() {
        //TODO: is there a case for an Attachment?
        return null;
    }
}
