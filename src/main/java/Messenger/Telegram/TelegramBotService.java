package Messenger.Telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        //FIXME: not working - FileNotFoundException - include config.properties into build
        File propertiesFile = new File("./config.properties");
        Properties properties = new Properties();

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(propertiesFile));
            properties.load(bis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
