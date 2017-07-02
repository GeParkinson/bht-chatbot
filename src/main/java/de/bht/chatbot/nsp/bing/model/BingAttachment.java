package de.bht.chatbot.nsp.bing.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingAttachment implements Attachment {

    /** generated unique id */
    private Long id;
    /** URI where attachment is stored */
    private String fileURI;
    /** caption of that attachment */
    private String caption;

    /**
     * Constructor
     * @param id generated unique id
     * @param fileURI URI where attachment is stored
     */
    public BingAttachment(final Long id, final String fileURI){
        this.id = id;
        this.caption = caption;
        this.fileURI = fileURI;
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
