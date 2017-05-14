package Messenger.Telegram;

import Message.*;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by Chris on 5/14/2017.
 */

@Path("/telegram")
public class TelegramAdapter {

    @Inject
    TelegramBotService bot;

    @GET
    @Path("/verifyWebhook")
    public void verifyWebhook(){ bot.verifyWebhook(); }

    @POST
    @Path("/getUpdates")
    public void getUpdates(String msg) {
        Update update = BotUtils.parseUpdate(msg);
        com.pengrad.telegrambot.model.Message message = update.message();

        System.out.println("Got new Message from: " + message.from() + " - " + message.text());

        // respons msg = request msg  - testcase
        sendMessage(message);

        // telegramMessageToMessage(message);
    }

    public void sendMessage(com.pengrad.telegrambot.model.Message msg) {
        SendMessage request = new SendMessage(msg.chat().id(),msg.text());

        SendResponse sendResponse = bot.getBot().execute(request);
        System.out.println("Send Message: " + sendResponse.isOk());
    }

    private static Message telegramMessageToMessage(com.pengrad.telegrambot.model.Message message){
        Message msg = new Message();

        //TODO: Check IDs - String or LONG?
        msg.setText(message.text());
        msg.setMessenger(Messenger.TELEGRAM);
        msg.setMessageID(message.messageId().toString());
        msg.setSenderID(message.chat().id().toString());

        return msg;
    }
}
