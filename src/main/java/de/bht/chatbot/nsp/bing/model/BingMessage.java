package de.bht.chatbot.nsp.bing.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.nsp.NSPResponse;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingMessage implements BotMessage{

    private String text;
    private Long messageID;
    private Long senderID;
    private Messenger messenger;
    private Attachment[] attachments;

    public BingMessage(BotMessage botMessage, BingAttachment bingAttachment){
        this.messageID = botMessage.getMessageID();
        this.senderID = botMessage.getSenderID();
        this.messenger = botMessage.getMessenger();
        this.attachments = new Attachment[]{bingAttachment};
    }

    public BingMessage(NSPResponse nspResponse, BotMessage botMessage){
        this.text = nspResponse.getText();
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
        return text;
    }

    @Override
    public boolean hasAttachements() {
        return (attachments != null);
    }

    @Override
    public Attachment[] getAttachements() {
        return attachments;
    }
}
