package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.enumerates.Color;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class GameStartedMessage extends Message implements Serializable {

    List<Color> availableColors;
    public GameStartedMessage(MessageType type , String content, List<Color> availableColors) {
        super(type, content);
        this.availableColors = availableColors;

    }

public List<Color> getAvailableColors() {
    return availableColors;
}
}