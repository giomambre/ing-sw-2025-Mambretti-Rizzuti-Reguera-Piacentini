package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.CardAdventure;

import java.util.ArrayList;
import java.util.List;

public class QuickGame extends BaseGame {
    private List<CardAdventure> deck_adventure = new ArrayList<>();

    /**
     * this method is called to start the flight phase putting al the rockets on the board
     */
    public void startFlight() {
        board = new Board(active_players,18);
    }
}
