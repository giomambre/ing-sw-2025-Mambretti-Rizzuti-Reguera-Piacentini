package it.polimi.ingsw.network.messages;

import java.io.Serializable;
import java.util.UUID;

public class StandardMessageClient extends Message implements Serializable {
    UUID id_client;
    public StandardMessageClient(MessageType type , String content, UUID sender) {
        super(type, content);
        this.id_client = sender;
    }

    public UUID getId_client() {
        return id_client;
    }

    public void setId_client(UUID id_client) {
        this.id_client = id_client;
    }
}
