package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>meteors: a list in which each element is a pair consisting of a value of type meteorType and one of type direction</li>
 * </ul>
 */
public class MeteorSwarm extends CardAdventure implements Serializable {

    private List<Pair<MeteorType, Direction>> meteors;

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param meteors
     */
    public MeteorSwarm(int level, int cost_of_days, CardAdventureType type, List<Pair<MeteorType, Direction>> meteors, String path) {
        super(level, cost_of_days, type, path);
        this.meteors = meteors;

    }

    /**
     * This method first checks that a valid position value comes out of the dice roll,
     * then it calls the {@code getFirstComponent} function of ship to obtain the first card at the given position and direction;
     * if a component is found (i.e., not {@code NotAccessible}), it proceeds based on the type of meteor or cannon fire,
     * as described below:
     *
     * <ul>
     *     <li>SmallMeteor: the method checks {@code shield_usage} to determine whether the player is using the shield.
     *     If true, a battery is consumed using {@code removeBattery}.
     *     Otherwise,  if the connector is {@code Smooth}, nothing happens.
     *     If neither applies, the component is removed.</li>
     *
     *     <li>LargeMeteor: the method checks the component type.
     *     If the hit component is a {@code Cannon} with a {@code Cannon_Connector}, it is ignored.
     *     If it's a {@code DoubleCannon}, a battery is consumed. Otherwise, the component is removed.</li>
     *
     *     <li>LightCannonFire: the method checks if {@code shield_usage} to determine whether the player is using the shield.
     *     If true, a battery is consumed using {@code removeBattery}. Otherwise, the component is removed.</li>
     *
     *     <li>HeavyCannonFire: the component is always removed.</li>
     * </ul>
     *
     * @param player              whose ship is being hit
     * @param direction           from which the meteor arrives
     * @param meteor_type
     * @param shield_usage        true if the player is using a shield to block the hit
     * @param battery
     * @param position
     * @param double_cannon_usage true if the player is using a double cannon
     */
    /*public void execute(Player player, Direction direction, MeteorType meteor_type, Boolean shield_usage, CardComponent battery, int position, Boolean double_cannon_usage) {

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


    }*/
    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }

   /* public void execute(Player p, Direction direction, MeteorType meteorType, Boolean shieldUsage, CardComponent battery, int position, Boolean doubleCannonUsage) {
    }*/
}
