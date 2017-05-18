package messenger.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import messenger.utils.MessengerUtils;

import java.io.*;
import java.util.Properties;

/**
 * Created by Chris on 5/14/2017.
 */
public class TelegramBotService {

    private TelegramBot bot;

    public TelegramBotService(){
        Properties properties = MessengerUtils.getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("TELEGRAM_BOT_TOKEN"));
    }

    public TelegramBot getBot(){
        return bot;
    }

    public void verifyWebhook() {
        Properties properties = MessengerUtils.getProperties();
        SetWebhook webhook = new SetWebhook().url(properties.getProperty("TELEGRAM_WEBHOOK_URL"));
        BaseResponse response = bot.execute(webhook);

        boolean ok = response.isOk();
        System.out.println("Set Webhook: " + ok);
    }
}
