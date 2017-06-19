package message;

/**
 * @Author: Christopher Kümmel on 6/19/2017.
 */
public interface NLUBotMessage extends BotMessage {

    public String getIntent();

    public String[] getEntities();

}
