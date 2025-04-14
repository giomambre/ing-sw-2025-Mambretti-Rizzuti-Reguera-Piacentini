package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Shield;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;

import static it.polimi.ingsw.model.enumerates.CrewmateType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;

/**
 * This class implements the ship of the player.
 * <ul>
 *     <li>ship_board: the place where the ship is built </li>
 *     <li>extra_components: contains the secured but not used components and the parts of the ship that were eliminated</li>
 *     <li>player: the owner of the ship</li>
 * </ul>
 */
public class Ship extends BaseShip{

    public Ship(Player player) {
        super(player);
        this.cols=7;
        this.rows=5;
    }


    /**
     * This method is used to initialize the plance
     */
    public void initializeShipPlance() {


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
                    ((LivingUnit)getComponent(row,col)).addAstronauts(); //already filled the main unit with 2 astounauts, no choices here

                } else if (row == 0 && (col == 0 || col == 1 || col == 3 || col == 5 || col == 6)) {
                    ship_board[row][col] = NOT_ACCESSIBLE_CELL;
                } else if ((row == 1 && (col == 0 || col == 6)) || (row == 4 && col == 3)) {
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
                        double local_power = 0; //
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
        if (power > 0 && findPinkAlien()) {
            power += 2;
        }

        return power;

    }

    /**
     * This method checks for the presence of pink alien
     * @return boolean
     */
    public boolean findPinkAlien() {
        return findAlien(PinkAlien, PinkAlienUnit);
    }

    /**
     * This method checks for the presence of brown alien
     * @return boolean
     */
    public boolean findBrownAlien(){
        return findAlien(BrownAlien, BrownAlienUnit);
    }

    /**
     * This method checks for the presence of alien
     * @return boolean
     */
    public boolean findAlien(CrewmateType alienType, ComponentType alienUnitType) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Salta le colonne agli estremi (0 e 6)
                if (col == 0 || col == 6) {
                    continue;
                }

                CardComponent component = this.getComponent(row, col);

                // Verifica che il componente sia un'unità alieno del tipo specificato
                if (component.getComponentType() != LivingUnit ||
                        ((LivingUnit) component).getCrewmateType() != alienType) {
                    continue;
                }

                // Controlla se ha un vicino dello stesso tipo con connettori compatibili
                if (hasAdjacentAlien(row, col, alienUnitType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * this method checks whether the component card is adjacent to an alien unit.
     * @param row
     * @param col
     * @param alienUnitType
     * @return boolean
     */
    private boolean hasAdjacentAlien(int row, int col, ComponentType alienUnitType) {
        // Direzioni: sopra, destra, sotto, sinistra
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        CardComponent component = this.getComponent(row, col);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Verifica che la posizione sia valida
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                CardComponent neighbor = this.getComponent(newRow, newCol);

                // Verifica se è un'unità alieno del tipo specificato
                if (neighbor.getComponentType() == alienUnitType) {
                    // Determina i bordi da controllare in base alla direzione
                    ConnectorType currentConnector;
                    ConnectorType neighborConnector;

                    if (dir[0] == -1) {  // Sopra
                        currentConnector = component.getConnector(North);
                        neighborConnector = neighbor.getConnector(South);
                    } else if (dir[0] == 1) {  // Sotto
                        currentConnector = component.getConnector(South);
                        neighborConnector = neighbor.getConnector(North);
                    } else if (dir[1] == -1) {  // Sinistra
                        currentConnector = component.getConnector(West);
                        neighborConnector = neighbor.getConnector(East);
                    } else {  // Destra
                        currentConnector = component.getConnector(East);
                        neighborConnector = neighbor.getConnector(West);
                    }

                    // Verifica la compatibilità dei connettori
                    if (component.getValidConnectors(currentConnector).contains(neighborConnector)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        if (power > 0 && findBrownAlien()) {
            power += 2;
        }
        return power;

    }

    /**
     * This method is used when a component card is destroyed or has invalid connections.
     * @param x row
     * @param y col
     */
    public void removeComponent(int x, int y) {
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);




        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        if(this.getComponent(x, y).getComponentType() ==PinkAlienUnit || this.getComponent(x, y).getComponentType() ==BrownAlienUnit ) {
             for(int row=0; row<rows; row++) {
                 for(int col=0 ;col<cols; col++) {
                     CardComponent card = getComponent(row, col);

                     if(ship_board[row][col].getComponentType() == LivingUnit
                             && ((LivingUnit)card).getCrewmateType() != Astronaut
                             && ((LivingUnit)card).getNum_crewmates()>0 ) {
                         System.out.println(checkAlienSupport(card));

                         if(!checkAlienSupport(card).contains(((LivingUnit)card).getCrewmateType())){
                             //kill the alien
                             ((LivingUnit)card).removeCrewmates(1);

                         }

                     }


                 }
             }
        }


        ship_board[x][y] = EMPTY_CELL;
    }

    /**
     * This method allows adding aliens to the 'LivingUnit' component card only if it is properly connected to a support
     * @param living_unit
     * @return list of alien added in the ship
     */
    public List<CrewmateType> checkAlienSupport(CardComponent living_unit) {
        List<CrewmateType> crew = new ArrayList<>();

        if (living_unit.getComponentType() != LivingUnit)

            System.out.println("cannot check alien support, this is not a living unit!");

        Pair<Integer, Integer> x_y = getCoords(living_unit);
        int x = x_y.getKey();
        int y = x_y.getValue();

        living_unit = this.getComponent(x, y);

        if (x!=4 && this.getComponent(x+1,y).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(North)).contains(this.getComponent(x+1,y).getConnector(South)))
            crew.add(PinkAlien);
        else if (x!=4 && this.getComponent(x+1,y).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(North)).contains(this.getComponent(x+1,y).getConnector(South))) {
            crew.add(BrownAlien);
        }

        if (x!=0 && this.getComponent(x-1,y).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(South)).contains(this.getComponent(x+1,y).getConnector(North)))
            crew.add(PinkAlien);
        else if (x!=0 && this.getComponent(x-1,y).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(South)).contains(this.getComponent(x+1,y).getConnector(North))) {
            crew.add(BrownAlien);
        }

        if (y!=6 && this.getComponent(x,y+1).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(East)).contains(this.getComponent(x+1,y).getConnector(West)))
            crew.add(PinkAlien);
        else if (y!=6 && this.getComponent(x,y+1).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(East)).contains(this.getComponent(x+1,y).getConnector(West))) {
            crew.add(BrownAlien);
        }

        if (y!=0 && this.getComponent(x,y-1).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(West)).contains(this.getComponent(x+1,y).getConnector(East)))
            crew.add(PinkAlien);
        else if (y!=0 &&this.getComponent(x,y-1).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(West)).contains(this.getComponent(x+1,y).getConnector(East))) {
            crew.add(BrownAlien);
        }

        return crew;
    }


}
