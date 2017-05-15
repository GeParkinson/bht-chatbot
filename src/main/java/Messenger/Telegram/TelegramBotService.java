package Messenger.Telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import java.io.*;
import java.util.Properties;

/**
 * Created by Chris on 5/14/2017.
 */
public class TelegramBotService {

    private TelegramBot bot;

    public TelegramBotService(){
        Properties properties = getProperties();
        bot = TelegramBotAdapter.build(properties.getProperty("BOT_TOKEN"));
    }

    public TelegramBot getBot(){
        return bot;
    }

    public void verifyWebhook() {
        Properties properties = getProperties();
        SetWebhook webhook = new SetWebhook().url(properties.getProperty("WEBHOOK_URL"));
        BaseResponse response = bot.execute(webhook);

        boolean ok = response.isOk();
        System.out.println("Set Webhook: " + ok);
    }

    private Properties getProperties(){
        Properties properties = new Properties();

        InputStream inputStream = null;
        try {
            inputStream = TelegramBotService.class.getResourceAsStream("/config.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
