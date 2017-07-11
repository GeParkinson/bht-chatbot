package de.bht.chatbot.messenger.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendVoice;
import com.pengrad.telegrambot.response.SendResponse;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Properties;

/**
 * @Author: Christopher Kümmel on 5/22/2017.
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
    private final Logger logger = LoggerFactory.getLogger(TelegramSendAdapter.class);

    /** com.pengrad.telegrambot.TelegramBot; */
    private TelegramBot bot;

    /**
     * Constructor: Initialize TelegramBot with Token
     */
    public TelegramSendAdapter(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));
    }


    /**
     * Process JMS Message
     * @param message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
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
                            sendMessage(botMessage.getSenderID(), "Sorry! I'm just a bot and my developers just implemented audio and voice attachments..");
                            break;
                        default:
                            sendMessage(botMessage.getSenderID(), "Sorry! I'm just a bot and my developers just implemented audio and voice attachments..");
                            logger.info("new OutMessage has Attachments but no defined AttachmentType Case.");
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
     * @param senderId Telegram Sender ID
     * @param message to send
     */
    private void sendMessage(final Long senderId, final String message) {
        SendMessage request = new SendMessage(senderId, message);
        SendResponse sendResponse = bot.execute(request);
        logger.debug("Send de.bht.chatbot.message: " + sendResponse.isOk());
    }

    /**
     * Send Telegram Audio message
     * @param botMessage to send
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
     * @param botMessage to send
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
