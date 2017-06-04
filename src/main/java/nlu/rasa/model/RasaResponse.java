package nlu.rasa.model;

import nlu.NLUResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 * <p>
 * NLUResponse adapter for rasa_nlu
 */
public class RasaResponse implements NLUResponse {

    private List<RasaEntity> rasaEntities;
    private RasaIntent rasaIntent;
    private String text;


    public List<RasaEntity> getRasaEntities() {
        return rasaEntities;
    }

    public void setRasaEntities(List<RasaEntity> rasaEntities) {
        this.rasaEntities = rasaEntities;
    }

    public RasaIntent getRasaIntent() {
        return rasaIntent;
    }

    public void setRasaIntent(RasaIntent rasaIntent) {
        this.rasaIntent = rasaIntent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Map<String, String> getEntities() {
        Map<String, String> entityMap = new HashMap<>();
        for (RasaEntity rasaEntity : rasaEntities) {
            entityMap.put(rasaEntity.getEntity(), rasaEntity.getValue());
        }
        return entityMap;
    }

    public String getIntent() {
        return rasaIntent.getName();
    }
}
