package de.bht.beuthbot.jms;

import de.bht.beuthbot.model.BotMessage;

import javax.ejb.Local;
import javax.ejb.Remote;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 05.07.17
 */
@Remote
public interface ProcessQueue {
    boolean addInMessage(BotMessage botMessageObject);

    boolean addMessage(BotMessage botMessageObject, String propertyName, String propertyValue);

    boolean addOutMessage(BotMessage botMessageObject);
}
