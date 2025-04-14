package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.components.CardComponent;

import java.util.UUID;

import it.polimi.ingsw.model.components.CardComponent;
import javafx.util.Pair;

import java.util.UUID;

public class AddCardMessage extends StandardMessageClient{
    private CardComponent cardComponent;
    private Pair<Integer,Integer> pos;
    public AddCardMessage(MessageType type, String content, UUID id, CardComponent cardComponent, Pair<Integer,Integer> pos) {
        super(type, content,id);
        this.cardComponent = cardComponent;
        this.pos = pos;
    }

    public CardComponent getCardComponent() {
        return cardComponent;
    }
    public Pair<Integer,Integer> getPos() { return pos; }
}

