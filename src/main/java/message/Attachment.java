package message;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.Serializable;

/**
 * Created by Chris on 5/14/2017.
 */
public class Attachment implements Serializable{
    Long id;
    String fileUrl;
    String fileID;
    File file;
    byte[] fileArray;
    AttachmentType attachmentType;
    FileType fileType;
    String fileName;
    Integer duration;
    String caption;

    public Attachment(){}

    public Attachment(String fileID, AttachmentType attachmentType, FileType fileType){
        this.fileID = fileID;
        this.attachmentType = attachmentType;
        this.fileType = fileType;
    }
    public Attachment(String fileID, AttachmentType attachmentType, FileType fileType, String fileUrl){
        this.fileID = fileID;
        this.attachmentType = attachmentType;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
    }

    /** Getter & Setter*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getFileArray() {
        return fileArray;
    }

    public void setFileArray(byte[] fileArray) {
        this.fileArray = fileArray;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(AttachmentType attachmentType) {
        this.attachmentType = attachmentType;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
