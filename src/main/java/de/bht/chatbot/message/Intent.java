package de.bht.chatbot.message;

/**
 * Created by sJantzen on 29.06.2017.
 */
public enum Intent {

    SHOW_DISHES("show_dishes"), OPENING_HOURS("opening_hours"), KITCHEN_OPENING_HOURS("kitchen_opening_hours"),
    GREETING("greeting"), SAYING_GOODBYE("saying_goodbye");

    private String text;

    Intent(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    /**
     * Returns an intent matching the given text.
     * @param text
     * @return null if no matching intent is found.
     */
    public Intent getIntent(final String text) {
        for (Intent intent : Intent.values()) {
            if (intent.getText().equals(text)){
                return intent;
            }
        }
        return null;
    }

}
