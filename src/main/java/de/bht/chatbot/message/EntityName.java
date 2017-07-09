package de.bht.chatbot.message;

/**
 * Created by sJantzen on 29.06.2017.
 */
public enum EntityName {

    DATE("date"), DISH_TYPE("dishtype"), HEALTHY("healthy"), INGREDIENTS("ingredients");

    private String text;

    EntityName(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
