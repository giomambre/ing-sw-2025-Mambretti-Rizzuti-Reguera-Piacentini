package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

public class Stardust extends CardAdventure{

    public Stardust(int level, int cost_of_days, CardAdventureType type, Board board) {
        super(level, cost_of_days, type, board);
    }

    public void execute(Player player, Ship ship) {
        board.movePlayer(player, -ship.calculateExposedConnectors());
    }
}
