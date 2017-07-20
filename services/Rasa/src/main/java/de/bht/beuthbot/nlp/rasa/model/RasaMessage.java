package de.bht.beuthbot.nlp.rasa.model;

import de.bht.beuthbot.jms.ProcessQueueMessageProtocol;
import de.bht.beuthbot.jms.Target;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.Messenger;

import java.util.List;
import java.util.Map;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 26.06.17
 */
public class RasaMessage implements ProcessQueueMessageProtocol {

    private ProcessQueueMessageProtocol inMessage;
    private RasaResponse rasaResponse;

    public RasaMessage(final ProcessQueueMessageProtocol inMessage, final RasaResponse rasaResponse) {
        this.inMessage = inMessage;
        this.rasaResponse = rasaResponse;
    }

    @Override
    public Long getId() {
        return inMessage.getId();
    }

    @Override
    public Target getTarget() {
        return Target.MAINBOT;
    }

    @Override
    public Long getMessageID() {
        return inMessage.getMessageID();
    }

    @Override
    public Long getSenderID() {
        return inMessage.getSenderID();
    }

    @Override
    public Messenger getMessenger() {
        return inMessage.getMessenger();
    }

    @Override
    public String getText() {
        return inMessage.getText();
    }

    @Override
    public boolean hasAttachments() {
        return inMessage.hasAttachments();
    }

    @Override
    public List<Attachment> getAttachments() {
        return inMessage.getAttachments();
    }

    @Override
    public Map<String, String> getEntities() {
        return rasaResponse.getEntities();
    }

    @Override
    public String getIntent() {
        return rasaResponse.getIntent();
    }
}
