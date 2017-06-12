package nlu.rasa.model;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 04.06.17
 */
public class RasaEntity {

    private String entity;
    private String value;


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
