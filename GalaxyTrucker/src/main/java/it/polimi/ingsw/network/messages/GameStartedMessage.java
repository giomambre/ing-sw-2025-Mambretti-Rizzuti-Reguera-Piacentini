package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.enumerates.Color;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class GameStartedMessage extends Message implements Serializable {

    List<Color> avaible_colors;
    public GameStartedMessage(MessageType type , String content, List<Color> avaiable_colors) {
        super(type, content);
        this.avaible_colors = avaiable_colors;

    }

public List<Color> getAvaiable_colors() {
        return avaible_colors;
}

}
