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

    public Long getMessageID();

    public Long getSenderID();

    public Messenger getMessenger();

    public String getText();

    public boolean hasAttachements();

    public Attachment[] getAttachements();

}
