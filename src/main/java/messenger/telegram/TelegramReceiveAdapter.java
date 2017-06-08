package messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jms.MessageQueue;
import message.*;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;

/**
 * Created by Chris on 5/14/2017.
 */

@Path("/telegram")
public class TelegramReceiveAdapter {

    @Inject
    private MessageQueue messageQueue;

    @POST
    @Path("/getUpdates")
    public void getUpdates(String msg) {
        Update update = BotUtils.parseUpdate(msg);
        Message message = update.message();
        messageQueue.addInMessage(toMessage(message));
    }

    private BotMessage toMessage(Message message) {
        BotMessage msg = new BotMessage();

        msg.setMessenger(Messenger.TELEGRAM);
        msg.setMessageID(message.messageId().longValue());
        msg.setSenderID(message.chat().id());

        ArrayList<Attachment> attachments = new ArrayList<>();

        if (message.audio() != null)
            attachments.add(new Attachment(Long.valueOf(message.audio().fileId()), AttachmentType.AUDIO, FileType.FILE_ID));
        if (message.video() != null)
            attachments.add(new Attachment(Long.valueOf(message.video().fileId()), AttachmentType.VIDEO, FileType.FILE_ID));
        if (message.voice() != null)
            attachments.add(new Attachment(Long.valueOf(message.voice().fileId()), AttachmentType.VOICE, FileType.FILE_ID));
        if (message.document() != null)
            attachments.add(new Attachment(Long.valueOf(message.document().fileId()), AttachmentType.DOCUMENT, FileType.FILE_ID));
        if (message.text() != null) msg.setText(message.text());

        msg.setAttachements((Attachment[]) attachments.toArray());

        return msg;
    }
}
