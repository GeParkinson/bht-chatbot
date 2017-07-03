package de.bht.beuthbot.nlp.rasa.model;

import de.bht.beuthbot.model.nlp.NLUResponse;

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

    private List<RasaEntity> entities;
    private RasaIntent intent;
    private String text;


    public List<RasaEntity> getRasaEntities() {
        return entities;
    }

    public void setRasaEntities(List<RasaEntity> rasaEntities) {
        this.entities = rasaEntities;
    }

    public RasaIntent getRasaIntent() {
        return intent;
    }

    public void setRasaIntent(RasaIntent rasaIntent) {
        this.intent = rasaIntent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Map<String, String> getEntities() {
        Map<String, String> entityMap = new HashMap<>();
        for (RasaEntity rasaEntity : entities) {
            entityMap.put(rasaEntity.getEntity(), rasaEntity.getValue());
        }
        return entityMap;
    }

    public String getIntent() {
        return intent.getName();
    }
}
