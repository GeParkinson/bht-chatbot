package de.bht.chatbot.messenger.facebook.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;

/**
 * Created by oliver on 15.06.2017.
 */
public class FacebookBotMessage implements BotMessage{

    private FacebookEntry facebookEntry;

    public FacebookBotMessage(FacebookEntry facebookEntry){

        this.facebookEntry=facebookEntry;

    }

    @Override
    public Long getId() {
        return 1L;
    }

    @Override
    public Long getMessageID() {
        return Long.valueOf(facebookEntry.getId());
    }

    @Override
    public Long getSenderID() {
        return Long.valueOf(facebookEntry.getMessaging().get(0).getSender().getId());
    }

    @Override
    public Messenger getMessenger() {
        return Messenger.FACEBOOK;
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for(FacebookMessaging facebookMessaging : facebookEntry.getMessaging()) {
            stringBuilder.append(facebookMessaging.getMessage().getText());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean hasAttachements() {
        if(facebookEntry.getMessaging().get(0).getMessage().getAttachments()==null)
            return false;
        else return true;
    }

    @Override
    public Attachment[] getAttachements() {

        Attachment[] atts=new Attachment[facebookEntry.getMessaging().get(0).getMessage().getAttachments().size()];

        for(int i=0; i<facebookEntry.getMessaging().get(0).getMessage().getAttachments().size(); i++)
        {
            atts[i]=facebookEntry.getMessaging().get(0).getMessage().getAttachments().get(i);
        }

        return atts;
    }
}
