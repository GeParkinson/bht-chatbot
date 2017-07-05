package de.bht.beuthbot.messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.MessageQueue;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.AttachmentType;
import de.bht.beuthbot.messenger.telegram.model.TelegramAttachment;
import de.bht.beuthbot.messenger.telegram.model.TelegramMessage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @Author: Christopher Kümmel on 5/14/2017.
 */

@Path("/telegram")
public class TelegramReceiveAdapter {

    /** Injected JMS MessageQueue */
    @Inject
    private MessageQueue messageQueue;

    /** BeuthBot Application Bean */
    @Inject
    private Application application;

    /** Injected AttachmentStore */
    @Inject
    private AttachmentStore attachmentStore;

    /** com.pengrad.telegrambot.TelegramBot; */
    private TelegramBot bot;

    /**
     * Initialize TelegramBot with Token
     */
    @PostConstruct
    public void startUp(){
        bot = TelegramBotAdapter.build(application.getConfiguration(Configuration.TELEGRAM_BOT_TOKEN));
    }

    /**
     * RESTEasy HTTP Post as Webhook Endpoint
     * @param msg Telegram Message
     */
    @POST
    @Path("/getUpdates")
    public void getUpdates(final String msg) {
        Update update = BotUtils.parseUpdate(msg);
        TelegramMessage message = new TelegramMessage(update.message());
        // TODO Chris: fix this
        // message.setAttachments(getAttachments(update.message()));
        messageQueue.addInMessage(message);
    }

    /**
     * Check and get for Attachments from com.pengrad.telegrambot.model.Update
     * @param message com.pengrad.telegrambot.model.Update
     * @return returns null if no Attachment available
     */
    private Attachment[] getAttachments(final Message message) {
        //TODO: remove and make sure Bot is initialized
        startUp();

        // check for attachments
        String fileID = null;
        if (message.audio() != null) fileID = message.audio().fileId();
        if (message.voice() != null) fileID = message.voice().fileId();


        if (fileID != null) {
            // get download link from Telegram
            GetFile request = new GetFile(fileID);
            GetFileResponse getFileResponse = bot.execute(request);

            File file = getFileResponse.file();
            String fullPath = bot.getFullFilePath(file);

            Long id;
            if (message.audio() != null) {
                id = attachmentStore.storeAttachment(fullPath, AttachmentType.AUDIO);
                TelegramAttachment[] telegramAttachments = {new TelegramAttachment(id, AttachmentType.AUDIO, message.caption())};
                return telegramAttachments;
            }
            if (message.voice() != null){
                id = attachmentStore.storeAttachment(fullPath, AttachmentType.VOICE);
                TelegramAttachment[] telegramAttachments = {new TelegramAttachment(id, AttachmentType.VOICE, message.caption())};
                return telegramAttachments;
            }
        }

        // "unkown" undefined attachments
        //TODO: process attachments
        boolean unknownType = false;
        if (message.video() != null) unknownType = true;
        if (message.contact() != null) unknownType = true;
        if (message.sticker() != null) unknownType = true;
        if (message.location() != null) unknownType = true;
        if (message.invoice() != null) unknownType = true;
        if (message.game() != null) unknownType = true;
        if (message.document() != null) unknownType = true;

        if (unknownType){
            TelegramAttachment[] telegramAttachments = {new TelegramAttachment(AttachmentType.UNKOWN)};
            return telegramAttachments;
        } else {
            return null;
        }
    }
}
