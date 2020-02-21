package kafka.model;

import java.util.ArrayList;
import java.util.List;

public class MessagesContainer {


    public boolean isCompacted() {
        return isCompacted;
    }

    public void setCompacted(boolean compacted) {
        isCompacted = compacted;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    private boolean isCompacted = false;
    private List<Message> messages = new ArrayList<>();

    public MessagesContainer(boolean isCompacted, List<Message> messages) {
        this.isCompacted = isCompacted;
        this.messages = messages;
    }

}
