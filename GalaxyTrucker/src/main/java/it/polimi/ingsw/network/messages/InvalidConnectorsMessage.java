package it.polimi.ingsw.network.messages;

import javafx.util.Pair;

import java.util.List;

public class InvalidConnectorsMessage extends Message {
    List<Pair<Integer, Integer>> invalids;
    public InvalidConnectorsMessage(MessageType type, String content, List<Pair<Integer, Integer>> invalids) {
        super(type, content);
        this.invalids = invalids;
    }

    public List<Pair<Integer, Integer>> getInvalids() {
            return invalids;
    }

}
