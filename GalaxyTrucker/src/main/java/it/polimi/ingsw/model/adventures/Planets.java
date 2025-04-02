package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;

import java.util.ArrayList;
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
    public Planets(int level, int cost_of_days, CardAdventureType type, Board board, List<List<Cargo>> cargo_reward) {
        super(level, cost_of_days, type, board);
        this.cargo_reward = cargo_reward;
    }

    //la parte di gestione delle posizioni dei cargo view??
    /**
     * This function gives the rewards to the player who landed on each planet.
     * @param players a map containing the players as keys and a map of the carried cargos as value.
     */
    public void execute(Map<Player, Map<CardComponent, Map<Cargo, Integer>>> players) {
        List<Cargo> totalRewards = new ArrayList<>();

        for (Player player : players.keySet()) {

            board.movePlayer(player,-getCost_of_days());
            Ship ship_player = player.getShip();


            for (int i = 0; i < ship_player.getROWS(); i++) {
                for (int j = 0; j < ship_player.getCOLS(); j++) {

                    CardComponent card = ship_player.getComponent(i, j);

                    if (players.get(player).containsKey(card)) {
                        Map<Cargo, Integer> rewards = players.get(player).get(card);

                        ((Storage) card).addCargo(rewards);
                    }
                }
            }
        }

    }


    public List<Cargo> getCargos(int index) {
        return cargo_reward.get(index);
    }

}
