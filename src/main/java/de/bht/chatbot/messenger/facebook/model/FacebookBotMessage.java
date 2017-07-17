package de.bht.chatbot.messenger.facebook.model;

import de.bht.chatbot.attachments.AttachmentService;
import de.bht.chatbot.attachments.AttachmentStore;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.message.Messenger;

/**
 * Created by oliver on 15.06.2017.
 * <p>
 * Facebook-specific class of the BotMessage Interface
 */
public class FacebookBotMessage implements BotMessage {

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
    public Attachment[] getAttachments() {

        if (hasAttachments()) {
            Attachment[] atts = new Attachment[facebookEntry.getMessaging().get(0).getMessage().getAttachments().size()];

            for (int i = 0; i < facebookEntry.getMessaging().get(0).getMessage().getAttachments().size(); i++) {
                atts[i] = facebookEntry.getMessaging().get(0).getMessage().getAttachments().get(i);
            }

            return atts;
        } else {
            return new Attachment[0];
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
}
