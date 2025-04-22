package it.polimi.ingsw.network.messages;

public class TimeUpdateMessage extends Message {
    private int id;
    public TimeUpdateMessage(MessageType type, String content, int id) {
        super(type, content);
        this.id = id;
    }
    public int getId() {
        return id;
    }
}
