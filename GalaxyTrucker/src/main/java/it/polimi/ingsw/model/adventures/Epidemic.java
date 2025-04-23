package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.CardComponentLoader;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.io.Serializable;

import static it.polimi.ingsw.model.enumerates.Direction.*;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>ROWS: number of rows of the ship's plance</li>
 *     <li>COLS: number of rows of the ship's plance</li>
 * </ul>
 */
public class Epidemic extends CardAdventure implements Serializable {
    int ROWS=5 , COLS=7;

    /**
     *
     * @param level
     * @param cost_of_days
     * @param type
     */
    public Epidemic(int level, int cost_of_days, CardAdventureType type) {
        super(level, cost_of_days, type);
    }

    /**
     * The method iterates through all ship components, and for each valid {@code LivingUnit} component,
     * it checks the four adjacent components (up, down, left, right).
     * If both components in a pair are occupied and connected, one crewmate is removed from each.
     *
     * @param player
     */
    public void execute(Player player) {
        Ship ship = player.getShip();
        ComponentType mainUnit = ship.getComponent(2, 3).getComponentType();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = ship.getComponent(row, col);

                if (isValidLivingUnit(component, mainUnit)) {
                    removeCrewmatesIfConnected(ship, row, col, row + 1, col, North, South);
                    removeCrewmatesIfConnected(ship, row, col, row, col + 1, East, West);
                    removeCrewmatesIfConnected(ship, row, col, row, col - 1, West, East);
                    removeCrewmatesIfConnected(ship, row, col, row - 1, col, South, North);
                }
            }
        }
    }

    /**
     * This method checks if the given component is a living unit or a matches the specified main unit type.
     *
     * @param component
     * @param mainUnit
     * @return true if the component is a living unit or has the same type as the main unit.
     */
    private boolean isValidLivingUnit(CardComponent component, ComponentType mainUnit) {
        return component.getComponentType() == ComponentType.LivingUnit || component.getComponentType() == mainUnit;
    }

    /**
     * Removes one crewmate from each of two connected living units on the ship,
     * if both have at least one crewmate and are connected through the specified directions.
     *
     * @param ship
     * @param row1 the row of the first component
     * @param col1 the column of the first component
     * @param row2 the row of the second component
     * @param col2 the column of the second component
     * @param conn1 the direction of the connector on the first component
     * @param conn2 the direction of the connector on the second component
     */
    private void removeCrewmatesIfConnected(Ship ship, int row1, int col1, int row2, int col2, Direction conn1, Direction conn2) {
        if (isWithinBounds(row2, col2) && areComponentsConnected(ship, row1, col1, row2, col2, conn1, conn2)) {
            LivingUnit unit1 = (LivingUnit) ship.getComponent(row1, col1);
            LivingUnit unit2 = (LivingUnit) ship.getComponent(row2, col2);

            if (unit1.getNum_crewmates() > 0 && unit2.getNum_crewmates() > 0) {
                unit1.removeCrewmates(1);
                unit2.removeCrewmates(1);
            }
        }
    }

    /**
     * This method checks wether the specified row and column are within the valid bounds of the ship grid.
     *
     * @param row
     * @param col
     * @return true if the position is within the ship's grid; false otherwise
     */
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Checks whether two components on the ship are connected through the given directions.
     * The connection is valid if the second component is a valid living unit (or matches the main unit type),
     * and the connectors on both components are compatible in the specified directions.
     *
     * @param ship
     * @param row1 the row of the first component
     * @param col1 the column of the first component
     * @param row2 the row of the second component
     * @param col2 the column of the second component
     * @param conn1 the direction of the connector on the first component
     * @param conn2 the direction of the connector on the second component
     * @return true if the components are valid and connected through the specified directions
     */
    private boolean areComponentsConnected(Ship ship, int row1, int col1, int row2, int col2, Direction conn1, Direction conn2) {
        CardComponent comp1 = ship.getComponent(row1, col1);
        CardComponent comp2 = ship.getComponent(row2, col2);

        return isValidLivingUnit(comp2, comp1.getComponentType()) &&
                comp1.getValidConnectors(comp1.getConnector(conn1)).contains(comp2.getConnector(conn2));
    }

}
