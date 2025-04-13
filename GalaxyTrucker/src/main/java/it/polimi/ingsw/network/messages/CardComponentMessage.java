package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.components.CardComponent;

import java.util.UUID;

public class CardComponentMessage extends StandardMessageClient{
    private CardComponent cardComponent;
    public CardComponentMessage(MessageType type, String content, UUID id, CardComponent cardComponent) {
        super(type, content,id);
        this.cardComponent = cardComponent;
    }
    public CardComponent getCardComponent() {
        return cardComponent;
    }
}
