package de.bht.chatbot.nsp.bing;

import com.google.gson.Gson;
import de.bht.chatbot.jms.MessageQueue;
import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.messenger.utils.MessengerUtils;
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
                //TODO: implement Attachmentstorage
                // AUDIO FILE DOWNLOAD
                HttpGet get = new HttpGet(attachment.getFileURI());
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
        //TODO: implement
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
            String xml = "<speak version='1.0' xml:lang='en-US'><voice xml:lang='en-US' xml:gender='Female' name='Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)'>" + botMessage.getText() + "</voice></speak>";
            HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            httpPost.setEntity(entity);

            // send request
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("Send Text to Speech returns: Response Code - " + String.valueOf(responseCode));

            //TODO: process response
            //httpResponse.getEntity();

        } catch (Exception e){
            logger.error("Error while processing Text to Speech request: ", e);
        }
    }
}
