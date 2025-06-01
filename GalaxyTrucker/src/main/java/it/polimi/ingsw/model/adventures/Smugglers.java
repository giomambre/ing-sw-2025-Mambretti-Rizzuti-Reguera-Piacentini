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

    /**
     * This method is called when the player's cannon strenght is greater than the enemy's.
     * If the player chooses to claim the reward, the method first retrieves the player's ship using {@code getShip}.
     * It moves the player back by the number of days defined by {@code getCost_of_days}
     * and finally allows adding the cargo to the indicated positions on the ship using {@code addCargo}.
     *
     * @param player
     * @param new_cargo_position a map linking ship components to the cargos and quantities to be added
     * @param choice a boolean indicating the player's desire to receive the reward or not.
     */
    public void executeWin(Player player, Map<CardComponent, Map<Cargo, Integer>> new_cargo_position, Boolean choice) {
        if (choice) {
            Ship ship_player = player.getShip();
            board.movePlayer(player, -getCost_of_days());
            for (int i = 0; i < ship_player.getROWS(); i++) {
                for (int j = 0; j < ship_player.getCOLS(); j++) {

                    CardComponent card = ship_player.getComponent(i, j);
                    for (CardComponent storage : new_cargo_position.keySet()) {

                        if (card.equals(storage)) {
                            ((Storage) storage).addCargo(new_cargo_position.get(storage));
                        }


                    }
                }

            }
        }

    }

    /**
     * This method is called when the player's cannon strenght is lower than the enemy's.
     * The method first retrieves the player's ship using {@code getShip},
     * then moves the player back by the number of days defined by {@code getCost_of_days},
     * and finally removes cargo from the specified components using {@code removeCargo}.
     *
     * @param player the player who suffers the penalty
     * @param cargo_position a map linking storage components to the cargos and quantities to be removed
     */
    public void executeLoss(Player player, Map<CardComponent, Map<Cargo, Integer>> cargo_position) {
        Ship ship_player = player.getShip();
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {

                CardComponent card = ship_player.getComponent(i, j);
                for (CardComponent storage : cargo_position.keySet()) {

                    if (card.equals(storage)) {
                        ((Storage) storage).removeCargo(cargo_position.get(storage));
                    }


                }
            }


        }
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

