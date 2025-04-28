package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;

import java.io.Serializable;
import java.util.UUID;

public class ShipClientMessage extends StandardMessageClient implements Serializable {
   Player player;



    public ShipClientMessage(MessageType type, String content, UUID sender, Player player) {
        super(type, content,sender);

        this.player = player;

    }

    public Player getPlayer() {
        return player;
    }

}
