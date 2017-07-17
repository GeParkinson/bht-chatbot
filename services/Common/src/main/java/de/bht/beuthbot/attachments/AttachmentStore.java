package de.bht.beuthbot.attachments;

import de.bht.beuthbot.attachments.model.AttachmentStoreMode;
import de.bht.beuthbot.model.AttachmentType;

import javax.ejb.Remote;
import java.io.ByteArrayOutputStream;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 05.07.17
 */
@Remote
public interface AttachmentStore {
    Long storeAttachment(String fileURI, AttachmentType attachmentType);

    Long storeAttachment(ByteArrayOutputStream byteArrayOutputStream, AttachmentType attachmentType);

    String loadAttachmentPath(long attachmentID, AttachmentStoreMode attachmentStoreMode);
}
