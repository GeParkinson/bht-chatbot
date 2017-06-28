package de.bht.chatbot.attachments;

import de.bht.chatbot.message.Attachment;
import de.bht.chatbot.message.BotMessage;
import de.bht.chatbot.nsp.bing.BingConnector;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author: georg.glossmann@adesso.de, Christopher Kümmel
 * Date: 26.05.17
 */
public class AttachmentStore {

    private static Logger logger = LoggerFactory.getLogger(BingConnector.class);

    private final String folderDir = "/attachments";


    public long storeAttachment(final Attachment attachment) {

        //TODO: generate proper IDs
        Long id = new Random().nextLong();

        //Download File
        try {
            HttpGet get = new HttpGet(attachment.getFileURI());
            CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(get);
            HttpEntity entity = execute.getEntity();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            entity.writeTo(bos);
            bos.flush();

            OutputStream outputStream = null;
            switch (attachment.getAttachmentType()) {
                case AUDIO:
                    outputStream = new FileOutputStream(folderDir + "/audio/" + String.valueOf(id));
                    break;
                case VOICE:
                    outputStream = new FileOutputStream(folderDir + "/audio/" + String.valueOf(id));
                    break;
                case VIDEO:
                    outputStream = new FileOutputStream(folderDir + "/video/" + String.valueOf(id));
                    break;
                case PHOTO:
                    outputStream = new FileOutputStream(folderDir + "/photo/" + String.valueOf(id));
                    break;
                case DOCUMENT:
                    outputStream = new FileOutputStream(folderDir + "/document/" + String.valueOf(id));
                    break;
                default:
                    break;
            }
            bos.writeTo(outputStream);
            outputStream.flush();
            bos.close();
            outputStream.close();
        } catch (Exception e) {
        }
        return id;
    }

    public Attachment loadAttachment(final long attachmentID) {
        return null;
    }
}
