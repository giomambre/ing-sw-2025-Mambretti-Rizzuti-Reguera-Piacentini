package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.util.List;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>meteors: a list in which each element is a pair consisting of a value of type meteorType and one of type direction</li>
 * </ul>
 */
public class MeteorSwarm extends CardAdventure {

    private List<Pair<MeteorType, Direction>> meteors;

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     * @param meteors
     */
    public MeteorSwarm(int level, int cost_of_days, CardAdventureType type, Board board, List<Pair<MeteorType, Direction>> meteors) {
        super(level, cost_of_days, type, board);
        this.meteors = meteors;

    }

    /**
     * This method first checks that a valid position value comes out of the dice roll,
     * then it checks for each meteor type if there is a component card in the resulting position.
     * The method foresees three different cases based on a specific meteor type
     *
     * @param player
     * @param direction
     * @param meteor_type
     * @param shield_usage
     * @param battery
     * @param position
     * @param double_cannon_usage a boolean
     */
    public void execute(Player player, Direction direction, MeteorType meteor_type, Boolean shield_usage, CardComponent battery, int position, Boolean double_cannon_usage) {

        if ((direction == Direction.North || direction == Direction.South) && (position < 4 || position > 10)) return;
        if ((direction == Direction.East || direction == Direction.West) && (position < 5 || position > 9)) return;

        Ship ship = player.getShip();

        CardComponent hitted_card = ship.getFirstComponent(direction, position);

        if (hitted_card.getComponentType() == ComponentType.NotAccessible) return; // non ci sono pezzi colpiti

        switch (meteor_type) {


            case SmallMeteor:
                if (shield_usage) {
                    ((Battery) battery).removeBattery();
                    break;
                } else if (hitted_card.getConnector(direction) == ConnectorType.Smooth) break;

                else ship.removeComponent(ship.getCoords(hitted_card).getKey(), ship.getCoords(hitted_card).getValue());

                break;

            case LargeMeteor:
                if (hitted_card.getComponentType() == ComponentType.Cannon
                        && hitted_card.getConnector(direction) == ConnectorType.Cannon_Connector) break;
                if (hitted_card.getComponentType() == ComponentType.DoubleCannon) {
                    ((Battery) battery).removeBattery();
                    break;
                }
                ship.removeComponent(ship.getCoords(hitted_card).getKey(), ship.getCoords(hitted_card).getValue());
                break;



                case LightCannonFire:

                    if (shield_usage) {
                        ((Battery) battery).removeBattery();
                        break;
                    }

                    else ship.removeComponent(ship.getCoords(hitted_card).getKey(), ship.getCoords(hitted_card).getValue());

                    break;

            case HeavyCannonFire:

                ship.removeComponent(ship.getCoords(hitted_card).getKey(), ship.getCoords(hitted_card).getValue());
                break;

        }


    }


}
