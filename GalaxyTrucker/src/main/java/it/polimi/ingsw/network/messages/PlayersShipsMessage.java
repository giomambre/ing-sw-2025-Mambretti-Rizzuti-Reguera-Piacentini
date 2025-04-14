package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayersShipsMessage extends Message {

    List<Player> players = new ArrayList<Player>();

    public PlayersShipsMessage(MessageType type, String content, List<Player> players) {

        super(type,content);
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

}
