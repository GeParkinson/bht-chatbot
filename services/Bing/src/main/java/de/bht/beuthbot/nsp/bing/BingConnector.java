package de.bht.beuthbot.nsp.bing;

import com.google.gson.Gson;
import de.bht.beuthbot.attachments.AttachmentStore;
import de.bht.beuthbot.attachments.model.AttachmentStoreMode;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.jms.ProcessQueue;
import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.TaskMessage;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.AttachmentType;
import de.bht.beuthbot.nsp.bing.model.BingAttachment;
import de.bht.beuthbot.nsp.bing.model.BingDetailedResponse;
import de.bht.beuthbot.nsp.bing.model.BingMessage;
import de.bht.beuthbot.nsp.bing.model.BingSimpleResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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

    /** slf4j Logger */
    private final Logger logger = LoggerFactory.getLogger(BingConnector.class);

    /** Bing Speech API Access Token */
    private String accessToken = "";

    /** locale e.g. global */
    private String locale = "";

    /** Bing Speech API User GUID */
    private String guid = "";

    //TODO: distinguish between formats (simple or detailed)
    /** Bing Speech API response format */
    private String format = "simple";

    /** Injected JMS MessageQueue */
    @Resource(lookup = "java:global/global/ProcessQueueBean")
    private ProcessQueue processQueue;

    /** Injected AttachmentStore */
    @Resource(lookup = "java:global/global/AttachmentStoreBean")
    private AttachmentStore attachmentStore;

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    /**
     * Process JMS Message
     * @param message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            ProcessQueueMessageProtocol botMessage = message.getBody(TaskMessage.class);
            if (botMessage.hasAttachments()) {
                // Speech to Text Request
                for (Attachment attachment : botMessage.getAttachments()) {
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

    //TODO: cyclically generate a Token - remove call in methods
    /**
     * Method to generate Bing Speech API Access Token. - Token decays after 10 minutes. -> Refresh every 9 minutes.
     */
    private void generateAccessToken() {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            String url = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
            HttpPost httpPost = new HttpPost(url);

            // get config Properties
            locale = application.getConfiguration(Configuration.BING_SPEECH_LOCALE);
            guid = application.getConfiguration(Configuration.BING_SPEECH_GUID);

            // headers
            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
            postHeaders.put("Ocp-Apim-Subscription-Key", application.getConfiguration(Configuration.BING_SPEECH_SECRET_KEY));

            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            // send request
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("AccessToken request returns: Response Code - " + String.valueOf(responseCode));

            String result = EntityUtils.toString(httpResponse.getEntity());

            if (responseCode == HttpStatus.SC_OK) {
                accessToken = result;
                logger.debug("new AccessToken: " + accessToken);
            } else {
                logger.warn("Could not generate new Bing Speech AccessToken");
            }
        } catch (Exception e) {
            logger.error("Exception while getting new AccessToken: ", e);
        }
    }

    /**
     * Send Bing Speech to Text Request.
     * @param botMessage Object to parse to Text
     */
    private void sendSpeechToTextRequest(final ProcessQueueMessageProtocol botMessage) {
        generateAccessToken();
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
            postHeaders.put("Content-Type", "audio/mpeg; codec=\"audio/pcm\"; samplerate=16000");

            for (Map.Entry<String, String> entry : postHeaders.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }

            for (Attachment attachment : botMessage.getAttachments()) {

                // audio file download
                HttpGet get = new HttpGet(attachment.getFileURI());
                CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(get);
                HttpEntity entity = execute.getEntity();

                ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
                entity.writeTo(bArrOS);
                bArrOS.flush();
                ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
                bArrOS.close();

                // IMPORTANT! For Bing Speech API it is necessary to set Transfer-Encoding = chunked. Otherwise Bing wouldn't accept the file.
                bArrEntity.setChunked(true);
                bArrEntity.setContentEncoding(HttpMultipartMode.BROWSER_COMPATIBLE.toString());
                bArrEntity.setContentType(ContentType.DEFAULT_BINARY.toString());

                //THIS CODE IS NOT WORKING PROPERLY - Check AttachmentStore for correct implementation
//                // get audio file from AttachmentStore
//                File file = new File(attachmentStore.loadAttachmentPath(attachment.getId(), AttachmentStoreMode.LOCAL_PATH));
//                FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
//                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//                multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//                multipartEntityBuilder.addPart("bin", fileBody);
//                HttpEntity httpEntity = multipartEntityBuilder.build();
//
//                ByteArrayOutputStream bArrOS = new ByteArrayOutputStream();
//                httpEntity.writeTo(bArrOS);
//                bArrOS.flush();
//                ByteArrayEntity bArrEntity = new ByteArrayEntity(bArrOS.toByteArray());
//                bArrOS.close();
//
//                // IMPORTANT! For Bing Speech API it is necessary to set Transfer-Encoding = chunked. Otherwise Bing wouldn't accept the file.
//                bArrEntity.setChunked(true);
//                bArrEntity.setContentEncoding(httpEntity.getContentEncoding());
//                bArrEntity.setContentType(httpEntity.getContentType());

                // set ByteArrayEntity to HttpPost
                httpPost.setEntity(bArrEntity);

                // send request
                HttpResponse httpResponse = client.execute(httpPost);
                int responseCode = httpResponse.getStatusLine().getStatusCode();

                logger.debug("Speech to Text request returns: Response Code - " + String.valueOf(responseCode));

                String result = EntityUtils.toString(httpResponse.getEntity());

                if (responseCode == HttpStatus.SC_OK) {
                    // process response message
                    BingMessage bingMessage;
                    try {
                        switch (format){
                            case "simple":
                                BingSimpleResponse bingSimpleResponse = new Gson().fromJson(result, BingSimpleResponse.class);
                                bingMessage = new BingMessage(bingSimpleResponse, botMessage);
                                logger.debug("Received message from bing: {}", result);
                                break;
                            case "detailed":
                                BingDetailedResponse bingDetailedResponse = new Gson().fromJson(result, BingDetailedResponse.class);
                                bingMessage = new BingMessage(bingDetailedResponse, botMessage);
                                break;
                            default:
                                logger.error("Could not parse BingSpeechResponse");
                                return;
                        }
                        // return message
                        processQueue.route(new TaskMessage(bingMessage));
                    } catch (Exception e) {
                        logger.error("Error while parsing BingSpeechResponse: ", e);
                    }
                } else {
                    logger.warn("Could not process Speech to Text request. Returns: " + "Speech to Text request returns: " + result.toString());
                }
            }
        } catch (Exception e){
            logger.error("Error while processing Speech to Text request: ", e);
        }
    }

    /**
     * Send Bing Text to Speech Request.
     * @param botMessage Object to parse to Speech
     */
    private void sendTextToSpeechRequest(final ProcessQueueMessageProtocol botMessage){
        generateAccessToken();

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
            //TODO: build a proper xml
            // Bing Text to Speech uses the Speech Synthesis Markup Language (SSML) Standard
            String xml = "<speak version='1.0' xml:lang='de-DE'><voice xml:lang='de-DE' xml:gender='Female' name='Microsoft Server Speech Text to Speech Voice (de-DE, HeddaRUS)'>" + botMessage.getText() + "</voice></speak>";
            HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
            httpPost.setEntity(entity);

            // send request
            HttpResponse httpResponse = client.execute(httpPost);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            logger.debug("Send Text to Speech returns: Response Code - " + String.valueOf(responseCode));

            if (responseCode == HttpStatus.SC_OK) {
                // process response
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(bos);
                bos.flush();

                // store file
                Long id = attachmentStore.storeAttachment(bos, AttachmentType.AUDIO);
                bos.close();
                String path = attachmentStore.loadAttachmentPath(id, AttachmentStoreMode.FILE_URI);

                BingMessage bingMessage = new BingMessage(botMessage, new BingAttachment(id, path));

                processQueue.route(new TaskMessage(bingMessage));
            } else {
                logger.warn("Could not process Speech to Text request. Returns: " + "Speech to Text request returns: " + httpResponse.toString());
            }
        } catch (Exception e){
            logger.error("Error while processing Text to Speech request: ", e);
        }
    }
}
