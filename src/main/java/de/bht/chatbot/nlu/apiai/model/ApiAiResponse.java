package de.bht.chatbot.nlu.apiai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.bht.chatbot.nlu.NLUResponse;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: Oliver
 * Date: 19.06.17
 *
 * ApiAi-specific class of the NLUResponse Interface
 * class which represents the JSON received from ApiAI
 */
public class ApiAiResponse implements NLUResponse, Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    @Override
    public String getIntent() {
        return result.getMetadata().getIntentName();
    }

    @Override
    public Map<String, String> getEntities() {
        return result.getParameters().getEntities();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
