package it.polimi.ingsw.network.messages;

import javafx.util.Pair;

import java.util.UUID;

public class AddBatteryMessage extends StandardMessageClient {
    private Pair<Integer,Integer> pos;
    public AddBatteryMessage(MessageType type, String content, UUID sender, Pair<Integer,Integer> pos) {
        super(type, content, sender);
        this.pos = pos;
    }
    public Pair<Integer,Integer> getPos() {
        return pos;
    }
}
