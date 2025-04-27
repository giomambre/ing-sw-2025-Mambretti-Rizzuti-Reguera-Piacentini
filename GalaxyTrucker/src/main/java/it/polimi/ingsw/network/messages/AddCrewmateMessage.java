package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.enumerates.CrewmateType;
import javafx.util.Pair;

import java.util.UUID;

public class AddCrewmateMessage extends StandardMessageClient {
    private Pair<Integer,Integer> pos;
    private CrewmateType crewmateType;

    public AddCrewmateMessage(MessageType type, String content, UUID sender, Pair<Integer,Integer> pos, CrewmateType crewmateType) {
        super(type, content, sender);
        this.pos = pos;
        this.crewmateType=crewmateType;
    }

    public Pair<Integer,Integer> getPos() {
        return pos;
    }

    public CrewmateType getCmType(){
        return crewmateType;
    }
}
