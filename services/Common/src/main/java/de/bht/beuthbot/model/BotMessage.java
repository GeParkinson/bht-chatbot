package de.bht.beuthbot.model;

import java.io.Serializable;

/**
 * Created by Chris on 5/14/2017.
 */
public interface BotMessage extends Serializable {

    public Long getId();

    public Long getMessageID();

    public Long getSenderID();

    public Messenger getMessenger();

    public String getText();

    public boolean hasAttachments();

    public Attachment[] getAttachments();
	
}
