package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Shield;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.util.Pair;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.CrewmateType.Astronaut;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.East;

/**
 * This class represents the Ship in general during the various phases of game. It's an abstract class because on run time it will be a Ship or a QuickShip
 * <ul>
 *     <li>rows: how many rows the ship's board spans for component allocation</li>
 *     <li>cols: how many rows the ship's board spans for component allocation</li>
 *     <li>ship_board: a matrix of CardComponent objects. Each element corresponds to a slot where a component can be placed.</li>
 *     <li>extra_components: a list of CardComponent reserved but not yet attached to the ship</li>
 *     <li>player: the shipowner</li>
 * </ul>
 */
public abstract class BaseShip {
    protected int rows;
    protected int cols;
    protected CardComponent[][] ship_board = new CardComponent[rows][cols];
    protected List<CardComponent> extra_components = new ArrayList<>();
    protected Player player;

    public BaseShip(Player player) {
        this.player = player;
    }

    public int getROWS() {
        return rows;
    }

    public int getCOLS() {
        return cols;
    }

    public CardComponent[][] getShipBoard() {
        return ship_board;
    }

    /**
     * Add a CardComponent to the ship.
     * @param component the one to add
     * @param row       to identify in witch row of the ship board the component will be added
     * @param col       to identify in witch col of the ship board the component will be added
     */
    public void addComponent(CardComponent component, int row, int col) {
        if (ship_board[row][col].getComponentType() == NotAccessible)
            throw new IllegalArgumentException("Position not Accessible");

        if (ship_board[row][col].getComponentType() != Empty)
            throw new IllegalArgumentException("Position already in use");

        ship_board[row][col] = component;

    }

    public abstract void initializeShipPlance();


    public abstract double calculateCannonPower(Map<CardComponent, Boolean> battery_usage);
    public abstract double calculateEnginePower(Map<CardComponent, Boolean> battery_usage);
    /**
     * This method allows getting the available batteries on the ship
     * @return list of available batteries
     */
    private List<CardComponent> getAvailableBatteries() {
        List<CardComponent> batteries = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                CardComponent component = ship_board[row][col];

                if (component != null && component.getComponentType() == ComponentType.Battery) {
                    batteries.add(component);
                }
            }
        }
        return batteries;
    }

    /**
     *This method retrieves the component card located at a specific position on the ship.
     * @param x row
     * @param y col
     * @return the card component at the given position
     */
    public CardComponent getComponent(int x, int y) {

        if(x<0 || y<0 || x>=rows || y>=cols) throw new IndexOutOfBoundsException("x or y out of bounds");
        return ship_board[x][y];
    }

    /**
     * This method checks if the connection between component cards is valid.
     * It always returns true if the component type is 'Empty' or 'NotAccessible'.
     * If the connector is an 'Engine_Connector' and the direction is 'South', the method returns false.
     * otherwise, it checks whether the connections are valid in the four directions.
     * @param x
     * @param y
     * @return boolean
     */
    public Boolean checkCorrectConnections(int x, int y) {
        CardComponent card = getComponent(x, y);
        if (card.getComponentType() == Empty || card.getComponentType() == NotAccessible) {
            return true;
        }
        for (Direction direction : Direction.values()) {

            if (card.getConnector(direction) == Engine_Connector) {

                if (direction != South) return false;

            }

            {
                switch (direction) {

                    case North:


                        if (x != 0 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x - 1, y).getConnector(South))
                        ) return false;

                        break;

                    case East:
                        if (y != cols - 1 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x, y + 1).getConnector(West))
                        ) return false;
                        break;

                    case South:
                        if (x != rows - 1 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x + 1, y).getConnector(North))
                        ) return false;
                        break;

                    case West:
                        if (y != 0 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x, y - 1).getConnector(East))
                        ) return false;
                        break;


                }
            }

        }
        return true;
    }

    /**
     * This method checks if the entire ship is properly connected.
     * @return the list of card positions with invalid connections
     */
    public List<Pair<Integer, Integer>> checkShipConnections() {


        List<Pair<Integer, Integer>> invalids = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                if (!checkCorrectConnections(row, col)) {
                    invalids.add(new Pair<>(row, col));
                }


            }
        }
        return invalids;
    }

    /**@return the number of crewmates*/
    public int getNumOfCrewmates() {
        CardComponent tmp;
        int total = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tmp = this.getComponent(row, col);
                if (tmp.getComponentType() == LivingUnit) {
                    total += ((LivingUnit) tmp).getNum_crewmates();
                }
            }

        }
        return total;
    }

    /**
     * This method checks whether the ship is protected in the specified direction.
     * @param direction
     * @return boolean
     */
    public boolean isProtected(Direction direction) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (ship_board[row][col].getComponentType() == Shield) {
                    Shield shieldObject = (Shield) ship_board[row][col];
                    if (shieldObject.getCoveredSides().get(direction) == Boolean.TRUE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method verifies the validity of a component (it must not be null, empty or inaccessible)
     * @param row
     * @param col
     * @return boolean true if it's valid
     */
    private boolean isValidComponent(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        CardComponent component = ship_board[row][col];
        return component != null && component.getComponentType() != Empty && component.getComponentType() != NotAccessible;
    }

    // DFS per trovare un pezzo connesso della nave
    /**
     * Explores all connected components starting from the specified position on the ship's board.
     * This method performs a depth-first search (DFS) to identify all adjacent components that are
     * directly connected to the starting component at coordinates (x, y). A connection is considered valid
     * if the neighboring component is not empty or inaccessible and if the connectors between the current
     * and neighboring components are compatible.
     * @param x       the row index of the starting component
     * @param y       the column index of the starting component
     * @param visited a set containing positions already visited during the exploration to prevent revisiting
     * @param piece   a list to which all positions belonging to the connected component will be added
     */
    private void explorePiece(int x, int y, Set<Pair<Integer, Integer>> visited, List<Pair<Integer, Integer>> piece) {
        Pair<Integer, Integer> pos = new Pair<>(x, y);
        if (x < 0 || x >= rows || y < 0 || y >= cols || visited.contains(pos) || !isValidComponent(x, y)) {
            return;
        }

        visited.add(pos);
        piece.add(pos);

        for (Direction dir : Direction.values()) {
            int newX = x, newY = y;

            switch (dir) {
                case North:
                    newX--;
                    break;
                case South:
                    newX++;
                    break;
                case East:
                    newY++;
                    break;
                case West:
                    newY--;
                    break;
            }

            if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                CardComponent neighbor = ship_board[newX][newY];

                if (neighbor.getComponentType() != Empty && neighbor.getComponentType() != NotAccessible
                        && ship_board[x][y].getConnector(dir) != Smooth) {

                    if (ship_board[x][y].getValidConnectors(ship_board[x][y].getConnector(dir))
                            .contains(neighbor.getConnector(getOpposite(dir)))) {
                        explorePiece(newX, newY, visited, piece);
                    }
                }
            }
        }
    }

    /**
     * This method counts the number of exposed connectors on the ship.
     * This method uses a switch statement to check each of the four directions: North, South, East and West.
     * It also examines the first and last rows and columns to determine which connectors are exposed.
     * @return the total number of exposed connectors
     */
    public int calculateExposedConnectors() {
        int exposed_connectors = 0;
        CardComponent component;


        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                component = this.getComponent(row, col);

                for (Direction direction : Direction.values()) {
                    switch (direction) {

                        case North:
                            if (row != 0 && (component.getConnector(North) == Universal || component.getConnector(North) == Double || component.getConnector(North) == Single)
                                    && this.getComponent(row - 1, col).getConnector(South) == Empty_Connector )
                                exposed_connectors++;
                            break;

                        case South:
                            if (row != 4 && (component.getConnector(South) == Universal || component.getConnector(South) == Double || component.getConnector(South) == Single)
                                    && this.getComponent(row + 1, col).getConnector(North) == Empty_Connector )
                                exposed_connectors++;

                            break;

                        case East:
                            if (col != 6 && (component.getConnector(East) == Universal || component.getConnector(East) == Double || component.getConnector(East) == Single)
                                    && this.getComponent(row, col + 1).getConnector(West) == Empty_Connector )
                                exposed_connectors++;

                            break;

                        case West:
                            if (col != 0 && (component.getConnector(West) == Universal || component.getConnector(West) == Double || component.getConnector(West) == Single)
                                    && this.getComponent(row, col - 1).getConnector(East) == Empty_Connector )
                                exposed_connectors++;

                            break;

                    }
                }


                switch (col) {
                    case 0:
                        if (component.getConnector(West) == Universal || component.getConnector(West) == Double || component.getConnector(West) == Single)
                            exposed_connectors++;

                        break;

                    case 6:
                        if (component.getConnector(East) == Universal || component.getConnector(East) == Double || component.getConnector(East) == Single)
                            exposed_connectors++;

                        break;
                }

                switch (row) {
                    case 0:
                        if (component.getConnector(North) == Universal || component.getConnector(North) == Double || component.getConnector(North) == Single)
                            exposed_connectors++;

                        break;

                    case 4:
                        if (component.getConnector(South) == Universal || component.getConnector(South) == Double || component.getConnector(South) == Single)
                            exposed_connectors++;

                        break;

                }

            }
        }

        return exposed_connectors;
    }

    /**
     * This method is used to find ship parts.
     * @return list of multiple lists of pairs
     */
    public List<List<Pair<Integer, Integer>>> findShipPieces() {
        List<List<Pair<Integer, Integer>>> pieces = new ArrayList<>(); // lista di Tronconi
        Set<Pair<Integer, Integer>> visited = new HashSet<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                Pair<Integer, Integer> currentPos = new Pair<>(row, col);

                // Se è un componente valido e non è stato visitato, avvia una nuova esplorazione DFS
                if (!visited.contains(currentPos) && isValidComponent(row, col)) {

                    List<Pair<Integer, Integer>> piece = new ArrayList<>();
                    explorePiece(row, col, visited, piece);

                    if(validatePiece(piece) ) pieces.add(piece);

                    else deletePiece(piece);
                }
            }
        }
        return pieces;
    }

    /**
     * This method validates a ship piece by checking if it contains exactly one valid engine or DoubleEngine
     * and exactly one valid living unit.
     * @param piece a list of coordinate pairs representing the positions of the component cards to validate
     * @return true if the piece contains exactly one engine and one living unit with at least one crewmate; false otherwise
     */
    public Boolean validatePiece(List<Pair<Integer, Integer>> piece) {
        int valid_cannon = 0;
        int valid_unit = 0;
        for (Pair<Integer, Integer> pos : piece) {
            int x = pos.getKey();
            int y = pos.getValue();

            CardComponent component = ship_board[x][y];
            if (component.getComponentType() == Engine || component.getComponentType() == DoubleEngine) {
                valid_cannon= 1;
            } else if (component.getComponentType() == LivingUnit && ((LivingUnit) component).getNum_crewmates() >= 1) {

                valid_unit = 1;

            }
        }

        return valid_cannon == 1 && valid_unit == 1;

    }

    /**
     * This method finds the coordinates of the given component card on the ship.
     * @param component
     * @return a pair of integers representing the (row, column) coordinates of the component
     */
    public Pair<Integer,Integer> getCoords(CardComponent component) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(this.getComponent(i, j) == component) {
                    return new Pair(i, j);
                }
            }
        }
        return new Pair(0,0);
    }

    /**
     * This method is used when a component card is destroyed or has invalid connections.
     * @param x row
     * @param y col
     */
    public abstract void removeComponent(int x, int y);

    public void setExtra_components(List<CardComponent> extraComponents) {
        this.extra_components = extraComponents;
    }

    public List<CardComponent> getExtra_components() {
        return extra_components;
    }

    public Direction getOpposite(Direction dir) {
        switch (dir) {
            case North:
                return South;
            case East:
                return West;
            case South:
                return North;
            default:
                return East;

        }
    }

    /**
     * This method is called by the adventure card 'MeteorSwarm' to determine which card is hit.
     * @param dir direction
     * @param pos row or column to search along
     * @return the first component found in the given direction and position
     */
    public CardComponent getFirstComponent(Direction dir , int pos ) {

// ritorna un component NOT ACCESSIBLE se non ce nessun componente colpito

        switch (dir) {
            case North:
                for (int i = 0; i < rows; i++) {
                    if (this.getComponent(i,pos-4).getComponentType() != ComponentType.Empty && this.getComponent(i,pos-4).getComponentType() != NotAccessible)
                        return this.getComponent(i,pos-4);
                }
                return this.getComponent(0,0);

            case South:
                for (int i = rows - 1; i >= 0; i--) {
                    if (this.getComponent(i,pos-4).getComponentType() != ComponentType.Empty && this.getComponent(i,pos-4).getComponentType() != NotAccessible)
                        return this.getComponent(i,pos-4);
                }
                return this.getComponent(0,0);

            case West:
                for (int i = 0; i < cols; i++) {
                    if (this.getComponent(pos-5,i).getComponentType() != ComponentType.Empty && this.getComponent(pos-5,i).getComponentType() != NotAccessible)
                        return this.getComponent(pos-5,i);
                }
                return this.getComponent(0,0);

            case East:
                for (int i = cols - 1; i >= 0; i--) {
                    if (this.getComponent(pos-5,i).getComponentType() != ComponentType.Empty && this.getComponent(pos-5,i).getComponentType() != NotAccessible)
                        return this.getComponent(pos-5,i);

                }
                return this.getComponent(0,0);


        }

        return this.getComponent(0,0);
    }

    /**
     * This method identifies all connected components (pieces) on the ship's board and deletes
     * all components except the one specified by the {@code choose} index.
     * @param choose the index of the component to retain; all other components will be removed
     */
    public void choosePiece(int choose){
        List<List<Pair<Integer, Integer>>> pieces = new ArrayList<>(findShipPieces());
        for (int i = 0; i < pieces.size() ; i++) {
            if (i != choose) {
                deletePiece(pieces.get(i));
            }

        }
    }

    /**
     * For each coordinate in the provided list, this method invokes {@code removeComponent(row, col)}
     * to eliminate the corresponding component from the board.
     * @param piece a list of coordinate pairs representing the connected component to be removed
     */
    public void deletePiece(List<Pair<Integer, Integer>> piece){

        for(Pair<Integer, Integer> pos : piece){
            removeComponent(pos.getKey(),pos.getValue());
        }

    }

    /**
     * Removes the component located at the specified position on the ship's board by replacing it with an empty cell.
     * @param row the row index of the component to remove
     * @param col the column index of the component to remove
     */
    public void RemoveComponent(int row, int col) {

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);

        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        ship_board[row][col] = EMPTY_CELL;

    }

    /**
     * Returns a list of coordinates for all components on the ship's board.
     * This method scans the entire board and collects the positions of all components that are neither empty nor marked as not accessible.
     * @return a list of coordinate pairs representing the non-empty and accessible components on the ship's board
     */
    public List<Pair<Integer, Integer>> printShipPlance() {
        List<Pair<Integer, Integer>> ships = new ArrayList<>();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(this.getComponent(i, j).getComponentType() != Empty
                        && this.getComponent(i, j).getComponentType() != NotAccessible) {
                    ships.add(new Pair<>(i, j));
                }
            }
        }
        return ships;
    }


}


