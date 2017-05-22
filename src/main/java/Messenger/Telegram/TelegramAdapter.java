package messenger.telegram;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import message.Messenger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by Chris on 5/14/2017.
 */

@Path("/telegram")
public class TelegramAdapter {

    @POST
    @Path("/getUpdates")
    public void getUpdates(String msg) {
        Update update = BotUtils.parseUpdate(msg);
        Message message = update.message();
        System.out.println("Got new message from: " + message.from());

        //TODO: process message
    }

    private message.Message toMessage(com.pengrad.telegrambot.model.Message message) {
        message.Message msg = new message.Message();

        //TODO: Check IDs - String or LONG?
        msg.setText(message.text());
        msg.setMessenger(Messenger.TELEGRAM);
        msg.setMessageID(message.messageId().longValue());
        msg.setSenderID(message.chat().id());

        return msg;
    }
}
