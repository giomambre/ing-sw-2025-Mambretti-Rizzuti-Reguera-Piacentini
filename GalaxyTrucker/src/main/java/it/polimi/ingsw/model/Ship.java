package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Shield;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.scene.chart.BarChart;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.*;

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
public class Ship implements Serializable {

    private int ROWS = 5, COLS = 7;
    private CardComponent[][] ship_board = new CardComponent[ROWS][COLS];
    private List<CardComponent> extra_components = new ArrayList<>();
    private Player player;

    public Ship(Player player) {
        this.player = player;
    }

    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    public CardComponent[][] getShipBoard() {
        return ship_board;
    }

    /**
     * Add a CardComponent to the ship.
     *
     * @param component the one to add
     * @param row       to identify in witch row of the ship board the component will be added
     * @param col       to identify in witch col of the ship board the component will be added
     */
    public void addComponent(CardComponent component, int row, int col) {

        //Controllo se è gia presente un elemento in quella pos, da capire se farlo nel controller o qui
        //(isa) secondo me: view permette al player di selezionare la posizione e la invia al controller, il controller verifica se è occupata
        //se si: aggiorna view e richiede al player di inserire, se no: invoca questa funzione qui


        if (ship_board[row][col].getComponentType() == NotAccessible)
            throw new IllegalArgumentException("Position not Accessible");

        if (ship_board[row][col].getComponentType() != Empty)
            throw new IllegalArgumentException("Position already in use");


        ship_board[row][col] = component;


    }


    public int getTotalBattery() {
        int total = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent card = ship_board[row][col];
                if (card.getComponentType() == Battery) {

                    if (((Battery) card).getStored() > 0)
                        total += ((Battery) card).getStored();
                }
            }
        }

        return total;
    }

    /**
     * This method is used to initialize the plance
     */
    public void initializeShipPlance() {


        ComponentType main_unit = switch (player.getColor()) {
            case RED -> MainUnitRed;
            case YELLOW -> MainUnitYellow;
            case GREEN -> MainUnitGreen;
            default -> MainUnitBlue;
        };

        String image_path = switch (player.getColor()) {
            case RED -> "/images/cardComponent/GT-mainUnitRed.jpg";
            case YELLOW -> "/images/cardComponent/GT-mainUnitYellow.jpg";
            case GREEN -> "/images/cardComponent/GT-mainUnitGreen.jpg";
            default -> "/images/cardComponent/GT-mainUnitBlue.jpg";


        };
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);


        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors, "");

        CardComponent NOT_ACCESSIBLE_CELL = new CardComponent(NotAccessible, connectors, "");
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (row == 2 && col == 3) {
                    connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Universal);
                    connectors.put(South, Universal);
                    connectors.put(East, Universal);
                    connectors.put(West, Universal);

                    ship_board[row][col] = new LivingUnit(main_unit, connectors, image_path);
                    ((LivingUnit) getComponent(row, col)).addAstronauts(); //already filled the main unit with 2 astounauts, no choices here

                } else if (row == 0 && (col == 0 || col == 1 || col == 3 || col == 5 || col == 6)) {
                    ship_board[row][col] = NOT_ACCESSIBLE_CELL;
                } else if ((row == 1 && (col == 0 || col == 6)) || (row == 4 && col == 3)) {
                    ship_board[row][col] = NOT_ACCESSIBLE_CELL;

                } else {
                    ship_board[row][col] = EMPTY_CELL;
                }

            }
        }

        player.utilePerTestare();

    }

    /**
     * This method calculates the cannon's power based on its type (Cannon or DoubleCannon).
     *
     * @param battery_usage the batteries used by the player in case he decides to activate the double cannon
     * @return power
     */
    public double calculateCannonPower(Map<CardComponent, Boolean> battery_usage) { //after the validity check , get battery_usage from controller i think
        double power = 0;
        CardComponent tmp;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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
     *
     * @return boolean
     */
    public boolean findPinkAlien() {
        return findAlien(PinkAlien, PinkAlienUnit);
    }

    /**
     * This method checks for the presence of pink alien
     *
     * @return boolean
     */
    public boolean findBrownAlien() {
        return findAlien(BrownAlien, BrownAlienUnit);
    }

    /**
     * This method checks for the presence of alien
     *
     * @return boolean
     */
    public boolean findAlien(CrewmateType alienType, ComponentType alienUnitType) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                // Salta le colonne agli estremi (0 e 6)
                if (col == 0 || col == 6) {
                    continue;
                }

                CardComponent component = this.getComponent(row, col);

                // Verifica che il componente sia un'unità alieno del tipo specificato
                if (component.getComponentType() != LivingUnit ||
                        ((LivingUnit) component).getCrewmate_type() != alienType) {
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
     *
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
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
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
     * This method allows getting the available batteries on the ship
     *
     * @return list of available batteries
     */
    private List<CardComponent> getAvailableBatteries() {
        List<CardComponent> batteries = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = ship_board[row][col];

                if (component != null && component.getComponentType() == ComponentType.Battery) {
                    batteries.add(component);
                }
            }
        }
        return batteries;
    }

    /**
     * This method calculates the engine's power based on its type (Engine or DoubleEngine).
     *
     * @param battery_usage the batteries used by the player in case he decides to activate the double engine
     * @return power
     */
    public int calculateEnginePower(Map<CardComponent, Boolean> battery_usage) { //after the validity check
        int power = 0;
        CardComponent tmp;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

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
     * This method checks if the connection between component cards is valid.
     * It always returns true if the component type is 'Empty' or 'NotAccessible'.
     * If the connector is an 'Engine_Connector' and the direction is 'South', the method returns false.
     * otherwise, it checks whether the connections are valid in the four directions.
     *
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
                        if (y != COLS - 1 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x, y + 1).getConnector(West))
                        ) return false;
                        break;

                    case South:
                        if (x != ROWS - 1 && !card.getValidConnectors(card.getConnector(direction)).contains(getComponent(x + 1, y).getConnector(North))
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
     *
     * @return the list of card positions with invalid connections
     */
    public List<Pair<Integer, Integer>> checkShipConnections() {


        List<Pair<Integer, Integer>> invalids = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (!checkCorrectConnections(row, col)) {
                    invalids.add(new Pair<>(row, col));
                }


            }
        }
        return invalids;
    }

    /**
     * @return the number of crewmates
     */
    public int getNumOfCrewmates() {
        CardComponent tmp;
        int total = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                tmp = this.getComponent(row, col);
                if (tmp instanceof LivingUnit) {
                    total += ((LivingUnit) tmp).getNum_crewmates();
                }
            }

        }
        return total;
    }
    //  public List<Pair<Integer, Integer>> checkShipValidity(){


    //}

    /**
     * This method checks whether the ship is protected in the specified direction.
     *
     * @param direction
     * @return boolean
     */
    public boolean isProtected(Direction direction) {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (ship_board[row][col].getComponentType() == Shield) {
                    Shield shieldObject = (Shield) ship_board[row][col];
                    if (shieldObject.getCoveredSides().get(direction)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method is used to find ship parts.
     *
     * @return list of multiple lists of pairs
     */
    public List<List<Pair<Integer, Integer>>> findShipPieces() {
        List<List<Pair<Integer, Integer>>> pieces = new ArrayList<>(); // lista di Tronconi
        Set<Pair<Integer, Integer>> visited = new HashSet<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                Pair<Integer, Integer> currentPos = new Pair<>(row, col);

                // Se è un componente valido e non è stato visitato, avvia una nuova esplorazione DFS
                if (!visited.contains(currentPos) && isValidComponent(row, col)) {

                    List<Pair<Integer, Integer>> piece = new ArrayList<>();
                    explorePiece(row, col, visited, piece);

                    if (validatePiece(piece)) pieces.add(piece);

                    else deletePiece(piece);
                }
            }
        }
        return pieces;
    }

    /**
     * This method verifies the validity of a component (it must not be null, empty or inaccessible)
     *
     * @param row
     * @param col
     * @return boolean
     */
    private boolean isValidComponent(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return false;
        CardComponent component = ship_board[row][col];
        return component != null && component.getComponentType() != Empty && component.getComponentType() != NotAccessible;
    }

    /**
     * Explores all connected components starting from the specified position on the ship's board.
     * This method performs a depth-first search (DFS) to identify all adjacent components that are
     * directly connected to the starting component at coordinates (x, y). A connection is considered valid
     * if the neighboring component is not empty or inaccessible and if the connectors between the current
     * and neighboring components are compatible.
     *
     * @param x       the row index of the starting component
     * @param y       the column index of the starting component
     * @param visited a set containing positions already visited during the exploration to prevent revisiting
     * @param piece   a list to which all positions belonging to the connected component will be added
     */
    private void explorePiece(int x, int y, Set<Pair<Integer, Integer>> visited, List<Pair<Integer, Integer>> piece) {
        Pair<Integer, Integer> pos = new Pair<>(x, y);
        if (x < 0 || x >= ROWS || y < 0 || y >= COLS || visited.contains(pos) || !isValidComponent(x, y)) {
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

            if (newX >= 0 && newX < ROWS && newY >= 0 && newY < COLS) {
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
     *
     * @return the total number of exposed connectors
     */
    public int calculateExposedConnectors() {
        int exposed_connectors = 0;
        CardComponent component;


        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                component = this.getComponent(row, col);

                for (Direction direction : Direction.values()) {
                    switch (direction) {

                        case North:
                            if (row != 0 && (component.getConnector(North) == Universal || component.getConnector(North) == Double || component.getConnector(North) == Single)
                                    && this.getComponent(row - 1, col).getConnector(South) == Empty_Connector)
                                exposed_connectors++;
                            break;

                        case South:
                            if (row != 4 && (component.getConnector(South) == Universal || component.getConnector(South) == Double || component.getConnector(South) == Single)
                                    && this.getComponent(row + 1, col).getConnector(North) == Empty_Connector)
                                exposed_connectors++;

                            break;

                        case East:
                            if (col != 6 && (component.getConnector(East) == Universal || component.getConnector(East) == Double || component.getConnector(East) == Single)
                                    && this.getComponent(row, col + 1).getConnector(West) == Empty_Connector)
                                exposed_connectors++;

                            break;

                        case West:
                            if (col != 0 && (component.getConnector(West) == Universal || component.getConnector(West) == Double || component.getConnector(West) == Single)
                                    && this.getComponent(row, col - 1).getConnector(East) == Empty_Connector)
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
     * This method retrieves the component card located at a specific position on the ship.
     *
     * @param x row
     * @param y col
     * @return the card component at the given position
     */
    public CardComponent getComponent(int x, int y) {

        if (x < 0 || y < 0 || x >= ROWS || y >= COLS) throw new IndexOutOfBoundsException();
        return ship_board[x][y];
    }

    /**
     * This method validates a ship piece by checking if it contains exactly one valid engine or DoubleEngine
     * and exactly one valid living unit.
     *
     * @param piece a list of coordinate pairs representing the positions of the component cards to validate
     * @return true if the piece contains exactly one engine and one living unit with at least one crewmate; false otherwise
     */
    public Boolean validatePiece(List<Pair<Integer, Integer>> piece) {
        int valid_cannon = 0;
        int valid_unit = 0;
        ComponentType util = switch (player.getColor()) {

            case GREEN -> MainUnitGreen;
            case BLUE -> MainUnitBlue;
            case RED -> MainUnitRed;
            case YELLOW -> MainUnitYellow;

        };

        for (Pair<Integer, Integer> pos : piece) {
            int x = pos.getKey();
            int y = pos.getValue();

            CardComponent component = ship_board[x][y];
            if (component.getComponentType() == Engine || component.getComponentType() == DoubleEngine) {
                valid_cannon = 1;
            } else if (component.getComponentType() == LivingUnit && ((LivingUnit) component).getNum_crewmates() >= 1) {

                valid_unit = 1;

            } else if (component.getComponentType() == util && ((LivingUnit) component).getNum_crewmates() >= 1) {
                valid_unit = 1;
            }
        }

        return valid_cannon == 1 && valid_unit == 1;

    }

    /**
     * This method finds the coordinates of the given component card on the ship.
     *
     * @param component
     * @return a pair of integers representing the (row, column) coordinates of the component
     */
    public Pair<Integer, Integer> getCoords(CardComponent component) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (ship_board[i][j] == component) {
                    return new Pair<>(i, j);
                }
            }
        }
        return new Pair<>(0, 0);
    }

    /**
     * This method is used when a component card is destroyed or has invalid connections.
     *
     * @param x row
     * @param y col
     */
    public void removeComponent(int x, int y) {
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);


        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors, "");

        if (this.getComponent(x, y).getComponentType() == PinkAlienUnit || this.getComponent(x, y).getComponentType() == BrownAlienUnit) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    CardComponent card = getComponent(row, col);

                    if (ship_board[row][col].getComponentType() == LivingUnit
                            && ((LivingUnit) card).getCrewmate_type() != Astronaut
                            && ((LivingUnit) card).getNum_crewmates() > 0) {
                        System.out.println(checkAlienSupport(card));

                        if (!checkAlienSupport(card).contains(((LivingUnit) card).getCrewmate_type())) {
                            //kill the alien
                            ((LivingUnit) card).removeCrewmates(1);

                        }

                    }


                }
            }
        }

        player.secureComponent(ship_board[x][y]);
        ship_board[x][y] = EMPTY_CELL;
    }

    /**
     * Sets the list of extra components that are not placed on the board.
     *
     * @param extraComponents
     */
    public void setExtra_components(List<CardComponent> extraComponents) {
        this.extra_components = extraComponents;
    }

    /**
     * @return list of extra components
     */
    public List<CardComponent> getExtra_components() {
        return extra_components;
    }

    /**
     * Returns the opposite direction of the one provided.
     *
     * @param dir the direction to invert
     * @return the opposite Direction (North <-> South, East <-> West)
     */
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
     *
     * @param dir direction
     * @param pos row or column to search along
     * @return the first component found in the given direction and position
     */
    public Pair<Integer,Integer> getFirstComponent(Direction dir, int pos) {

// ritorna un component NOT ACCESSIBLE se non ce nessun componente colpito

        switch (dir) {
            case North:
                for (int i = 0; i < ROWS; i++) {
                    if (this.getComponent(i, pos - 4).getComponentType() != ComponentType.Empty && this.getComponent(i, pos - 4).getComponentType() != NotAccessible)
                        return new Pair<>(i, pos - 4);
                }
                return new Pair<>(0, 0);

            case South:
                for (int i = ROWS - 1; i >= 0; i--) {
                    if (this.getComponent(i, pos - 4).getComponentType() != ComponentType.Empty && this.getComponent(i, pos - 4).getComponentType() != NotAccessible)
                        return new Pair<>(i, pos - 4);
                }
                return new Pair<>(0, 0);

            case West:
                for (int i = 0; i < COLS; i++) {
                    if (this.getComponent(pos - 5, i).getComponentType() != ComponentType.Empty && this.getComponent(pos - 5, i).getComponentType() != NotAccessible)
                        return new Pair<>(pos- 5, i);
                }
                return new Pair<>(0, 0);

            case East:
                for (int i = COLS - 1; i >= 0; i--) {
                    if (this.getComponent(pos - 5, i).getComponentType() != ComponentType.Empty && this.getComponent(pos - 5, i).getComponentType() != NotAccessible)
                        return new Pair<>(pos- 5, i);
                }
                return new Pair<>(0, 0);
        }
        return new Pair<>(0, 0);
    }

    /**
     * This method identifies all connected components (pieces) on the ship's board and deletes
     * all components except the one specified by the {@code choose} index.
     *
     * @param choose the index of the component to retain; all other components will be removed
     */
    public void choosePiece(int choose) {
        List<List<Pair<Integer, Integer>>> pieces = new ArrayList<>(findShipPieces());
        for (int i = 0; i < pieces.size(); i++) {
            if (i != choose) {
                deletePiece(pieces.get(i));
            }
        }
    }

    /**
     * For each coordinate in the provided list, this method invokes {@code removeComponent(row, col)}
     * to eliminate the corresponding component from the board.
     *
     * @param piece a list of coordinate pairs representing the connected component to be removed
     */
    public void deletePiece(List<Pair<Integer, Integer>> piece) {

        for (Pair<Integer, Integer> pos : piece) {
            removeComponent(pos.getKey(), pos.getValue());
        }
    }

    /**
     * Removes the component located at the specified position on the ship's board by replacing it with an empty cell.
     *
     * @param row the row index of the component to remove
     * @param col the column index of the component to remove
     */
    public void RemoveComponent(int row, int col) {

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);

        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors, "");

        ship_board[row][col] = EMPTY_CELL;

    }

    /**
     * This method allows adding aliens to the 'LivingUnit' component card only if it is properly connected to a support
     *
     * @param living_unit
     * @return list of alien added in the ship
     */
//funzione che ritorni da 0 a 2 tipi di support per alieni
    public List<CrewmateType> checkAlienSupport(CardComponent living_unit) {
        List<CrewmateType> crew = new ArrayList<>();

        if (living_unit.getComponentType() != LivingUnit)

            System.out.println("cannot check alien support, this is not a living unit!");

        Pair<Integer, Integer> x_y = getCoords(living_unit);
        int x = x_y.getKey();
        int y = x_y.getValue();

        living_unit = this.getComponent(x, y);

        if (x != 4 && this.getComponent(x + 1, y).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(North)).contains(this.getComponent(x + 1, y).getConnector(South)))
            crew.add(PinkAlien);
        else if (x != 4 && this.getComponent(x + 1, y).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(North)).contains(this.getComponent(x + 1, y).getConnector(South))) {
            crew.add(BrownAlien);
        }

        if (x != 0 && this.getComponent(x - 1, y).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(South)).contains(this.getComponent(x - 1, y).getConnector(North)))
            crew.add(PinkAlien);
        else if (x != 0 && this.getComponent(x - 1, y).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(South)).contains(this.getComponent(x - 1, y).getConnector(North))) {
            crew.add(BrownAlien);
        }

        if (y != 6 && this.getComponent(x, y + 1).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(East)).contains(this.getComponent(x, y + 1).getConnector(West)))
            crew.add(PinkAlien);
        else if (y != 6 && this.getComponent(x, y + 1).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(East)).contains(this.getComponent(x, y + 1).getConnector(West))) {
            crew.add(BrownAlien);
        }

        if (y != 0 && this.getComponent(x, y - 1).getComponentType() == PinkAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(West)).contains(this.getComponent(x, y - 1).getConnector(East)))
            crew.add(PinkAlien);
        else if (y != 0 && this.getComponent(x, y - 1).getComponentType() == BrownAlienUnit
                && living_unit.getValidConnectors(living_unit.getConnector(West)).contains(this.getComponent(x, y - 1).getConnector(East))) {
            crew.add(BrownAlien);
        }

        return crew;
    }

    /**
     * Returns a list of coordinates for all components on the ship's board.
     * This method scans the entire board and collects the positions of all components that are neither empty nor marked as not accessible.
     *
     * @return a list of coordinate pairs representing the non-empty and accessible components on the ship's board
     */
    public List<Pair<Integer, Integer>> printShipPlance() {
        List<Pair<Integer, Integer>> ships = new ArrayList<>();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (this.getComponent(i, j).getComponentType() != Empty
                        && this.getComponent(i, j).getComponentType() != NotAccessible) {
                    ships.add(new Pair<>(i, j));
                }
            }
        }
        return ships;
    }


    public CardComponent[][] deepCopyBoard(CardComponent[][] plance) {
        // Copia tutti i componenti della nave
        CardComponent[][] copy = new CardComponent[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                CardComponent original = this.getComponent(r, c);
                if (original != null) {
                    // Crea una copia del componente
                    copy[r][c] = original.copy();
                }
            }
        }
        // Copia eventuali altri attributi
        return copy;
    }


    public CardComponent[][] getShip_board() {
        return ship_board;
    }

    public void setShip_board(CardComponent[][] ship_board) {
        this.ship_board = ship_board;
    }

    public void setCOLS(int COLS) {
        this.COLS = COLS;
    }

    public void setROWS(int ROWS) {
        this.ROWS = ROWS;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
