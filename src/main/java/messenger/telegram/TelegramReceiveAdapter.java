package messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import jms.MessageQueue;
import message.*;
import messenger.utils.MessengerUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Chris on 5/14/2017.
 */

@Path("/telegram")
public class TelegramReceiveAdapter {

    @Inject
    private MessageQueue messageQueue;

    private TelegramBot bot;

    @PostConstruct
    public void startUp(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));
    }

    @POST
    @Path("/getUpdates")
    public void getUpdates(String msg) {
        Update update = BotUtils.parseUpdate(msg);
        Message message = update.message();
        messageQueue.addInMessage(toMessage(message));
    }

    private BotMessage toMessage(Message message) {
        startUp();
        BotMessage msg = new BotMessage();

        msg.setMessenger(Messenger.TELEGRAM);
        msg.setMessageID(message.messageId().longValue());
        msg.setSenderID(message.chat().id());

        Attachment[] attachments = new Attachment[1];

        String fileID = null;
        if (message.text() != null) msg.setText(message.text());

        if (message.audio() != null) fileID = message.audio().fileId();
        if (message.video() != null) fileID = message.video().fileId();
        if (message.voice() != null) fileID = message.voice().fileId();
        if (message.document() != null) fileID = message.document().fileId();

        if(fileID != null) {
            // get download link
            GetFile request = new GetFile(fileID);
            GetFileResponse getFileResponse = bot.execute(request);

            File file = getFileResponse.file();
            String fullPath = bot.getFullFilePath(file);

            if (message.audio() != null) attachments[0] = new Attachment(fileID,AttachmentType.AUDIO,FileType.SERVER_URL,fullPath);
            if (message.video() != null) attachments[0] = new Attachment(fileID,AttachmentType.VIDEO,FileType.SERVER_URL,fullPath);
            if (message.voice() != null) attachments[0] = new Attachment(fileID,AttachmentType.VOICE,FileType.SERVER_URL,fullPath);
            if (message.document() != null) attachments[0] = new Attachment(fileID,AttachmentType.DOCUMENT,FileType.SERVER_URL,fullPath);
            msg.setAttachements(attachments);
        }


        return msg;
    }
}
