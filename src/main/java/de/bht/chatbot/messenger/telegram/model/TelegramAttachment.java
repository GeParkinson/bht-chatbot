package de.bht.chatbot.messenger.telegram.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class TelegramAttachment implements Attachment {

    private Long id;
    private String fileURI;
    private AttachmentType attachmentType;
    private String caption;

    public TelegramAttachment(String fileURI, AttachmentType attachmentType, String caption){
        this.fileURI = fileURI;
        this.attachmentType = attachmentType;
        this.caption = caption;
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
