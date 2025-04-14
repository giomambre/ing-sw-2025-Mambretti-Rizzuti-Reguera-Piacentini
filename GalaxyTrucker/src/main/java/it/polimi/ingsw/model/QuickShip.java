package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.EnumMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.ComponentType.Empty;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Cannon_Connector;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Empty_Connector;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;

public class QuickShip extends BaseShip{

    public QuickShip(Player player) {
        super(player);
        this.cols=7;
        this.rows=5;
    }

    public  void initializeShipPlance(){
        //cice
    }

    /**
     * This method calculates the cannon's power based on its type (Cannon or DoubleCannon).
     * @param battery_usage the batteries used by the player in case he decides to activate the double cannon
     * @return power
     */
    public double calculateCannonPower(Map<CardComponent, Boolean> battery_usage) { //after the validity check , get battery_usage from controller i think
        double power = 0;
        CardComponent tmp;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tmp = ship_board[row][col];

                switch (tmp.getComponentType()) {

                    case Cannon:

                        if (tmp.getConnector(North) == Cannon_Connector) power++;
                        else power += 0.5;
                        break;

                    case DoubleCannon:
                        double local_power = 0;
                        boolean useBattery = battery_usage.getOrDefault(tmp, false);

                        if (tmp.getConnector(North) == Cannon_Connector) local_power++;

                        else local_power += 0.5;

                        if (useBattery) local_power *= 2;

                        power += local_power;
                        break;

                    default:
                        break;

                }

            }
        }
        return power;
    }

    /**
     * This method calculates the engine's power based on its type (Engine or DoubleEngine).
     * @param battery_usage the batteries used by the player in case he decides to activate the double engine
     * @return power
     */
    public double calculateEnginePower(Map<CardComponent, Boolean> battery_usage) { //after the validity check
        double power = 0;
        CardComponent tmp;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                tmp = this.getComponent(row, col);

                switch (tmp.getComponentType()) {

                    case Engine:

                        power++;
                        break;

                    case DoubleEngine:

                        boolean useBattery = battery_usage.getOrDefault(tmp, false);

                        if (useBattery) power += 2;
                        else power += 1;
                        break;

                    default:
                        break;
                }

            }
        }

        return power;

    }

    @Override
    public void removeComponent(int x, int y) {
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);

        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        ship_board[x][y] = EMPTY_CELL;
    }


}
