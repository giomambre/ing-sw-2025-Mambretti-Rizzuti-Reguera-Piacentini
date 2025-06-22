package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.io.Serializable;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 */
public class Stardust extends CardAdventure implements Serializable {
    /**
     *
     * @param level
     * @param cost_of_days
     * @param type
     */
    public Stardust(int level, int cost_of_days, CardAdventureType type,String path) {
        super(level, cost_of_days, type,path);
    }

    /**
     * This method uses the {@code calculateExposedConnectors} method from the {@code Ship} class
     * and applies the result by calling {@code movePlayer} from the {@code Board}.
     *
     * @param player
     */
    /*public void execute(Player player) {
        Ship ship = player.getShip();
        board.movePlayer(player, -ship.calculateExposedConnectors());
    }*/
}
