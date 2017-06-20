package message;

import java.io.File;
import java.io.Serializable;

/**
 * @Author: Christopher Kümmel on 5/14/2017.
 */
public interface Attachment extends Serializable{
    public Long getId();

    public String getFileURI();

    public AttachmentType getAttachmentType();

    public String getCaption();
}
