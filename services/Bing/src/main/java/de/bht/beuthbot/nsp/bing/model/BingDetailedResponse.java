package de.bht.beuthbot.nsp.bing.model;

import de.bht.beuthbot.model.nsp.NSPResponse;

import java.util.List;

/**
 * Bing Speech API Java Class for JSON parsing.
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public class BingDetailedResponse implements NSPResponse {

    @SerializedName("RecognitionStatus")
    private String recognitionStatus;
    @SerializedName("Offset")
    private String offset;
    @SerializedName("Duration")
    private String duration;
    @SerializedName("N-Best")
    private List<BingNBest> nBest = null;

    @Override
    public String getText() {
        double highestValue = 0;
        for (BingNBest best : nBest){
            if (Double.valueOf(best.getConfidence()) > highestValue) highestValue = Double.valueOf(best.getConfidence());
        }
        for (BingNBest best : nBest){
            if (Double.valueOf(best.getConfidence()) == highestValue) return best.getConfidence();
        }
        return "";
    }

    @Override
    public String getRecognitionStatus() {
        return recognitionStatus;
    }

    public void setRecognitionStatus(String recognitionStatus){this.recognitionStatus = recognitionStatus;}

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

    public List<BingNBest> getnBests() {
        return nBest;
    }

    public void setnBests(List<BingNBest> nBests) {
        this.nBest = nBests;
    }

}
