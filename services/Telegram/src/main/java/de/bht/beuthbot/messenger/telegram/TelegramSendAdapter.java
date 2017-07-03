package de.bht.beuthbot.messenger.telegram;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVoice;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.BotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;

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

    /** slf4j Logger */
    private Logger logger = LoggerFactory.getLogger(TelegramSendAdapter.class);

    /** com.pengrad.telegrambot.TelegramBot; */
    private TelegramBot bot;

    /** BeuthBot Application Bean */
    @Inject
    private Application application;

    /**
     * Initialize TelegramBot with Token
     */
    @PostConstruct
    public void startUp(){
        bot = TelegramBotAdapter.build(application.getConfiguration(Configuration.TELEGRAM_BOT_TOKEN));

        //verifyWebhook();
    }

    /**
     * Method to async set TelegramWebhook
     */
    private void verifyWebhook() {
        SetWebhook webhook = new SetWebhook().url(application.getConfiguration(Configuration.TELEGRAM_WEBHOOK_URL));

        bot.execute(webhook, new Callback<SetWebhook, BaseResponse>() {
            @Override
            public void onResponse(final SetWebhook request, final BaseResponse response) {
                logger.debug("No errors while setting Telegram webhook.");
                //TODO: Check if webhook is really set
            }
            @Override
            public void onFailure(final SetWebhook request, final IOException e) {
                logger.warn("An Error occured while setting Telegram webhook. BOT_TOKEN: " + application.getConfiguration(Configuration.TELEGRAM_WEBHOOK_URL) + " - WEBHOOK_URL: " + application.getConfiguration(Configuration.TELEGRAM_BOT_TOKEN));
            }
        });
    }

    /**
     * Process JMS Message
     * @param message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
            //TODO: remove and make sure Bot is initialized
            startUp();
            if (botMessage.hasAttachments()){
                for (Attachment attachment : botMessage.getAttachments()) {
                    switch (attachment.getAttachmentType()) {
                        case AUDIO:
                            sendAudio(botMessage);
                            break;
                        case VOICE:
                            sendVoice(botMessage);
                            break;
                        case UNKOWN:
                            sendMessage(botMessage.getSenderID(), "Sorry! Can't process this AttachmentType.");
                        default:
                            sendMessage(botMessage.getSenderID(), "UNKNOWN ATTACHMENT!");
                            logger.info("new OutMessage has Attachements but no defined AttachmentType Case.");
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

    /**
     * Send Telegram Text message
     * @param senderId
     * @param message
     */
    private void sendMessage(final Long senderId, final String message) {
        SendMessage request = new SendMessage(senderId, message);
        SendResponse sendResponse = bot.execute(request);
        logger.debug("Send de.bht.chatbot.message: " + sendResponse.isOk());
    }

    /**
     * Send Telegram Audio message
     * @param botMessage
     */
    private void sendAudio(final BotMessage botMessage){
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

    /**
     * Send Telegram Voice message
     * @param botMessage
     */
    private void sendVoice(final BotMessage botMessage){
        SendVoice request;

        for (Attachment attachment : botMessage.getAttachments()) {
            request = new SendVoice(botMessage.getSenderID(), attachment.getFileURI());

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            logger.debug("Send Voice: " + sendResponse.isOk());
        }
    }
}
