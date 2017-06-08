package nlu.apiai;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import message.BotMessage;
import messenger.utils.MessengerUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Properties;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
@MessageDriven(
        name = "ApiAiConnector",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "Telegram = 'in'")
        }
)
public class ApiAiConnector implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(ApiAiConnector.class);


    @PostConstruct
    public void init() {
        // TODO: implementation
    }


    @Override
    public void onMessage(final Message message) {
        // TODO: fix sample code
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);

            String sessionID = "";
            String[] contexts = new String[]{};

            Properties properties = MessengerUtils.getProperties();
            String token = properties.getProperty("API_AI_TOKEN");

            String language = "en";

            String reqURL = String.format("https://api.api.ai/api/query?query=%s&lang=%s&sessionId=%s", botMessage.getText(), language, sessionID);

            for (String context : contexts) {
                reqURL = reqURL + "&contexts=" + context;
            }

            //TODO: Replace Unirest with RESTeasy
            JSONObject resultJson = Unirest.get(reqURL).header("Authorization", "Bearer " + token).asJson().getBody().getObject();

            resultJson.getJSONObject("result");
        } catch (JMSException e) {
            logger.error("Could not process message.", e);
        } catch (UnirestException e) {
            logger.error("Request failed!", e);
        }
    }
}
