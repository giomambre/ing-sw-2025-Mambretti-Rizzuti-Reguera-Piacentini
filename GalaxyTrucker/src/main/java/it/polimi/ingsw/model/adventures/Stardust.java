package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.io.Serializable;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 */
public class Stardust extends CardAdventure implements Serializable {
    /**
     *
     * @param level
     * @param cost_of_days
     * @param type
     * @param board
     */
    public Stardust(int level, int cost_of_days, CardAdventureType type) {
        super(level, cost_of_days, type);
    }

    /**
     * This method calls the 'movePlayer' function from Board to move the player back by number of exposed connectors positions
     * calculated by the 'calculateExposedConnectors' function of Ship
     * @param player
     * @param ship
     */
    public void execute(Player player, Ship ship) {
        board.movePlayer(player, -ship.calculateExposedConnectors());
    }
}
