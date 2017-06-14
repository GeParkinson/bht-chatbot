package nsp.bing.model;

import nsp.NSPResponse;

import java.util.List;

/**
 * @Author: Christopher Kümmel on 6/14/2017.
 */
public class BingDetailedResponse implements NSPResponse {

    private String recognitionStatus;
    private Long offset;
    private Long duration;
    private List<nBest> nBests;

    @Override
    public String getText() {
        double highestValue = 0;
        //TODO: implement other way
        for (nBest best : nBests){
            if (best.getConfidence() > highestValue) highestValue = best.getConfidence();
        }
        for (nBest best : nBests){
            if (best.getConfidence() == highestValue) return best.getDisplayText();
        }
        return "";
    }

    @Override
    public String getRecognitionStatus() {
        return recognitionStatus;
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

    public List<nBest> getnBests() {
        return nBests;
    }

    public void setnBests(List<nBest> nBests) {
        this.nBests = nBests;
    }

    public class nBest{
        double confidence;
        String lexical;
        String itn;
        String maskedITN;
        String displayText;

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getLexical() {
            return lexical;
        }

        public void setLexical(String lexical) {
            this.lexical = lexical;
        }

        public String getItn() {
            return itn;
        }

        public void setItn(String itn) {
            this.itn = itn;
        }

        public String getMaskedITN() {
            return maskedITN;
        }

        public void setMaskedITN(String maskedITN) {
            this.maskedITN = maskedITN;
        }

        public String getDisplayText() {
            return displayText;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }
    }
}
