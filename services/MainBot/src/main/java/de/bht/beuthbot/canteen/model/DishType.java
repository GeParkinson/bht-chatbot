package de.bht.beuthbot.canteen.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum DishType {

    VEGETARIAN("vegetarian", "1"), VEGAN("vegan", "15"), CLIMATE_NEUTRAL("klimaessen", "43"), BIO("bio", "18"), MSC("msc", "38");

    private String text;
    private String number;

    DishType(final String text, final String number){
        this.text = text;
        this.number = number;
    }

    public String getText(){
        return text;
    }

    public String getNumber() {
        return number;
    }

    public String toString(){
        return text;
    }

    /**
     * Checks whether the given name matches with a dishType.
     * @param name
     * @returns dishType if matches, else null
     */
    public static DishType getDishTypeByName(final String name) {
        for (DishType dishType : DishType.values()) {
            if(dishType.getText().equals(name)){
                return dishType;
            }
        }
        return null;
    }

    /**
     * Checks whether the given number matches with a dishType.
     * @param number
     * @returns dishType if matches, else null
     */
    public static DishType getDishTypeByNumber(final String number) {
        for (DishType dishType : DishType.values()) {
            if(dishType.getNumber().equals(number)){
                return dishType;
            }
        }
        return null;
    }

    /**
     * Creates a list of dishTypes by a given string.
     * @param strDishTypes
     * @return
     */
    public static List<DishType> getDishTypes(final String strDishTypes) {
        List<DishType> rv = new ArrayList<>();

        for (String dishType : Arrays.asList(strDishTypes.split("\\s*,\\s*"))) {
            DishType dt = getDishTypeByName(dishType);
            if(dt != null) {
                rv.add(dt);
            }
        }
        return rv;
    }
}
