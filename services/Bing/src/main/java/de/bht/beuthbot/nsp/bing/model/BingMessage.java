package de.bht.beuthbot.nsp.bing.model;

import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;
import de.bht.beuthbot.model.nsp.NSPResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingMessage implements ProcessQueueMessageProtocol{

    /** Display text */
    private String text;

    /** serial number */
    private Long messageID;

    /** unique sender id */
    private Long senderID;

    /** messenger type */
    private Messenger messenger;

    /** Attachment[] */
    private List<Attachment> attachments;

    /**
     * Constructor
     * @param botMessage
     * @param attachment
     */
    public BingMessage(final ProcessQueueMessageProtocol botMessage, final Attachment attachment){
        this.messageID = botMessage.getMessageID();
        this.senderID = botMessage.getSenderID();
        this.messenger = botMessage.getMessenger();
        this.attachments = new ArrayList<>(1);
        this.attachments.add(attachment);
    }

    /**
     * Constructor
     * @param nspResponse
     * @param botMessage
     */
    public BingMessage(final NSPResponse nspResponse, final ProcessQueueMessageProtocol botMessage){
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
    public Target getTarget() {
        return Target.NTSP;
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
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Override
    public String getIntent() {
        return null;
    }

    @Override
    public Map<String, String> getEntities() {
        return null;
    }
}
