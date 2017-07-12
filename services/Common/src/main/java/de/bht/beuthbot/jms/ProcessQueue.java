package de.bht.beuthbot.jms;

import javax.ejb.Remote;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 05.07.17
 */
@Remote
public interface ProcessQueue {
    boolean addInMessage(ProcessQueueMessageProtocol botMessageObject);

    boolean addMessage(ProcessQueueMessageProtocol botMessageObject, String propertyName, String propertyValue);

    boolean addOutMessage(ProcessQueueMessageProtocol botMessageObject);
}
