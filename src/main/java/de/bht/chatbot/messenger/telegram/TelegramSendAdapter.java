package de.bht.chatbot.messenger.telegram;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import de.bht.chatbot.message.*;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Chris on 5/22/2017.
 */
@MessageDriven(
    name = "OutboxTelegramProcessor",
    activationConfig = {
            @ActivationConfigProperty(
                    propertyName = "destinationType",
                    propertyValue = "javax.jms.Topic"),
            @ActivationConfigProperty(
                    propertyName = "destination",
                    propertyValue = "jms/messages/inbox"),
            @ActivationConfigProperty(
                    propertyName = "maxSession", propertyValue = "1"),
            @ActivationConfigProperty(
                    propertyName = "messageSelector", propertyValue = "Telegram = 'out'"
            )
    }
)
public class TelegramSendAdapter implements MessageListener {

    private Logger logger = LoggerFactory.getLogger(TelegramSendAdapter.class);

    private TelegramBot bot;

    @PostConstruct
    public void startUp(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));

        //verifyWebhook();
    }

    private void verifyWebhook() {
        Properties properties = MessengerUtils.getProperties();
        SetWebhook webhook = new SetWebhook().url(properties.getProperty("TELEGRAM_WEBHOOK_URL"));

        bot.execute(webhook, new Callback<SetWebhook, BaseResponse>() {
            @Override
            public void onResponse(SetWebhook request, BaseResponse response) {
                logger.debug("No errors while setting Telegram webhook.");
                //TODO: Check if webhook is really set
            }
            @Override
            public void onFailure(SetWebhook request, IOException e) {
                logger.warn("An Error occured while setting Telegram webhook. BOT_TOKEN: " + properties.getProperty("TELEGRAM_BOT_TOKEN") + " - WEBHOOK_URL: " + properties.getProperty("TELEGRAM_WEBHOOK_URL"));
            }
        });
    }

    @Override
    public void onMessage(Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
            startUp();
            if (botMessage.hasAttachments()){
                for(Attachment attachment : botMessage.getAttachments()) {
                    switch (attachment.getAttachmentType()) {
                        case AUDIO:
                            sendAudio(botMessage);
                            break;
                        case VOICE:
                            sendVoice(botMessage);
                            break;
                        case VIDEO:
                            sendVideo(botMessage);
                            break;
                        case DOCUMENT:
                            sendDocument(botMessage);
                            break;
                        case PHOTO:
                            sendPhoto(botMessage);
                            break;
                        default:
                            logger.warn("new OutMessage has Attachements but no defined AttachementType Case.");
                            break;
                    }
                }
            } else {
                sendMessage(botMessage.getSenderID(), botMessage.getText());
            }
        }
        catch (JMSException e) {
            logger.error("Error on receiving BotMessage-Object on TelegramSendAdapter: ", e);
        }
    }

    /** Send Text BotMessage */
    private void sendMessage(Long senderId, String message) {
        SendMessage request = new SendMessage(senderId, message);
        SendResponse sendResponse = bot.execute(request);
        logger.debug("Send de.bht.chatbot.message: " + sendResponse.isOk());
    }

    /** Send Photo Method */
    private void sendPhoto(BotMessage botMessage){
        SendPhoto request;

        // check & send each attachement
        for (Attachment attachment : botMessage.getAttachments()){
            request = new SendPhoto(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Photo: " + sendResponse.isOk());
        }
    }

    /** Send Audio Method */
    private void sendAudio(BotMessage botMessage){
        SendAudio request;

        // check & send each attachement
        for (Attachment attachment : botMessage.getAttachments()) {
            request = new SendAudio(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Audio: " + sendResponse.isOk());
        }
    }

    /** Send Voice Method */
    private void sendVoice(BotMessage botMessage){
        SendVoice request;

        for (Attachment attachment : botMessage.getAttachments()) {
            request = new SendVoice(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Voice: " + sendResponse.isOk());
        }
    }

    /** Send Document Method */
    private void sendDocument(BotMessage botMessage){
        SendDocument request;

        for (Attachment attachment : botMessage.getAttachments()) {
            request = new SendDocument(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Voice: " + sendResponse.isOk());
        }
    }

    /** Send Video Method */
    private void sendVideo(BotMessage botMessage){
        SendVideo request;

        for (Attachment attachment : botMessage.getAttachments()) {
            request = new SendVideo(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Voice: " + sendResponse.isOk());
        }
    }
}
