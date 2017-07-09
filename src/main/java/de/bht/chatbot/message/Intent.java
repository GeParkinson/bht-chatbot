package de.bht.chatbot.message;

/**
 * Created by sJantzen on 29.06.2017.
 */
public enum Intent {

    SHOW_FOOD("showFood"), HELLO("Hello"), GREET("greet"), BYE("Bye"),
    GOODBYE("goodbye"), RESTAURANT_SEARCH("restaurant_search");

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
