package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;

import java.util.Map;

public class BoardMessage extends Message {
    Map<Integer, Player> positions;
 Map<Integer, Player> laps;

    public BoardMessage(MessageType type, String content, Map<Integer, Player> positions, Map<Integer, Player> laps) {
        super(type, content);
        this.positions = positions;
        this.laps = laps;
    }

    public Map<Integer, Player> getPositions() {
        return positions;
    }

    public Map<Integer, Player> getLaps() {
        return laps;
    }

}
