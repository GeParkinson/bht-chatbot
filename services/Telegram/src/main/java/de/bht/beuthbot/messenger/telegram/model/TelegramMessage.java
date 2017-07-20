package de.bht.beuthbot.messenger.telegram.model;

import com.pengrad.telegrambot.model.Message;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;
import de.bht.beuthbot.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class TelegramMessage implements ProcessQueueMessageProtocol{

    /** com.pengrad.telegrambot.model.Message */
    private Message message;

    /** Attachment[] */
    private List<Attachment> attachments;

    /**
     * Constructor
     * @param message com.pengrad.telegrambot.model.Message
     */
    public TelegramMessage(final Message message){
        this.message = message;
    }

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public Target getTarget() {
        return Target.NTSP;
    }

    @Override
    public Long getMessageID() {
        return Long.valueOf(message.messageId());
    }

    @Override
    public Long getSenderID() {
        return message.chat().id();
    }

    @Override
    public Messenger getMessenger() {
        return Messenger.TELEGRAM;
    }

    @Override
    public String getText() {
        if (message.text() != null) return message.text();
        else return "";
    }

    @Override
    public boolean hasAttachments() {
        return (attachments != null);
    }

    @Override
    public List<Attachment> getAttachments() {
        if (hasAttachments()) {
            return attachments;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getIntent() {
        return null;
    }

    @Override
    public Map<String, String> getEntities() {
        return Collections.emptyMap();
    }

    /**
     * Setter for Attachment[]
     * @param attachments to store
     */
    public void setAttachments(final Attachment[] attachments){
        if (attachments != null) {
            this.attachments = new ArrayList<>(attachments.length);
            for (Attachment attachment : attachments) {
                this.attachments.add(attachment);
            }
        }
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
