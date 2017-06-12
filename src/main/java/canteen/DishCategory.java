package canteen;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum DishCategory {

    STARTER("Vorspeise"), SALAD("Salat"), SOUP("Suppe"), SPECIAL("Aktionsstand"), FOOD("Essen"), SIDE_DISH("Beilage"), DESSERT("Dessert");

    private String text;

    DishCategory(final String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public String toString(){
        return text;
    }
}
