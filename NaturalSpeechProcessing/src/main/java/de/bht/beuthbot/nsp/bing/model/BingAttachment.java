package de.bht.beuthbot.nsp.bing.model;

import de.bht.beuthbot.message.Attachment;
import de.bht.beuthbot.message.AttachmentType;

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
     * @param id
     * @param fileURI
     */
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
