package it.polimi.ingsw.model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MeteorSwarm extends CardAdventure {

    private List<Pair<MeteorType, Direction>> meteors;

    public MeteorSwarm(int level, int cost_of_days, CardAdventureType type, Board board, List<Pair<MeteorType, Direction>> meteors) {
        super(level, cost_of_days, type, board);
        this.meteors = meteors;

    }

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
