package messenger.telegram;

import message.*;
import message.Message;

/**
 * Created by Chris on 5/18/2017.
 */
public class MessageUtils {

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

