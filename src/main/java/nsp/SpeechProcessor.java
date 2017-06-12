package nsp;

import jms.MessageQueue;
import message.Attachment;
import message.BotMessage;
import message.Messenger;
import messenger.utils.MessengerUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @Author: Christopher Kümmel on 6/12/2017.
 */
@MessageDriven(
        name = "OutboxSpeechProcessor",
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "javax.jms.Topic"),
                @ActivationConfigProperty(
                        propertyName = "destination",
                        propertyValue = "jms/messages/inbox"),
                @ActivationConfigProperty(
                        propertyName = "maxSession", propertyValue = "1"),
                @ActivationConfigProperty(
                        propertyName = "messageSelector", propertyValue = "SpeechProcessor = 'in'"
                )
        }
)
public class SpeechProcessor implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(SpeechProcessor.class);

    private String accessToken = "";
    private String locale = "";
    private String guid = "";

    @Inject
    MessageQueue messageQueue;

    @Override
    public void onMessage(Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
            for(Attachment attachment : botMessage.getAttachements()){
                //TODO: distinguish between different languages
                sendAudioRequest(attachment.getFileUrl(), "en-US", botMessage.getMessenger());
            }
        } catch (JMSException e) {
            logger.error("Error while getting BotMessage-Object on SpeechProcessor: " + e.toString());
        }
    }

    //TODO: generate Token at Systemstart -> at runtime every 9 minutes
    private void generateAccesToken(){
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
            HttpPost httpPost = new HttpPost(url);

            Properties properties = MessengerUtils.getProperties();
            locale = properties.getProperty("BING_SPEECH_LOCALE");
            guid = properties.getProperty("BING_SPEECH_GUID");

            // HEADERS
            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
            postHeaders.put("Ocp-Apim-Subscription-Key", properties.getProperty("BING_SPEECH_SECRET_KEY"));

            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            // send request
            HttpResponse response = client.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            logger.debug("AccessToken request returns: Response Code - " + String.valueOf(responseCode));

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            accessToken = result.toString();

            logger.debug("new AccessToken: " + accessToken);
        } catch (Exception e) {
            logger.error("Exception while getting new AccessToken: " + e.toString());
        }
    }

    private void sendAudioRequest(String fileDir, String language, Messenger messenger){
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language=" + language + "&locale=" + locale + "&requestid=" + guid;
            HttpPost httpPost = new HttpPost(url);

            // HEADERS
            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Authorization", "Bearer " + accessToken);
            postHeaders.put("Content-Type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000");

            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            // WAV FILE
            File file = new File(fileDir);
            FileBody bin = new FileBody(file, ContentType.DEFAULT_BINARY);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("bin", bin);
            HttpEntity reqEntity = builder.build();

            ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
            reqEntity.writeTo(bArrOS);
            bArrOS.flush();
            ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
            bArrOS.close();

            bArrEntity.setChunked(true);
            bArrEntity.setContentEncoding(reqEntity.getContentEncoding());
            bArrEntity.setContentType(reqEntity.getContentType());

            // Set ByteArrayEntity to HttpPost
            httpPost.setEntity(bArrEntity);

            // send request
            HttpResponse response = client.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            logger.debug("Send Audio request returns: Response Code - " + String.valueOf(responseCode));

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            logger.debug("Audio request returns: " + result.toString());

            // return message
            BotMessage emptyBotMessage = new BotMessage();
            emptyBotMessage.setId(1L);
            emptyBotMessage.setText(result.toString());
            emptyBotMessage.setMessenger(messenger);
            messageQueue.addInMessage(emptyBotMessage);

        } catch (Exception e){
            logger.error("Exception while audio request: " + e.toString());
        }
    }
}
