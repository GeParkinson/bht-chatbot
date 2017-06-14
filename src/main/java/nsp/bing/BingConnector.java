package nsp.bing;

import com.google.gson.Gson;
import jms.MessageQueue;
import message.Attachment;
import message.BotMessage;
import message.Messenger;
import messenger.utils.MessengerUtils;
import nsp.bing.model.BingDetailedResponse;
import nsp.bing.model.BingSimpleResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.*;

/**
 * @Author: Christopher Kümmel on 6/12/2017.
 */
@MessageDriven(
        name = "BingConnector",
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
                        propertyName = "messageSelector", propertyValue = "BingConnector = 'in'"
                )
        }
)
public class BingConnector implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(BingConnector.class);

    private String accessToken = "";
    private String locale = "";
    private String guid = "";
    //TODO: distinguish between formats (simple or detailed)
    private String format = "simple";

    @Inject
    MessageQueue messageQueue;

    @Override
    public void onMessage(Message message) {
        try {
            BotMessage botMessage = message.getBody(BotMessage.class);
            for(Attachment attachment : botMessage.getAttachements()){
                //TODO: distinguish between different languages
                sendAudioRequest(attachment.getFileUrl(), "en-US", botMessage.getMessenger(), botMessage.getSenderID());
            }
        } catch (JMSException e) {
            logger.error("Error on receiving BotMessage-Object on BingConnector: ", e);
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

            if (responseCode == 200) {
                accessToken = result.toString();
                logger.debug("new AccessToken: " + accessToken);
            } else {
                logger.warn("Could not generate new Bing Speech AccessToken");
            }
        } catch (Exception e) {
            logger.error("Exception while getting new AccessToken: ", e);
        }
    }

    private void sendAudioRequest(String fileURI, String language, Messenger messenger, Long senderID){
        generateAccesToken();
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "https://speech.platform.bing.com/speech/recognition/interactive/cognitiveservices/v1?language="
                    + language + "&locale=" + locale + "&requestid=" + guid + "&format=" + format;
            HttpPost httpPost = new HttpPost(url);

            // HEADERS
            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Authorization", "Bearer " + accessToken);
            postHeaders.put("Content-Type", "audio/wav; codec=\"audio/pcm\"; samplerate=16000");

            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            // AUDIO FILE
            HttpGet get = new HttpGet(fileURI);
            CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(get);
            HttpEntity entity = execute.getEntity();

            ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
            entity.writeTo(bArrOS);
            bArrOS.flush();
            ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
            bArrOS.close();

            bArrEntity.setChunked(true);
            bArrEntity.setContentEncoding(HttpMultipartMode.BROWSER_COMPATIBLE.toString());
            bArrEntity.setContentType(ContentType.DEFAULT_BINARY.toString());

            //TODO: implement fileDirectory
            //File file = new File(fileURI);
            //FileBody bin = new FileBody(file, ContentType.DEFAULT_BINARY);
            //MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //builder.addPart("bin", bin);
            //HttpEntity reqEntity = builder.build();

            //ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
            //reqEntity.writeTo(bArrOS);
            //bArrOS.flush();
            //ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
            //bArrOS.close();

            //bArrEntity.setChunked(true);
            //bArrEntity.setContentEncoding(reqEntity.getContentEncoding());
            //bArrEntity.setContentType(reqEntity.getContentType());

            // set ByteArrayEntity to HttpPost
            httpPost.setEntity(bArrEntity);

            // send request
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("Send Audio request returns: Response Code - " + String.valueOf(responseCode));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            logger.debug("Audio request returns: " + result.toString());

            String response = "";
            // process response message
            //FIXME: not working!
            try {
                if (format == "simple") {
                    BingSimpleResponse bingSimpleResponse = new Gson().fromJson(result.toString(), BingSimpleResponse.class);
                    response = bingSimpleResponse.getText();
                } else if (format == "detailed") {
                    BingDetailedResponse bingDetailedResponse = new Gson().fromJson(result.toString(), BingDetailedResponse.class);
                    response = bingDetailedResponse.getText();
                }
            } catch (Exception e){
                logger.error("Error while parsing BingResponse: ", e);
            }

            // return message
            BotMessage emptyBotMessage = new BotMessage();
            emptyBotMessage.setId(1L);
            emptyBotMessage.setText(result.toString());
            emptyBotMessage.setMessenger(messenger);
            emptyBotMessage.setSenderID(senderID);
            messageQueue.addOutMessage(emptyBotMessage);

        } catch (Exception e){
            logger.error("Exception while audio request: ", e);
        }
    }
}
