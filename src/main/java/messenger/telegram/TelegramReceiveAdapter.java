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
import messenger.telegram.model.TelegramAttachment;
import messenger.telegram.model.TelegramMessage;
import messenger.utils.MessengerUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
        TelegramMessage message = new TelegramMessage(update.message());
        message.setTelegramAttachments(addAttachments(update.message()));
        messageQueue.addInMessage(message);
    }

    private TelegramAttachment[] addAttachments(Message message) {
        startUp();
        TelegramAttachment[] telegramAttachments = new TelegramAttachment[1];

        String fileID = null;
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

            if (message.audio() != null) telegramAttachments[0] = new TelegramAttachment(fullPath,AttachmentType.AUDIO,message.caption());
            if (message.video() != null) telegramAttachments[0] = new TelegramAttachment(fullPath,AttachmentType.VIDEO,message.caption());
            if (message.voice() != null) telegramAttachments[0] = new TelegramAttachment(fullPath,AttachmentType.VOICE,message.caption());
            if (message.document() != null) telegramAttachments[0] = new TelegramAttachment(fullPath,AttachmentType.DOCUMENT,message.caption());
            return telegramAttachments;
        }
        return null;
    }
}
