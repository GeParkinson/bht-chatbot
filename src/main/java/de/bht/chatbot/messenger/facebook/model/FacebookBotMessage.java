package de.bht.chatbot.messenger.facebook.model;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;

/**
 * Created by oliver on 15.06.2017.
 *
 * Facebook-specific class of the BotMessage Interface
 */
public class FacebookBotMessage implements BotMessage{

    //stores a FacebookEntry object which contains all necessary information
    private FacebookEntry facebookEntry;

    public FacebookBotMessage(FacebookEntry facebookEntry){

        this.facebookEntry=facebookEntry;

    }

    @Override
    public Long getId() {
        //TODO: generate ID
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

    /**
     * due to the possibility that a FacebookEntry can contain multiply FacebookMessaging objects, iterate through all
     * @return Text containing all message-texts
     */
    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for(FacebookMessaging facebookMessaging : facebookEntry.getMessaging()) {
            stringBuilder.append(facebookMessaging.getMessage().getText());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean hasAttachments() {
        if(facebookEntry.getMessaging().get(0).getMessage().getAttachments()==null)
            return false;
        else return true;
    }

    /**
     * get all Attachments from the message object
     * @return List of attachments found
     */
    @Override
    public Attachment[] getAttachments() {

        if(hasAttachments()) {
            Attachment[] atts = new Attachment[facebookEntry.getMessaging().get(0).getMessage().getAttachments().size()];

            for (int i = 0; i < facebookEntry.getMessaging().get(0).getMessage().getAttachments().size(); i++) {
                atts[i] = facebookEntry.getMessaging().get(0).getMessage().getAttachments().get(i);
            }

            return atts;
        }
        else {
            return new Attachment[0];
        }
    }
}
