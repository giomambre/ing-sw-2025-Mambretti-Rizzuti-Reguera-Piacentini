package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.components.CardComponent;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ShipPiecesMessage extends Message {

    List<List<Pair<Integer, Integer>>> pieces;
    public ShipPiecesMessage(MessageType type, String content, List<List<Pair<Integer, Integer>>> pieces) {
        super(type, content);
        this.pieces = pieces;

    }


    public List<List<Pair<Integer, Integer>>> getPieces() {
        return pieces;
    }
}
