package canteen;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum DishType {

    NORMAL("normal"), VEGETARIAN("vegetarisch"), VEGAN("vegan"), CLIMATE_NEUTRAL("Klimaessen"), BIO("Bio");

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
