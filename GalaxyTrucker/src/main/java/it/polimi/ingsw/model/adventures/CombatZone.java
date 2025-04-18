package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.North;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>crewmates_loss: how many crewmates the player with the weakest engines loses</li>
 *     <li>cargo_loss: how many cargo units the player with the least engine power loses</li>
 *     <li> meteors: a list of pairs, each containing a meteor type and the direction it comes from.
 *     Depending on the type of Combat Zone card, the meteors threaten either the player with the weakest cannons
 *     or the player with the fewest crew members.</li>
 * </ul>
 */
public class CombatZone extends CardAdventure{
    private int crewmates_loss;
    private int cargo_loss;
    private int id;
    private List<Pair<MeteorType, Direction>> meteors;

    /**
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose. Can be =0
     * @param type
     * @param board
     * @param crewmates_loss
     * @param cargo_loss
     * @param meteors
     */
    public CombatZone(int level, int cost_of_days, CardAdventureType type,int id, int crewmates_loss, int cargo_loss, List<Pair<MeteorType, Direction>> meteors) {
        super(level, cost_of_days, type);
        this.crewmates_loss=crewmates_loss;
        this.cargo_loss=cargo_loss;
        this.meteors = meteors;
        this.id = id;
    }

    /**
     * This method decreases the number of flight days for the player who has the fewest crew.
     */
    public void executeLessCrewmates1(Player player) {
        board.movePlayer(player, -getCost_of_days());
    }

    /**
     * This method applies a crew loss penalty to the player with the weakest engine power.
     * @param player the player with the weakest engine power
     * @param astronaut_losses a map linking ship components to the number of crew members to remove
     */
    public void executeLessEnginePower1(Player player, Map<CardComponent,Integer> astronaut_losses) {

        Ship ship_player = player.getShip();
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {
                CardComponent component = ship_player.getComponent(i, j);

                for (CardComponent unit : astronaut_losses.keySet()) {
                    if (component.equals(unit)){

                        ((LivingUnit) component).removeCrewmates(astronaut_losses.get(unit)); // occhio al cast Exception

                    }
                }
            }
        }
    }

    // LessCrewmates2 e LessCannonPower1 vengono gestiti con la execute di meteorswarm
    /**
     * This method applies a cargo loss penalty to the player with the weakest engine power.
     * @param player the player with the weakest engine power
     * @param cargo_position a map linking storage components to a sub-map of cargo types and their quantities to remove
     */
    public void executeLessEnginePower2(Player player, Map<CardComponent, Map<Cargo, Integer>> cargo_position) {

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

    /**@return a list of pair of meteor type and its direction*/
    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }

    /**
     * This method decreases the number of flight days for the player who has the lowest cannon strength.
     */
    public void executeLessCannonPower2(Player player) {
        board.movePlayer(player, -getCost_of_days());
    }

    public int getId(){
        return id;
    }

    public int getCrewmates_loss() {
        return crewmates_loss;
    }

    public int getCargo_loss() {
        return cargo_loss;
    }
}
