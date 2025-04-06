package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AvaiableLobbiesMessage extends Message implements Serializable {
    List<Integer> lobbies = new ArrayList<>();
    public AvaiableLobbiesMessage(MessageType type , String content,List<Integer> lobbies) {
        super(type, content);
        this.lobbies = lobbies;
    }

    public List<Integer> getLobbies() {
        return lobbies;
    }
}
