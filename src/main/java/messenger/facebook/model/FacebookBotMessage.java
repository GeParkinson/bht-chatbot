package messenger.facebook.model;

import message.Attachment;
import message.BotMessage;
import message.Messenger;

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
        return stringBuilder.toString();//facebookEntry.getMessaging().get(0).getMessage().getText();
    }

    @Override
    public boolean hasAttachements() {
        return false;
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
