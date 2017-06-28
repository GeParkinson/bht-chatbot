package de.bht.chatbot.nsp.bing;

import com.google.gson.Gson;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
import de.bht.chatbot.nsp.bing.model.BingAttachment;
import de.bht.chatbot.nsp.bing.model.BingDetailedResponse;
import de.bht.chatbot.nsp.bing.model.BingMessage;
import de.bht.chatbot.nsp.bing.model.BingSimpleResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
            if (botMessage.hasAttachements()) {
                // Speech to Text Request
                for (Attachment attachment : botMessage.getAttachements()) {
                    sendSpeechToTextRequest(botMessage);
                }
            } else {
                // Text to Speech Request
                sendTextToSpeechRequest(botMessage);
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
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("AccessToken request returns: Response Code - " + String.valueOf(responseCode));

            String result = EntityUtils.toString(httpResponse.getEntity());

            if (responseCode == 200) {
                accessToken = result;
                logger.debug("new AccessToken: " + accessToken);
            } else {
                logger.warn("Could not generate new Bing Speech AccessToken");
            }
        } catch (Exception e) {
            logger.error("Exception while getting new AccessToken: ", e);
        }
    }

    private void sendSpeechToTextRequest(BotMessage botMessage){
        generateAccesToken();
        //TODO: different languages
        String language = "de-DE";
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

            for (Attachment attachment : botMessage.getAttachements()) {
                //TODO: implement AttachmentStore
                // download audio file
                HttpGet get = new HttpGet(attachment.getFileURI());
                CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(get);
                HttpEntity entity = execute.getEntity();

                ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
                entity.writeTo(bArrOS);
                bArrOS.flush();
                ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
                bArrOS.close();


                // IMPORTANT! For Bing Speech API it is very important to set Transfer-Encoding = chunked. Otherwise Bing wouldn't accept the file.
                bArrEntity.setChunked(true);
                bArrEntity.setContentEncoding(HttpMultipartMode.BROWSER_COMPATIBLE.toString());
                bArrEntity.setContentType(ContentType.DEFAULT_BINARY.toString());

                // set ByteArrayEntity to HttpPost
                httpPost.setEntity(bArrEntity);

                // send request
                HttpResponse httpResponse = client.execute(httpPost);
                int responseCode = httpResponse.getStatusLine().getStatusCode();

                logger.debug("Speech to Text request returns: Response Code - " + String.valueOf(responseCode));

                String result = EntityUtils.toString(httpResponse.getEntity());

                logger.debug("Speech to Text request returns: " + result.toString());


                // process response message
                BingMessage bingMessage;
                try {
                    if (format == "simple") {
                        BingSimpleResponse bingSimpleResponse = new Gson().fromJson(result.toString(), BingSimpleResponse.class);
                        bingMessage = new BingMessage(bingSimpleResponse,botMessage);
                    } else if (format == "detailed") {
                        BingDetailedResponse bingDetailedResponse = new Gson().fromJson(result.toString(), BingDetailedResponse.class);
                        bingMessage = new BingMessage(bingDetailedResponse,botMessage);
                    } else {
                        logger.error("Could not parse BingSpeechResponse");
                        return;
                    }
                    // return message
                    messageQueue.addInMessage(bingMessage);

                } catch (Exception e) {
                    logger.error("Error while parsing BingSpeecResponse: ", e);
                }
            }
        } catch (Exception e){
            logger.error("Error while processing Speech to Text request: ", e);
        }
    }

    private void sendTextToSpeechRequest(BotMessage botMessage){
        generateAccesToken();

        HttpClient client = HttpClientBuilder.create().build();
        String url = "https://speech.platform.bing.com/synthesize";
        HttpPost httpPost = new HttpPost(url);

        // HEADERS
        Map<String, String> postHeaders = new HashMap<>();
        postHeaders.put("Content-Type", MediaType.APPLICATION_XML);
        postHeaders.put("Authorization", "Bearer " + accessToken);
        postHeaders.put("X-Microsoft-OutputFormat", "audio-16khz-32kbitrate-mono-mp3");

        for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }

        try {
            String xml = "<speak version='1.0' xml:lang='de-DE'><voice xml:lang='de-DE' xml:gender='Female' name='Microsoft Server Speech Text to Speech Voice (de-DE, HeddaRUS)'>" + botMessage.getText() + "</voice></speak>";
            HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            httpPost.setEntity(entity);

            // send request
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("Send Text to Speech returns: Response Code - " + String.valueOf(responseCode));

            // process response
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(bos);
            bos.flush();

            //TODO: generate proper IDs
            Long id = new Random().nextLong();

            //TODO: implement AttachmentStore or similar
            File file = new File (String.valueOf(id) + ".mpg");
            //file.mkdirs();
            file.createNewFile();

            OutputStream outputStream = null;
            outputStream = new FileOutputStream(file);

            bos.writeTo(outputStream);
            outputStream.flush();
            bos.close();
            outputStream.close();

            //TODO: dont use hardcoded URI
            BingMessage bingMessage = new BingMessage(botMessage, new BingAttachment(id, "https://wicioplcgi.localtunnel.me/bht-chatbot/rest/attachments/audio/" + id));

            messageQueue.addOutMessage(bingMessage);

        } catch (Exception e){
            logger.error("Error while processing Text to Speech request: ", e);
        }
    }
}
