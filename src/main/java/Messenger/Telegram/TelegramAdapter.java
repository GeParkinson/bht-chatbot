package messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
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
        Message message = update.message();
        System.out.println("Got new message from: " + message.from());

        //TODO: process message

        // sendMessage(message);
    }

    /** Send message Types */

    public void sendMessage(Message msg) {
        SendMessage request = new SendMessage(msg.chat().id(),msg.text());
        SendResponse sendResponse = bot.getBot().execute(request);
        System.out.println("Send message: " + sendResponse.isOk());
    }
    public void sendPhoto(Message msg){
        SendPhoto request = new SendPhoto(msg.chat().id(), msg.photo()[0].fileId());
        SendResponse sendResponse = bot.getBot().execute(request);
        System.out.println("Send Photo: " + sendResponse.isOk());
    }
    public void sendAudio(Message msg){
        SendAudio request = new SendAudio(msg.chat().id(), msg.audio().fileId());
        SendResponse sendResponse = bot.getBot().execute(request);
        System.out.println("Send Audio: " + sendResponse.isOk());
    }
}
