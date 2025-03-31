package it.polimi.ingsw.model;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Shield;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.util.Pair;

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
public class Ship {


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

    /**
     * Add a CardComponent to the ship.
     * @param component the one to add
     * @param row to identify in witch row of the ship board the component will be added
     * @param col to identify in witch col of the ship board the component will be added
     */
    public void addComponent(CardComponent component, int row, int col) {

        //Controllo se è gia presente un elemento in quella pos, da capire se farlo nel controller o qui
        //(isa) secondo me: view permette al player di selezionare la posizione e la invia al controller, il controller verifica se è occupata
        //se si: aggiorna view e richiede al player di inserire, se no: invoca questa funzione qui


        if(ship_board[row][col].getComponentType() == NotAccessible )
            throw new IllegalArgumentException("Position not Accessible");

        if(ship_board[row][col].getComponentType() != Empty )
            throw new IllegalArgumentException("Position already in use");



        ship_board[row][col] = component;


    }


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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

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

    public boolean findPinkAlien() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                CardComponent component = this.getComponent(row, col);

                // Verifica che il componente sia un'unità alieno rosa
                if (component.getComponentType() != LivingUnit ||
                        ((LivingUnit) component).getCrewmateType() != PinkAlien) {
                    continue;
                }

                // Controlla se ha un vicino dello stesso tipo (PinkAlienUnit)
                if (hasAdjacentPinkAlien(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAdjacentPinkAlien(int row, int col) {
        // Direzioni: sopra, destra, sotto, sinistra
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        CardComponent component = this.getComponent(row, col);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];


            // Verifica che la posizione sia valida
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                CardComponent neighbor = this.getComponent(newRow, newCol);
                if (neighbor.getComponentType() == PinkAlienUnit &&
                        (dir[0]==-1 && component.getValidsConnectors(component.getConnector(West)).contains(neighbor.getConnector(East))||
                                dir[1]==1 && component.getValidsConnectors(component.getConnector(South)).contains(neighbor.getConnector(North))||
                                dir[0]==1 && component.getValidsConnectors(component.getConnector(East)).contains(neighbor.getConnector(West))||
                                dir[1]==-1 && component.getValidsConnectors(component.getConnector(North)).contains(neighbor.getConnector(South))
                                )) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findBrownAlien() {
        boolean brown_alien = false;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = this.getComponent(row, col);

                if (component.getComponentType() == LivingUnit
                        && (this.getComponent(row+1, col).getComponentType() == BrownAlienUnit || this.getComponent(row, col+1).getComponentType() == BrownAlienUnit
                            || this.getComponent(row, col-1).getComponentType() == BrownAlienUnit || this.getComponent(row-1, col).getComponentType() == BrownAlienUnit)
                        && ((LivingUnit) component).getCrewmateType() == BrownAlien) {
                    brown_alien = true;
                }

            }
        }
        return brown_alien;
    }

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

    public double calculateEnginePower(Map<CardComponent, Boolean> battery_usage) { //after the validity check
        double power = 0;
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


                        if (x != 0 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x - 1, y).getConnector(South))
                        ) return false;

                        break;

                    case East:
                        if (y != COLS - 1 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x, y + 1).getConnector(West))
                        ) return false;
                        break;

                    case South:
                        if (x != ROWS - 1 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x + 1, y).getConnector(North))
                        ) return false;
                        break;

                    case West:
                        if (y != 0 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x, y - 1).getConnector(East))
                        ) return false;
                        break;


                }
            }

        }
        return true;
    }

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

    public boolean isProtected(Direction direction) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
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

                    if(validatePiece(piece) ) pieces.add(piece);

                    else deletePiece(piece);
                }
            }
        }
        return pieces;
    }

    private boolean isValidComponent(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return false;
        CardComponent component = ship_board[row][col];
        return component != null && component.getComponentType() != Empty && component.getComponentType() != NotAccessible;
    }


    // DFS per trovare un pezzo connesso della nave
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

                    if (ship_board[x][y].getValidsConnectors(ship_board[x][y].getConnector(dir))
                            .contains(neighbor.getConnector(getOpposite(dir)))) {
                        explorePiece(newX, newY, visited, piece);
                    }
                }
            }
        }
    }

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

    public CardComponent getComponent(int x, int y) {
        return ship_board[x][y];
    }

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

    public Pair<Integer,Integer> getCoords(CardComponent component) {
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                if(this.getComponent(i, j) == component) {
                    return new Pair(i, j);
                }
            }
        }
        return new Pair(0,0);
    }


    public void removeComponent(int x, int y) {
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);




        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        if(this.getComponent(x, y).getComponentType() ==PinkAlienUnit || this.getComponent(x, y).getComponentType() ==BrownAlienUnit ) {
             for(int row=0; row<ROWS; row++) {
                 for(int col=0 ;col<COLS; col++) {
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

    public CardComponent getFirstComponent(Direction dir , int pos ) {

// ritorna un component NOT ACCESSIBLE se non ce nessun componente colpito

        switch (dir) {
            case North:
                for (int i = 0; i < ROWS; i++) {
                    if (ship_board[i][pos - 4].getComponentType() != ComponentType.Empty && ship_board[i][pos - 4].getComponentType() != NotAccessible)
                        return ship_board[i][pos - 4];
                }
                return ship_board[0][0];
            case South:
                for (int i = ROWS - 1; i >= 0; i--) {
                    if (ship_board[i][pos - 4].getComponentType() != ComponentType.Empty && ship_board[i][pos - 4].getComponentType() != NotAccessible)
                        return ship_board[i][pos - 4];
                }
                return ship_board[0][0];

            case West:
                for (int i = 0; i < COLS; i++) {
                    if (ship_board[pos - 5][i].getComponentType() != ComponentType.Empty && ship_board[pos - 5][i].getComponentType() != NotAccessible)
                        return ship_board[pos - 5][i];
                }
                return ship_board[0][0];

            case East:
                for (int i = COLS - 1; i >= 0; i--) {
                    if (ship_board[pos - 5][i].getComponentType() != ComponentType.Empty && ship_board[pos - 5][i].getComponentType() != NotAccessible)
                        return ship_board[pos - 5][i];
                }
                return ship_board[0][0];


        }

        return ship_board[0][0];
    }

    public void choosePiece(int choose){
        List<List<Pair<Integer, Integer>>> pieces = new ArrayList<>(findShipPieces());
        for (int i = 0; i < pieces.size() ; i++) {
            if (i != choose) {
                deletePiece(pieces.get(i));
            }

    }
    }

    public void deletePiece(List<Pair<Integer, Integer>> piece){

                       for(Pair<Integer, Integer> pos : piece){
                           removeComponent(pos.getKey(),pos.getValue());
                       }

        }


    public void RemoveComponent(int row, int col) {

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);

        CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);

        ship_board[row][col] = EMPTY_CELL;

    }

//funzione che ritorni da 0 a 2 tipi di support per alieni
    public List<CrewmateType> checkAlienSupport(CardComponent living_unit) {
        List<CrewmateType> crew = new ArrayList<>();

        if (living_unit.getComponentType() != LivingUnit)

            System.out.println("cannot check alien support, this is not a living unit!");

        Pair<Integer, Integer> x_y = getCoords(living_unit);
        int x = x_y.getKey();
        int y = x_y.getValue();

        living_unit = this.getComponent(x, y);

        if (x!=4 && this.getComponent(x+1,y).getComponentType() == PinkAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(North)).contains(this.getComponent(x+1,y).getConnector(South)))
            crew.add(PinkAlien);
        else if (x!=4 && this.getComponent(x+1,y).getComponentType() == BrownAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(North)).contains(this.getComponent(x+1,y).getConnector(South))) {
            crew.add(BrownAlien);
        }

        if (x!=0 && this.getComponent(x-1,y).getComponentType() == PinkAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(South)).contains(this.getComponent(x+1,y).getConnector(North)))
            crew.add(PinkAlien);
        else if (x!=0 && this.getComponent(x-1,y).getComponentType() == BrownAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(South)).contains(this.getComponent(x+1,y).getConnector(North))) {
            crew.add(BrownAlien);
        }

        if (y!=6 && this.getComponent(x,y+1).getComponentType() == PinkAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(East)).contains(this.getComponent(x+1,y).getConnector(West)))
            crew.add(PinkAlien);
        else if (y!=6 && this.getComponent(x,y+1).getComponentType() == BrownAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(East)).contains(this.getComponent(x+1,y).getConnector(West))) {
            crew.add(BrownAlien);
        }

        if (y!=0 && this.getComponent(x,y-1).getComponentType() == PinkAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(West)).contains(this.getComponent(x+1,y).getConnector(East)))
            crew.add(PinkAlien);
        else if (y!=0 &&this.getComponent(x,y-1).getComponentType() == BrownAlienUnit
                && living_unit.getValidsConnectors(living_unit.getConnector(West)).contains(this.getComponent(x+1,y).getConnector(East))) {
            crew.add(BrownAlien);
        }


        return crew;
    }


}
