
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


    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("dishtype")
    @Expose
    private String dishtype;
    @SerializedName("healthy")
    @Expose
    private String healthy;
    @SerializedName("ingredients")
    @Expose
    private String ingredients;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("dishcategory")
    @Expose
    private String dishcategory;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDishtype() {
        return dishtype;
    }

    public void setDishtype(String dishtype) {
        this.dishtype = dishtype;
    }

    public String getHealthy() {
        return healthy;
    }

    public void setHealthy(String healthy) {
        this.healthy = healthy;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDishcategory() {
        return dishcategory;
    }

    public void setDishcategory(String dishcategory) {
        this.dishcategory = dishcategory;
    }

    /**
     * @return puts the given entities with their values into a Map
     */
    public Map<String, String> getEntities(){
        Map<String, String> Entities = new HashMap<>();

        if (date!=null)Entities.put("date",date);
        if (dishtype!=null)Entities.put("dishtype",dishtype);
        if (healthy!=null)Entities.put("healthy",healthy);
        if (ingredients!=null)Entities.put("ingredients",ingredients);
        if (price!=null)Entities.put("price",price);
        if (number!=null)Entities.put("number",number);
        if (dishcategory!=null)Entities.put("dishcategory",dishcategory);

        return Entities;
    }

}
