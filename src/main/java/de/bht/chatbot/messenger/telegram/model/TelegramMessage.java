package de.bht.chatbot.messenger.telegram.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Message;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;

/**
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class TelegramMessage implements BotMessage{

    private Message message;

    private Attachment[] attachments;

    public TelegramMessage(Message message){
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
    public boolean hasAttachements() {
        return (attachments != null);
    }

    @Override
    public Attachment[] getAttachements() {
        return attachments;
    }

    public void setAttachments(final Attachment[] attachments){
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
