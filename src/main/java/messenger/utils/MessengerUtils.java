package messenger.utils;

import message.Message;
import messenger.telegram.TelegramSendAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Chris on 5/18/2017.
 */
public class MessengerUtils {

    /***
     * Static method to send a Message. Distinguishes between different messenger types based on MessengerEnum.
     * @param message to send
     */
    public static void sendMessage(Message message){
        switch(message.getMessenger()){
            case TELEGRAM:
                TelegramSendAdapter.sendMessage(message);
                break;
            case FACEBOOK:
                //sendMessage
                break;
        }
    }

    /***
     * Simple method to get properties in resources folder.
     * @return config.properties
     */
    public static Properties getProperties(){
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = MessengerUtils.class.getResourceAsStream("/config.properties");
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
