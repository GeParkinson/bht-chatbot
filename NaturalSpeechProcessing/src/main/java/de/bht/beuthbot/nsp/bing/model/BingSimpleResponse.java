package de.bht.beuthbot.nsp.bing.model;

import com.google.gson.annotations.SerializedName;
import de.bht.beuthbot.nsp.NSPResponse;

/**
 * Bing Speech API Java Class for JSON parsing.
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public class BingSimpleResponse implements NSPResponse {

    private String recognitionStatus;
    private String displayText;
    private String offset;
    private String duration;

    @Override
    public String getText() {
        return displayText;
    }

    public void setText(String displayText){
        this.displayText = displayText;
    }

    @Override
    public String getRecognitionStatus() {
        return recognitionStatus;
    }

    public void setRecognitionStatus(String recognitionStatus){
        this.recognitionStatus = recognitionStatus;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
