package de.bht.chatbot.nsp.bing.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;
import de.bht.chatbot.nsp.NSPResponse;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingMessage implements BotMessage{

    /** Display text */
    private String text;

    /** serial number */
    private Long messageID;

    /** unique sender id */
    private Long senderID;

    /** messenger type */
    private Messenger messenger;

    /** Attachment[] */
    private Attachment[] attachments;

    /**
     * Constructor
     * @param botMessage
     * @param attachment
     */
    public BingMessage(final BotMessage botMessage, final Attachment attachment){
        this.messageID = botMessage.getMessageID();
        this.senderID = botMessage.getSenderID();
        this.messenger = botMessage.getMessenger();
        this.attachments = new Attachment[]{attachment};
    }

    /**
     * Constructor
     * @param nspResponse
     * @param botMessage
     */
    public BingMessage(final NSPResponse nspResponse, final BotMessage botMessage){
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
    public boolean hasAttachments() {
        return (attachments != null);
    }

    @Override
    public Attachment[] getAttachments() {
        return attachments;
    }
}
