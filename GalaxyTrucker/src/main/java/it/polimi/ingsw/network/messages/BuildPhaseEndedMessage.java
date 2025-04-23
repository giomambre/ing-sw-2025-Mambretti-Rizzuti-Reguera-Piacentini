package it.polimi.ingsw.network.messages;

public class BuildPhaseEndedMessage extends Message {
    private int pos;
    public BuildPhaseEndedMessage(MessageType type, String content, int pos) {
        super(type, content);
        this.pos = pos;
    }
    public int getPos() {
        return pos;
    }
}
