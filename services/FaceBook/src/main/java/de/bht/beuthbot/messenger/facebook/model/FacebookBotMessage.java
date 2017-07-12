package de.bht.beuthbot.messenger.facebook.model;

import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by oliver on 15.06.2017.
 *
 * Facebook-specific class of the BotMessage Interface
 */
public class FacebookBotMessage implements ProcessQueueMessageProtocol{

    //stores a FacebookEntry object which contains all necessary information
    private FacebookEntry facebookEntry;

    public FacebookBotMessage(FacebookEntry facebookEntry){

        this.facebookEntry=facebookEntry;

    }

    @Override
    public Long getId() {
        //TODO: generate Chatbot-intern-ID
        return 1L;
    }

    @Override
    public Target getTarget() {
        return Target.NTSP;
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
    public List<Attachment> getAttachments() {

        if(hasAttachments()) {
            List<Attachment> atts = new ArrayList<>();

            for (int i = 0; i < facebookEntry.getMessaging().get(0).getMessage().getAttachments().size(); i++) {
                atts.add(facebookEntry.getMessaging().get(0).getMessage().getAttachments().get(i));
            }

            return atts;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getIntent() {
        return null;
    }

    @Override
    public Map<String, String> getEntities() {
        return null;
    }
}
