package de.bht.beuthbot.nlp.rasa.model;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
public class RasaIntent {
    private String name;
    private String confidence;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
}
