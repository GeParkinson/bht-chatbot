package de.bht.chatbot.nsp.bing.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingAttachment implements Attachment {

    private Long id;
    private String fileURI;
    private String caption;

    public BingAttachment(final Long id, final String fileURI){
        this.id = id;
        this.fileURI = fileURI;
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
        return AttachmentType.AUDIO;
    }

    @Override
    public String getCaption() {
        return caption;
    }
}
