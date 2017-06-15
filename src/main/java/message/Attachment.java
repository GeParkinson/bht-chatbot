package message;

import java.io.File;
import java.io.Serializable;

/**
 * @Author: Christopher Kümmel on 5/14/2017.
 */
public interface Attachment extends Serializable{
    public Long getId();

    public void setId(Long id);

    public String getFileURI();

    public void setFileURI(String fileUrl);

    public AttachmentType getAttachmentType();

    public void setAttachmentType(AttachmentType attachmentType);

    public String getCaption();

    public void setCaption(String caption);
}
