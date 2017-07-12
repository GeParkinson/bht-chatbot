package de.bht.beuthbot.model;

/**
 * Created by sJantzen on 29.06.2017.
 */
public enum EntityName {

    DATE("date"), DISH_TYPE("dishtype"), HEALTHY("healthy"), INGREDIENTS("ingredients"), DISH_CATEGORY("dishcategory"),
    DISH_NAME("dishname");

    private String text;

    EntityName(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
