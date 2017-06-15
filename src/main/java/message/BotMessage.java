package message;

import attachments.AttachmentStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import messenger.telegram.model.TelegramAttachment;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by Chris on 5/14/2017.
 */
public interface BotMessage extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Long getMessageID();

    public void setMessageID(Long messageID);

    public Long getSenderID();

    public void setSenderID(Long senderID);

    public Messenger getMessenger();

    public void setMessenger(Messenger messenger);

    public String getText();

    public void setText(String text);

    public boolean hasAttachements();

    public TelegramAttachment[] getAttachements();

    public void setAttachements(final TelegramAttachment[] attachements);

}
