package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.List;
import java.util.Map;

public class CardAdventureDeckMessage extends Message{
    Map<Direction, List<CardAdventure>> deck;
    public CardAdventureDeckMessage(MessageType type, String content,  Map<Direction, List<CardAdventure>> deck) {
        super(type, content);
        this.deck = deck;
    }

    public Map<Direction, List<CardAdventure>> getDeck() {
        return deck;
    }
}
