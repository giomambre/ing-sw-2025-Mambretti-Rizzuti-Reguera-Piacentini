package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateLobbyMessage extends StandardMessageClient implements Serializable {

    int limit ;



    public CreateLobbyMessage(MessageType type, String content, UUID sender,int limit) {
        super(type, content,sender);

        this.limit = limit;

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
