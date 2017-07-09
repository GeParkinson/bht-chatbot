package de.bht.chatbot.nsp.bing.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Bing Speech API Java Class for JSON parsing.
 * @Author: Christopher Kümmel on 6/15/2017.
 */
public class BingNBest implements Serializable{

    @SerializedName("Confidence")
    private String confidence;
    @SerializedName("Lexical")
    private String lexical;
    @SerializedName("ITN")
    private String iTN;
    @SerializedName("MaskedITN")
    private String maskedITN;
    @SerializedName("Display")
    private String display;

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getLexical() {
        return lexical;
    }

    public void setLexical(String lexical) {
        this.lexical = lexical;
    }

    public String getITN() {
        return iTN;
    }

    public void setITN(String iTN) {
        this.iTN = iTN;
    }

    public String getMaskedITN() {
        return maskedITN;
    }

    public void setMaskedITN(String maskedITN) {
        this.maskedITN = maskedITN;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

}
