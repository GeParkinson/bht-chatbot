package de.bht.beuthbot.messenger.telegram.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Message;
import de.bht.beuthbot.message.BotMessage;
import de.bht.beuthbot.message.Messenger;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class TelegramMessage implements BotMessage{

    /** com.pengrad.telegrambot.model.Message */
    private Message message;

    /** Attachment[] */
    private TelegramAttachment[] telegramAttachments;

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
        return message.text();
    }

    @Override
    public boolean hasAttachments() {
        return(telegramAttachments != null);
    }

    @Override
    public TelegramAttachment[] getAttachments() {
        return telegramAttachments;
    }

    /**
     * Setter for Attachment[]
     * @param attachments
     */
    public void setTelegramAttachments(TelegramAttachment[] telegramAttachments){
        this.telegramAttachments = telegramAttachments;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
