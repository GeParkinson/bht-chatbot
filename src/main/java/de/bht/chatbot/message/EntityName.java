package de.bht.chatbot.message;

/**
 * Created by sJantzen on 29.06.2017.
 */
public enum EntityName {

    DATE("date"), DISH_TYPE("dishType"), DISH_CATEGORY("dishCategory"), TRAFFICLIGHT("trafficLights"), MARKINGS("markings");

    private String text;

    EntityName(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
