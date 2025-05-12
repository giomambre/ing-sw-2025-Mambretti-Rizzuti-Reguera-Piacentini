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
 *     <li>needed_crewmates: how many crewmates are required to use this adventure card</li>
 *     <li>cargo_reward: the list of cargo that the player can load onto the ship</li>
 * </ul>
 */
public class AbandonedStation extends  CardAdventure implements Serializable {

    private int needed_crewmates ;
    private List<Cargo> cargo_reward; //da capire come implementare quali l utente accetta e quali rifiuta

    /**
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param needed_crewmates
     * @param cargo_reward
     */
    public AbandonedStation(int level, int cost_of_days, CardAdventureType type , int needed_crewmates, List<Cargo> cargo_reward) {
        super(level, cost_of_days,type);
    this.needed_crewmates = needed_crewmates;
    this.cargo_reward = cargo_reward;
    }

    /**
     * This method is called when a player has enough crewmates and chooses to use this card.
     * It moves the player back by by the number of days specified by {@code getCost_of_days} through the {@code movePlayer} function of the board.
     * Then, it allows the player to receive cargo rewards by calling {@code addCargo} on the appropriate storage components.
     *
     * @param player who uses the card
     * @param new_cargo_positions a map linking storage components to the cargo types and quantities to be added
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

    /**@return list of cargo rewards*/
    public List<Cargo> getCargo() {
        return cargo_reward;
    }



    /**@return the number of crewmates required to use this card*/
    public int getNeeded_crewmates() {
        return needed_crewmates;
    }
public void setNeeded_crewmates(int needed_crewmates) {
        this.needed_crewmates = needed_crewmates;
}
}
