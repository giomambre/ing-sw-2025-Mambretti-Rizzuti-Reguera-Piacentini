package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>cargo_reward: list of rewards the player receives when landing on each planet</li>
 * </ul>
 */
public class Planets extends CardAdventure implements Serializable {
    private List<List<Cargo>> cargo_reward;

    /**
     * @param level
     * @param cost_of_days
     * @param type
     * @param cargo_reward the list of cargo rewards available for each planet
     */
    public Planets(int level, int cost_of_days, CardAdventureType type, List<List<Cargo>> cargo_reward, String path) {
        super(level, cost_of_days, type,path);
        this.cargo_reward = cargo_reward;
    }



    /**@return the full list of cargo rewards associated with all planets*/
    public List<List<Cargo>> getCargo_reward() {
        return cargo_reward;
    }

    /**
     * This method returns the list of cargo rewards associated with a specific planet.
     *
     * @param index the index of the planet
     * @return the list of cargo rewards for that planet
     */
    public List<Cargo> getCargos(int index) {
        return cargo_reward.get(index);
    }

}
