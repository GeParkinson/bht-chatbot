package de.bht.beuthbot.attachments;

import de.bht.beuthbot.attachments.model.AttachmentStoreMode;
import de.bht.beuthbot.conf.Application;
import de.bht.beuthbot.conf.Configuration;
import de.bht.beuthbot.model.AttachmentType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author: georg.glossmann@adesso.de, Christopher Kümmel
 * Date: 26.05.17
 */
@Singleton
public class AttachmentStoreBean implements AttachmentStore {

    /** slf4j Logger */
    private static Logger logger = LoggerFactory.getLogger(AttachmentStoreBean.class);

    /** id counter for unique id. */
    private static Long idCounter = 0L;

    /** BeuthBot Application Bean */
    @Resource(lookup = "java:global/global/ApplicationBean")
    private Application application;

    /** path to local attachment directory. */
    private String localPath;

    /** accessible FileURI base Path. */
    private String fileURIPath;

    /**
     * Initialize AttachmentStore
     */
    @PostConstruct
    public void init(){
        localPath = application.getConfiguration(Configuration.LOCAL_ATTACHMENT_PATH);
        fileURIPath = application.getConfiguration(Configuration.WEB_URL) + "/attachments/";
        // make directories
        File file = new File(localPath);
        try {
            if (file.exists()) {
                // WORKAROUND: delete old data at system start
                deleteDirectory(file);
            } else {
                file.mkdirs();
            }
        } catch (Exception e) {
            logger.error("Error while deleting or creating AttachmentStore Directory.", e);
        }
    }

    /**
     * Delete attachment directory.
     * @param file directory to delete
     */
    private void deleteDirectory(final File file){
        File[] directoryFiles = file.listFiles();
        for (File directoryFile : directoryFiles) {
            directoryFile.delete();
        }
    }

    /**
     * Download and store file on Server.
     * @param fileURI to store on Server
     * @param attachmentType for file-ending and directory
     * @return generated id for specific Attachment. null if attachment isn´t an audio or voice file
     */
    @Override
    public Long storeAttachment(final String fileURI, final AttachmentType attachmentType) {

        Long id = ++idCounter;

        if (attachmentType == AttachmentType.AUDIO || attachmentType == AttachmentType.VOICE) {
            // download file then store it locally
            try {
                HttpGet get = new HttpGet(fileURI);
                CloseableHttpResponse execute = HttpClientBuilder.create().build().execute(get);
                HttpEntity entity = execute.getEntity();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                entity.writeTo(byteArrayOutputStream);
                byteArrayOutputStream.flush();

                OutputStream outputStream = getOutputStreamByAttachmentType(attachmentType);

                byteArrayOutputStream.writeTo(outputStream);
                outputStream.flush();
                byteArrayOutputStream.close();
                outputStream.close();

            } catch (Exception e) {
                logger.error("Error occurred while try to store new attachment. ", e);
            }
            return id;
        } else {
            return null;
        }
    }

    /**
     * Store file on Server.
     * @param byteArrayOutputStream to store on Server
     * @param attachmentType for file-ending and directory
     * @return generated id for specific Attachment. null if attachment isn´t an audio or voice file
     */
    @Override
    public Long storeAttachment(final ByteArrayOutputStream byteArrayOutputStream, final AttachmentType attachmentType){

        Long id = ++idCounter;

        if (attachmentType == AttachmentType.AUDIO || attachmentType == AttachmentType.VOICE) {
            // store file locally
            try{
                OutputStream outputStream = getOutputStreamByAttachmentType(attachmentType);

                byteArrayOutputStream.writeTo(outputStream);
                outputStream.flush();
                byteArrayOutputStream.close();
                outputStream.close();

            } catch (Exception e) {
                logger.error("Error occurred while try to store new attachment. ", e);
            }
            return id;
        } else {
            return null;
        }
    }

    /**
     * Create OutputStream from given attachmentType
     * @param attachmentType OutputStreamPath for given attachmentType
     * @return null if attachmentType is undefined or error thrown
     */
    private OutputStream getOutputStreamByAttachmentType(final AttachmentType attachmentType) {
        try {
            File file;
            switch (attachmentType) {
                case AUDIO:
                    file = new File(localPath + "/audio/" + String.valueOf(idCounter) + ".mpg");
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    return new FileOutputStream(file);
                case VOICE:
                    file = new File(localPath + "/voice/" + String.valueOf(idCounter) + ".mpg");
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    return new FileOutputStream(file);
                //TODO: implement more cases
                default:
                    logger.warn("Undefined case for storing a new attachment!");
                    break;
            }
        } catch (Exception e) {
            logger.error("Error while creating OutputStream on AttachmentStore.", e);
        }
        return null;
    }

    /**
     * returns Attachment with local file path
     * @param attachmentID to identify file
     * @param attachmentStoreMode defines the response path to the file - accessible file URI or local path
     * @return null if Attachment not found
     */
    @Override
    public String loadAttachmentPath(final long attachmentID, final AttachmentStoreMode attachmentStoreMode) {
        // find local file
        String localFilePath = findFile(new File(localPath).listFiles(), attachmentID);

        if (localFilePath != null) {
            if (attachmentStoreMode == AttachmentStoreMode.LOCAL_PATH) {
                return localFilePath;
            } else {
                //TODO: implement a better way
                // find AttachmentType
                String[] type = localFilePath.split("/");
                switch (type[type.length - 2]) {
                    case "audio":
                        return fileURIPath + "audio/" + String.valueOf(attachmentID) + ".mpg";
                    case "voice":
                        return fileURIPath + "voice/" + String.valueOf(attachmentID) + ".mpg";
                    default:
                        logger.warn("AttachmentStore couldn't find requested file!");
                        return null;
                }
            }
        } else {
            logger.warn("AttachmentStore couldn't find requested file!");
            return null;
        }
    }

    /**
     * Iterate through file directory and find file
     * @param files list of files to compare
     * @param id of the file
     * @return null if file not found
     */
    private String findFile(final File[] files, final Long id) {
        for (File file : files) {
            if (file.isDirectory()) {
                String filePath = findFile(file.listFiles(), id);
                if (filePath != null) return filePath;
            } else {
                String name = file.getName();
                int pos = name.lastIndexOf(".");
                if (pos > 0) {
                    name = name.substring(0, pos);
                }
                if (name.equals(String.valueOf(id))) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }
}
