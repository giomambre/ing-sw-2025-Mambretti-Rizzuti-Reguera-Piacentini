package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>cargo_reward: list of rewards the player receives when landing on each planet</li>
 * </ul>
 */
public class Planets extends CardAdventure {
    private List<List<Cargo>> cargo_reward;

    /**
     *
     * @param level
     * @param cost_of_days
     * @param type
     * @param board
     * @param cargo_reward
     */
    public Planets(int level, int cost_of_days, CardAdventureType type, List<List<Cargo>> cargo_reward) {
        super(level, cost_of_days, type);
        this.cargo_reward = cargo_reward;
    }

    /**
     * This function gives the rewards to the player who landed on each planet.
     * @param planets a map of the carried cargos as value and the card component where he wants to put it.
     */
    public void execute(Player player, Map<CardComponent, Map<Cargo, Integer>> planets) {
        Map<Cargo, Integer> rewards = new HashMap<>();

            board.movePlayer(player,-getCost_of_days());
            Ship ship_player = player.getShip();

            for (int i = 0; i < ship_player.getROWS(); i++) {
                for (int j = 0; j < ship_player.getCOLS(); j++) {

                    CardComponent card = ship_player.getComponent(i, j);

                    if (planets.containsKey(card)) {
                            rewards = planets.get(card);
                            ((Storage) card).addCargo(rewards);
                    }
                }
            }


    }


    public List<Cargo> getCargos(int index) {
        return cargo_reward.get(index);
    }

}
