package de.bht.beuthbot.canteen.model;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum DishCategory {

    STARTER("Vorspeisen"), SALAD("Salate"),SOUP("Suppen"), SPECIAL("Aktionen"), FOOD("Essen"), SIDE_DISH("Beilagen"), DESSERT("Desserts");

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

    /**
     * Returns the assigned dish category to a given string.
     * @param strCategory
     * @return
     */
    public static DishCategory getDishCategory(final String strCategory) {
        switch (strCategory.toLowerCase()) {
            case "vorspeisen":
                return STARTER;
            case "salate":
                return SALAD;
            case "suppen":
                return SOUP;
            case "aktionen":
                return SPECIAL;
            case "essen":
                return FOOD;
            case "beilagen":
                return SIDE_DISH;
            case "desserts":
                return DESSERT;
            default:
                return null;
        }
    }
}
