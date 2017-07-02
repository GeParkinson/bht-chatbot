package de.bht.chatbot.messenger.telegram.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;

import java.util.Random;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class TelegramAttachment implements Attachment {

    /** generated unique id */
    private Long id;
    /** URI where attachment is stored */
    private String fileURI;
    /** type of Attachment e.g. AUDIO, VIDEO, .. */
    private AttachmentType attachmentType;
    /** caption of that attachment */
    private String caption;

    /**
     * Constructor
     * @param id generated unique id
     * @param attachmentType type of Attachment e.g. AUDIO, VIDEO, ..
     * @param caption caption of that attachment
     */
    public TelegramAttachment(final Long id, final AttachmentType attachmentType, final String caption){
        this.attachmentType = attachmentType;
        this.caption = caption;
        this.id = id;
    }

    /**
     * Constructor
     * @param attachmentType type of Attachment e.g. AUDIO, VIDEO, ..
     */
    public TelegramAttachment(final AttachmentType attachmentType){
        this.attachmentType = attachmentType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getFileURI() {
        return fileURI;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    @Override
    public String getCaption() {
        return caption;
    }

}
