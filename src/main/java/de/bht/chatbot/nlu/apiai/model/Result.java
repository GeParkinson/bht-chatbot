
package de.bht.chatbot.nlu.apiai.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author: Oliver
 * Date: 19.06.17
 */
public class Result  implements Serializable {

    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("resolvedQuery")
    @Expose
    private String resolvedQuery;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("actionIncomplete")
    @Expose
    private Boolean actionIncomplete;
    @SerializedName("parameters")
    @Expose
    private Parameters parameters;
    @SerializedName("contexts")
    @Expose
    private List<Object> contexts = null;
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("fulfillment")
    @Expose
    private Fulfillment fulfillment;
    @SerializedName("score")
    @Expose
    private Integer score;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResolvedQuery() {
        return resolvedQuery;
    }

    public void setResolvedQuery(String resolvedQuery) {
        this.resolvedQuery = resolvedQuery;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getActionIncomplete() {
        return actionIncomplete;
    }

    public void setActionIncomplete(Boolean actionIncomplete) {
        this.actionIncomplete = actionIncomplete;
    }

    public Parameters getParameters(){
        return parameters;
    }

    /*public Map<String,String> getEntities() {
        Map<String, String> Entities = new HashMap<>();

        JSONObject obj =new JSONObject(parameters);
        for(Iterator iterator = obj.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            Entities.put(key, obj.getString(key));
        }

        return Entities;
    }*/

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public List<Object> getContexts() {
        return contexts;
    }

    public void setContexts(List<Object> contexts) {
        this.contexts = contexts;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Fulfillment getFulfillment() {
        return fulfillment;
    }

    public void setFulfillment(Fulfillment fulfillment) {
        this.fulfillment = fulfillment;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}
