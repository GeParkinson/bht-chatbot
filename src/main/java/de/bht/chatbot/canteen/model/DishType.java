package de.bht.chatbot.canteen.model;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum DishType {

    VEGETARIAN("vegetarisch"), VEGAN("vegan"), CLIMATE_NEUTRAL("Klimaessen"), BIO("Bio"), MSC("MSC");

    private String text;

    DishType(final String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public String toString(){
        return text;
    }
}
