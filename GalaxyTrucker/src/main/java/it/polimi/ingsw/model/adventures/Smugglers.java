package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;

import java.util.List;
import java.util.Map;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>cannons_strenght: the cannon power of each player</li>
 *     <li>cargo_rewards: the list of cargo that the player can load onto the ship</li>
 *     <li>cargo_loss: how much cargo the player loses</li>
 * </ul>
 */
public class Smugglers extends CardAdventure {
    private int cannons_strenght;
    private List<Cargo> cargo_rewards;
    private int cargo_loss;

    /**
     * @param level        must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     * @param cannons_strenght
     * @param cargo_rewards
     * @param cargo_loss
     */
    public Smugglers(int level, int cost_of_days, CardAdventureType type, Board board, int cannons_strenght, List<Cargo> cargo_rewards, int cargo_loss) {
        super(level, cost_of_days, type, board);
        this.cannons_strenght = cannons_strenght;
        this.cargo_rewards = cargo_rewards;
        this.cargo_loss = cargo_loss;
    }

    /**
     * This method is called when the player's cannon strenght is greater than the enemy's.
     * If the player decides to claim the reward, the method first calls the 'getShip' function of the player in order to get the player's ship.
     * It moves the player back by 'getCost_of_days' positions using the 'movePlayer' function,
     * and finally allows adding the cargo through the 'addCargo' function at the 'new_cargo_position' position.
     *
     * @param player
     * @param new_cargo_position
     * @param choice the boolean that rappresents the player's desire to recive the reward or not.
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
     * The method first calls the 'getShip' function of the player in order to get the player's ship.
     * It moves the player back by 'getCost_of_days' positions using the 'movePlayer' function,
     * and finally removes cargo through the 'removeCargo' function from the 'cargo_position' position.
     *
     * @param player
     * @param cargo_position
     */
    public void executeLoss(Player player, Map<CardComponent, Map<Cargo, Integer>> cargo_position) {
        Ship ship_player = player.getShip();
        board.movePlayer(player, -getCost_of_days());
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
}

