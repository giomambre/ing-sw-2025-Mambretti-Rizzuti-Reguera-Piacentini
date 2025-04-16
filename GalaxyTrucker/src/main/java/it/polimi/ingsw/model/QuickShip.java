package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.EnumMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Universal;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;

/**This class is a sublass of BaseShip, from which it inherits attributes and methods.
 *It represents the QUICK SHIP (it’s a learning flight) during the various phases.
 */
public class QuickShip extends BaseShip{

    public QuickShip(Player player) {
        super(player);
        this.cols=7;
        this.rows=5;
    }

    /**
     * This method is used to initialize the plance
     */
    public  void initializeShipPlance(){
        ComponentType main_unit;
        switch (player.getColor()) {
            case RED:
                main_unit = MainUnitRed;
                break;
            case YELLOW:
                main_unit = MainUnitYellow;
                break;
            case GREEN:
                main_unit = MainUnitGreen;
                break;
            default:
                main_unit = MainUnitBlue;
                break;
        }
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        CardComponent NOT_ACCESSIBLE_CELL = new CardComponent(NotAccessible, connectors);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                if (row == 2 && col == 3) {
                    connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Universal);
                    connectors.put(South, Universal);
                    connectors.put(East, Universal);
                    connectors.put(West, Universal);

                    ship_board[row][col] = new LivingUnit(main_unit, connectors);

                } else if ((row == 0 && (col == 0 || col == 1 || col == 2 || col == 4 || col == 5 || col == 6)) ||
                           (row == 1 && (col == 0 || col == 1 || col == 5 || col == 6 )) ||
                           (row == 2 && (col == 0 || col == 6)) || (row == 3 && (col == 0 || col == 6)) || (row == 4 && (col == 0 || col == 3 || col == 6))){
                    ship_board[row][col] = NOT_ACCESSIBLE_CELL;
                } else {
                    ship_board[row][col] = EMPTY_CELL;
                }

            }
        }
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

    /**
     * Removes the component located at the specified position on the ship's board by replacing it with an empty cell.
     * @param x the row index of the component to remove
     * @param y the column index of the component to remove
     */
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
