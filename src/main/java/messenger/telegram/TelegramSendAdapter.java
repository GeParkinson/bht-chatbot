package messenger.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import message.Attachment;
import message.FileType;
import messenger.utils.MessengerUtils;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
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

    private static TelegramBot bot;

    @PostConstruct
    public void startUp(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));

        verifyWebhook();
    }

    private void verifyWebhook() {
        Properties properties = MessengerUtils.getProperties();
        SetWebhook webhook = new SetWebhook().url(properties.getProperty("TELEGRAM_WEBHOOK_URL"));
        BaseResponse response = bot.execute(webhook);

        if(!response.isOk()) {
            int count = 0;
            int maxTries = 3;
            while (!response.isOk()){
                response = bot.execute(webhook);
                if (count++ <= maxTries) continue;
            }
            verifyWebhook();
        }
        System.out.print("Webhook set: " + response);
    }

    @Override
    public void onMessage(Message message) {
        // TODO Chris: refactoring/implementing message sending
    }

    private void sendMessage(message.Message message){
        switch(message.getAttachements()[0].getAttachmentType()){
            case AUDIO:
                sendAudio(message);
                break;
            case VOICE:
                sendVoice(message);
                break;
            case VIDEO:
                sendVideo(message);
                break;
            case DOCUMENT:
                sendDocument(message);
                break;
            case PHOTO:
                sendPhoto(message);
                break;
            default:
                sendMessage(message.getMessageID(), message.getText());
                break;
        }
    }

    /** Send Text Message */
    private void sendMessage(Long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message);
        SendResponse sendResponse = bot.execute(request);
        System.out.println("Send message: " + sendResponse.isOk());
    }

    /** Send Photo Method */
    private void sendPhoto(message.Message message){
        SendPhoto request;

        // check & send each attachement
        for (Attachment attachment : message.getAttachements()){
            /** distinguish between FileTypes */
            if (attachment.getFileType() == FileType.FILE) request = new SendPhoto(message.getSenderID(), attachment.getFile());
            else if (attachment.getFileType() == FileType.BYTE) request = new SendPhoto(message.getSenderID(), attachment.getFileArray());
            else if (attachment.getFileType() == FileType.FILE_ID) request = new SendPhoto(message.getSenderID(), attachment.getFileUrl());
            else continue;

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            System.out.println("Send Photo: " + sendResponse.isOk());
        }
    }

    /** Send Audio Method */
    private void sendAudio(message.Message message){
        SendAudio request;

        // check & send each attachement
        for (Attachment attachment : message.getAttachements()) {
            /** distinguish between FileTypes */
            if(attachment.getFileType() == FileType.FILE) request = new SendAudio(message.getSenderID(), attachment.getFile());
            else if(attachment.getFileType() == FileType.BYTE) request = new SendAudio(message.getSenderID(), attachment.getFileArray());
            else if(attachment.getFileType() == FileType.FILE_ID) request = new SendAudio(message.getSenderID(), attachment.getFileUrl());
            else continue;

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());
            if (attachment.getDuration() != null)
                request.duration(attachment.getDuration());

            //TODO: performer & title

            SendResponse sendResponse = bot.execute(request);
            System.out.println("Send Audio: " + sendResponse.isOk());
        }
    }

    /** Send Voice Method */
    private void sendVoice(message.Message message){
        SendVoice request;

        for (Attachment attachment : message.getAttachements()) {
            /** distinguish between FileTypes */
            if (attachment.getFileType() == FileType.FILE) request = new SendVoice(message.getSenderID(), attachment.getFile());
            else if (attachment.getFileType() == FileType.BYTE) request = new SendVoice(message.getSenderID(), attachment.getFileArray());
            else if (attachment.getFileType() == FileType.FILE_ID) request = new SendVoice(message.getSenderID(), attachment.getFileUrl());
            else continue;

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());
            if (attachment.getDuration() != null)
                request.duration(attachment.getDuration());

            SendResponse sendResponse = bot.execute(request);
            System.out.println("Send Voice: " + sendResponse.isOk());
        }
    }

    /** Send Document Method */
    private void sendDocument(message.Message message){
        SendDocument request;

        for (Attachment attachment : message.getAttachements()) {
            /** distinguish between FileTypes */
            if (attachment.getFileType() == FileType.FILE) request = new SendDocument(message.getSenderID(), attachment.getFile());
            else if (attachment.getFileType() == FileType.BYTE) request = new SendDocument(message.getSenderID(), attachment.getFileArray());
            else if (attachment.getFileType() == FileType.FILE_ID) request = new SendDocument(message.getSenderID(), attachment.getFileUrl());
            else continue;

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());

            SendResponse sendResponse = bot.execute(request);
            System.out.println("Send Voice: " + sendResponse.isOk());
        }
    }

    /** Send Video Method */
    private void sendVideo(message.Message message){
        SendVideo request;

        for (Attachment attachment : message.getAttachements()) {
            /** distinguish between FileTypes */
            if (attachment.getFileType() == FileType.FILE) request = new SendVideo(message.getSenderID(), attachment.getFile());
            else if (attachment.getFileType() == FileType.BYTE) request = new SendVideo(message.getSenderID(), attachment.getFileArray());
            else if (attachment.getFileType() == FileType.FILE_ID) request = new SendVideo(message.getSenderID(), attachment.getFileUrl());
            else continue;

            if (attachment.getCaption() != null)
                request.caption(attachment.getCaption());
            if (attachment.getDuration() != null)
                request.duration(attachment.getDuration());

            //TODO: width & heigth

            SendResponse sendResponse = bot.execute(request);
            System.out.println("Send Voice: " + sendResponse.isOk());
        }
    }
}
