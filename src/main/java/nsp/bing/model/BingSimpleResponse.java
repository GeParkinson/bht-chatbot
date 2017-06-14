package nsp.bing.model;

import nsp.NSPResponse;

/**
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public class BingSimpleResponse implements NSPResponse {

    private String recognitionStatus;
    private String displayText;
    private Long offset;
    private Long duration;

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

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
