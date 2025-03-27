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
 *     <li>needed_crewmates: how many crewmates are required to use this adventure card</li>
 *     <li>cargo_reward: the list of cargo that the player can load onto the ship</li>
 * </ul>
 */
public class AbandonedStation extends  CardAdventure{

    private int needed_crewmates ;
    private List<Cargo> cargo_reward; //da capire come implementare quali l utente accetta e quali rifiuta

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     * @param needed_crewmates
     * @param cargo_reward
     */

    public AbandonedStation(int level, int cost_of_days, CardAdventureType type , int needed_crewmates, Board board, List<Cargo> cargo_reward) {
        super(level, cost_of_days,type ,board);
    this.needed_crewmates = needed_crewmates;
    this.cargo_reward = cargo_reward;


    }

    /**
     * This method is called when a player has enought crewmates to use this card and decides to use it.
     * It moves the player back by cost_of_days positions through the 'movePlayer' function of board.
     * It allows the player to receive cargo_reward cargo, calling the 'addCargo' function of storage.
     *
     * @param player
     * @param new_cargo_positions
     */
//eventuale controllo se nessuno accetta la carte, da fare nel controller, tutto rimane invariato nel model
    public void execute(Player player, Map<CardComponent, Map<Cargo,Integer>> new_cargo_positions) {

        Ship ship_player = player.getShip();
        board.movePlayer(player, -getCost_of_days());
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {

                CardComponent card = ship_player.getComponent(i, j);
                for (CardComponent storage : new_cargo_positions.keySet()) {

                    if (card.equals(storage)) {
                        ((Storage) storage).addCargo(new_cargo_positions.get(storage));
                    }



                }
            }

        }
    }


    public List<Cargo> getCargo() {
        return cargo_reward;
    }

    public int getNeeded_crewmates() {
        return needed_crewmates;
    }








}
