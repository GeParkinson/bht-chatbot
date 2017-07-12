package de.bht.beuthbot.jms;

import com.google.gson.Gson;
import de.bht.beuthbot.model.Attachment;
import de.bht.beuthbot.model.BotMessage;
import de.bht.beuthbot.model.Messenger;
import de.bht.beuthbot.model.NLUBotMessage;
import de.bht.beuthbot.utils.GsonUtil;

import java.io.Serializable;
import java.util.*;

/**
 * @author: georg.glossmann@adesso.de
 * Date: 06.07.17
 */
public class TaskMessage implements ProcessQueueMessageProtocol {

    private static final long serialVersionUID = 8234160552216573531L;

    private final Long id;
    private final Target target;
    private final Long messageID;
    private final Long senderID;
    private final Messenger messenger;
    private final String text;
    private final List<Attachment> attachments;
    private final String intent;
    private final Map<String, String> entities;

    public TaskMessage(final ProcessQueueMessageProtocol message) {
        this.id = message.getId();
        this.target = message.getTarget();
        this.messageID = message.getMessageID();
        this.senderID = message.getSenderID();
        this.messenger = message.getMessenger();
        this.text = message.getText();

        this.attachments = new ArrayList<>();
        this.attachments.addAll(message.getAttachments());

        this.intent = message.getIntent();
        this.entities = new HashMap<>();
        this.entities.putAll(message.getEntities());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Target getTarget() {
        return target;
    }

    @Override
    public Long getMessageID() {
        return messageID;
    }

    @Override
    public Long getSenderID() {
        return senderID;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean hasAttachments() {
        if (attachments == null) {
            return false;
        }
        return attachments.size() > 0;
    }

    @Override
    public List<Attachment> getAttachments() {
        if (attachments == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(attachments);
    }

    @Override
    public String getIntent() {
        return intent;
    }

    @Override
    public Map<String, String> getEntities() {
        if (entities == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(entities);
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }
}
