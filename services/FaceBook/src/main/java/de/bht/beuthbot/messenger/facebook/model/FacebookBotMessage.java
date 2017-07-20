package de.bht.beuthbot.messenger.facebook.model;

import de.bht.beuthbot.attachments.AttachmentStore;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.AttachmentType;
import de.bht.beuthbot.model.Messenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by oliver on 15.06.2017.
 * <p>
 * Facebook-specific class of the BotMessage Interface
 */

public class FacebookBotMessage implements ProcessQueueMessageProtocol {

    //stores a FacebookEntry object which contains all necessary information
    private FacebookEntry facebookEntry;

    public FacebookBotMessage(FacebookEntry facebookEntry) {

        this.facebookEntry = facebookEntry;

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
     *
     * @return Text containing all message-texts
     */
    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (FacebookMessaging facebookMessaging : facebookEntry.getMessaging()) {
            stringBuilder.append(facebookMessaging.getMessage().getText());
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean hasAttachments() {
        if (facebookEntry.getMessaging().get(0).getMessage().getAttachments() == null)
            return false;
        else return true;
    }

    /**
     * get all Attachments from the message object
     *
     * @return List of attachments found
     */
    @Override
    public List<Attachment> getAttachments() {


        if (hasAttachments()) {
            List<Attachment> atts = new ArrayList<>();

            for (int i = 0; i < facebookEntry.getMessaging().get(0).getMessage().getAttachments().size(); i++) {
                atts.add(facebookEntry.getMessaging().get(0).getMessage().getAttachments().get(i));
            }

            return atts;

        } else {
            return Collections.emptyList();
        }
    }


    public boolean isIncomingMessage() {
        //check if Message-node of BotMessage object exists
        if (facebookEntry.getMessaging().get(0).getMessage() != null) {
            // 'IsEcho' means whether the message is a new incoming message or just an 'echo' of a message the bot sends out
            if (!facebookEntry.getMessaging().get(0).getMessage().getIsEcho()) {
                return true;
            }
        }
        return false;
    }

    public void generateAttachmentID(AttachmentStore attachmentStore) {
        FacebookMessage facebookMessage = facebookEntry.getMessaging().get(0).getMessage();

        //if Attachment --> get ID via AttachmentStore and put it into attachment
        if (facebookMessage.getAttachments() != null) {
            FacebookAttachment att = facebookMessage.getAttachments().get(0);

            //at the moment just audio files are supported --> saved
            //TODO: add/support more attachment types
            if (att.getAttachmentType() == AttachmentType.AUDIO) {
                Long id = attachmentStore.storeAttachment(att.getFileURI(), att.getAttachmentType());
                facebookMessage.getAttachments().get(0).setID(id);
            }
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
}
