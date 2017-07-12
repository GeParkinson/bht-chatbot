package de.bht.beuthbot.jms;

import javax.ejb.Remote;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 05.07.17
 */
@Remote
public interface ProcessQueue {

    /**
     * Routing internal messages to there destination, based on the message content.
     * @param processQueueMessage the internal message to route
     */
    void route(ProcessQueueMessageProtocol processQueueMessage);
}
