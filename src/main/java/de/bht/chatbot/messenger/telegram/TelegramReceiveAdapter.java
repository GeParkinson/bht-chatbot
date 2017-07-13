package de.bht.chatbot.messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetFileResponse;
import de.bht.chatbot.attachments.AttachmentStore;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.AttachmentType;
import de.bht.chatbot.messenger.telegram.model.TelegramAttachment;
import de.bht.chatbot.messenger.telegram.model.TelegramMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Properties;

/**
 * @Author: Christopher Kümmel on 5/14/2017.
 */

@Path("/telegram")
public class TelegramReceiveAdapter {

    /** slf4j Logger */
    private final Logger logger = LoggerFactory.getLogger(TelegramReceiveAdapter.class);

    /** Injected JMS MessageQueue */
    @Inject
    private MessageQueue messageQueue;

    /** Injected AttachmentStore */
    @Inject
    private AttachmentStore attachmentStore;

    /** com.pengrad.telegrambot.TelegramBot; */
    private TelegramBot bot;

    /**
     * Constructor: Initialize TelegramBot with Token
     */
    public TelegramReceiveAdapter(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));
    }

    /**
     * RESTEasy HTTP Post as Webhook Endpoint
     * @param msg Telegram Message
     */
    @POST
    @Path("/getUpdates")
    public Response getUpdates(final String msg) {
        Update update = BotUtils.parseUpdate(msg);
        logger.debug("Received new Telegram message: {}", update);
        TelegramMessage message = new TelegramMessage(update.message());
        message.setAttachments(getAttachments(update.message()));
        messageQueue.addInMessage(message);
        return Response.status(HttpStatus.SC_OK).build();
    }

    /**
     * Check and get for Attachments from com.pengrad.telegrambot.model.Update
     * @param message com.pengrad.telegrambot.model.Update
     * @return returns null if no Attachment available
     */
    private Attachment[] getAttachments(final Message message) {

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
            logger.debug("Telegram attachment uri: {}", fullPath);

            Long id;
            if (message.audio() != null) {
                id = attachmentStore.storeAttachment(fullPath, AttachmentType.AUDIO);
                TelegramAttachment[] telegramAttachments = {new TelegramAttachment(id, AttachmentType.AUDIO, message.caption(), fullPath)};
                return telegramAttachments;
            }
            if (message.voice() != null){
                id = attachmentStore.storeAttachment(fullPath, AttachmentType.VOICE);
                TelegramAttachment[] telegramAttachments = {new TelegramAttachment(id, AttachmentType.VOICE, message.caption(), fullPath)};
                return telegramAttachments;
            }
        }

        // "unkown" undefined attachments
        boolean unknownType = false;
        if (fileID == null && message.text() == null) unknownType = true;

        if (unknownType){
            TelegramAttachment[] telegramAttachments = {new TelegramAttachment(AttachmentType.UNKOWN)};
            return telegramAttachments;
        } else {
            return null;
        }
    }


    /**
     * RESTEasy HTTP Post to set Webhook
     */
    @POST
    @Path("/setWebhook")
    public Response setWebhook() {
        int responseCode = verifyWebhook();
        return Response.status(responseCode).build();
    }

    /**
     * Method to set TelegramWebhook
     */
    private int verifyWebhook() {
        Properties properties = MessengerUtils.getProperties();
        SetWebhook webhook = new SetWebhook().url(properties.getProperty("WEB_URL") + properties.getProperty("TELEGRAM_WEBHOOK_URL"));

        BaseResponse response = bot.execute(webhook);
        if (response.isOk()) {
            logger.debug("Telegram Webhook was set.");
            return HttpStatus.SC_OK;
        } else {
            logger.warn("Telegram Webhook could not be set!");
            return HttpStatus.SC_METHOD_FAILURE;
        }
    }
}
