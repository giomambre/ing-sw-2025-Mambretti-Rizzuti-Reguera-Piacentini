package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>cannons_strenght: the cannon power of card
 *     <li>cargo_rewards: the list of cargo that the player can load onto the ship</li>
 *     <li>cargo_loss: how much cargo the player loses</li>
 * </ul>
 */
public class Smugglers extends CardAdventure implements Serializable {
    private int cannons_strenght;
    private List<Cargo> cargo_rewards;
    private int cargo_loss;

    /**
     * @param level        must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param cannons_strenght
     * @param cargo_rewards
     * @param cargo_loss
     */
    public Smugglers(int level, int cost_of_days, CardAdventureType type, int cannons_strenght, List<Cargo> cargo_rewards, int cargo_loss, String path) {
        super(level, cost_of_days, type,path);
        this.cannons_strenght = cannons_strenght;
        this.cargo_rewards = cargo_rewards;
        this.cargo_loss = cargo_loss;
    }

    /** @return the cannon strength*/
    public int getCannons_strenght() {
        return cannons_strenght;
    }

    /** @return a list of cargo rewards*/
    public List<Cargo> getCargo_rewards() {
        return cargo_rewards;
    }

    /** @return a list of lost cargo*/
    public int getCargo_loss() {
        return cargo_loss;
    }
}

