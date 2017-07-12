package de.bht.beuthbot.jms;

import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 10.07.17
 */
public interface ProcessQueueMessageProtocol extends Serializable{
    Long getId();

    Target getTarget();

    Long getMessageID();

    Long getSenderID();

    Messenger getMessenger();

    String getText();

    boolean hasAttachments();

    List<Attachment> getAttachments();

    String getIntent();

    Map<String, String> getEntities();
}
