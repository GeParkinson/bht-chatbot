package de.bht.chatbot.canteen.model;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum TrafficLight {

    GREEN("Grün"), YELLOW("Gelb"), RED("Rot");

    private String text;

    TrafficLight(final String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public String toString(){
        return text;
    }
}
