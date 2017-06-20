
package de.bht.chatbot.nlu.apiai.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Oliver
 * Date: 19.06.17
 */
public class Parameters implements Serializable{

    //TODO: allow custom parameters, not just fixed one

    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("Essen")
    @Expose
    private String essen;
    @SerializedName("Vegan-Vegetarisch")
    @Expose
    private String veganVegetarisch;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getEssen() {
        return essen;
    }

    public void setEssen(String essen) {
        this.essen = essen;
    }

    public String getVeganVegetarisch() {
        return veganVegetarisch;
    }

    public void setVeganVegetarisch(String veganVegetarisch) {
        this.veganVegetarisch = veganVegetarisch;
    }

    public Map<String, String> getEntities(){
        Map<String, String> Entities = new HashMap<>();

        Entities.put("Essen",essen);
        Entities.put("Day",day);
        Entities.put("VeganVegetarisch",veganVegetarisch);

        return Entities;
    }

}
