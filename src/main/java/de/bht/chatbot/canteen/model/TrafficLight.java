package de.bht.chatbot.canteen.model;

/**
 * Created by sJantzen on 11.06.2017.
 */
public enum TrafficLight {

    GREEN("green"), YELLOW("yellow"), RED("red");

    private String text;

    TrafficLight(final String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    /**
     * Returns a trafficlight if matches with given text.
     * @param text
     * @returns null if no match was found.
     */
    public static TrafficLight getTrafficLight(final String text){
        switch (text.toLowerCase()) {
            case "green":
                return TrafficLight.GREEN;
            case "yellow":
                return TrafficLight.YELLOW;
            case "red":
                return TrafficLight.RED;
            default:
                return null;
        }
    }

    public String toString(){
        return text;
    }
}
