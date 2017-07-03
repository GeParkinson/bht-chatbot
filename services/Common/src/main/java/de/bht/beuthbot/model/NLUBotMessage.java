package de.bht.beuthbot.model;

import java.util.Map;

/**
 * @Author: Christopher Kümmel on 6/19/2017.
 */
public interface NLUBotMessage extends BotMessage {

    public String getIntent();

    public Map<String, String> getEntities();

}
