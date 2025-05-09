package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.adventures.CardAdventure;

public class AdventureCardMessage extends Message {
    public CardAdventure adventure;

    public AdventureCardMessage(MessageType type, String content, CardAdventure adventure) {
        super(type, content);
        this.adventure = adventure;

    }
    public CardAdventure getAdventure() {
        return adventure;
    }

}
