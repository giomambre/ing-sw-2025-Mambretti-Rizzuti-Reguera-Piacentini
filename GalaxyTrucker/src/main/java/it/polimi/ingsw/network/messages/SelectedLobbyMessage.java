package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.UUID;

public class SelectedLobbyMessage extends StandardMessageClient implements Serializable {
    Integer lobbyId;
    public SelectedLobbyMessage(MessageType type , String content, UUID sender, Integer lobbyId) {
        super(type, content, sender);
        this.lobbyId = lobbyId;
    }

    public Integer getLobbyId() { return lobbyId; }


}
