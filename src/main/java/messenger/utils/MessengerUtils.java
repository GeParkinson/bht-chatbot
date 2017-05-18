package messenger.utils;

import messenger.telegram.TelegramBotService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Chris on 5/18/2017.
 */
public class MessengerUtils {

    public static Properties getProperties(){
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
