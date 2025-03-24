package it.polimi.ingsw.model;

import javafx.util.Pair;

import java.util.*;

import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.ConnectorType.*;

import static it.polimi.ingsw.model.CrewmateType.BrownAlien;
import static it.polimi.ingsw.model.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.Direction.*;

public class Ship {

    private int ROWS = 5, COLS = 7;
    private CardComponent[][] ship_plance = new CardComponent[ROWS][COLS];
    private List<CardComponent> extra_components = new ArrayList<>();
    private int batteries = 0;
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

    public void AddComponent(CardComponent component, int row, int col) {

        //Controllo se è gia presente un elemento in quella pos, da capire se farlo nel controller o qui

        ship_plance[row][col] = component;


    }


    public void initializeShipPlance() {


        ComponentType main_unit;
        switch (player.getColor()) {
            case Red:
                main_unit = MainUnitRed;
                break;
            case Yellow:
                main_unit = MainUnitYellow;
                break;
            case Green:
                main_unit = MainUnitGreen;
                break;
            default:
                main_unit = MainUnitBlue;
                break;

        }
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North,EmptyConnector);
        connectors.put(South,EmptyConnector);
        connectors.put(East,EmptyConnector);
        connectors.put(West,EmptyConnector);



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

                    ship_plance[row][col] = new CardComponent(main_unit, connectors); //da mettere main unit in base al colore
                } else if (row == 0 && (col == 0 || col == 1 || col == 3 || col == 5 || col == 6)) {
                    ship_plance[row][col] = NOT_ACCESSIBLE_CELL;
                } else if ((row == 1 && (col == 0 || col == 6)) || (row == 4 && col == 3)) {
                    ship_plance[row][col] = NOT_ACCESSIBLE_CELL;

                } else {
                    ship_plance[row][col] = EMPTY_CELL;
                }

            }
        }
    }

    public double calculateCannonPower(Map<CardComponent, Boolean> battery_usage) { //after the validity check , get battery_usage from controller i think
        double power = 0;
        CardComponent tmp;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                tmp = ship_plance[row][col];

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
        if(power > 0 && findPinkAlien())   {power +=2;}

        return power;

    }

    public boolean findPinkAlien(){
        boolean pink_alien = false;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = this.getComponent(row, col);

                if (component.getComponentType() == PinkAlienUnit && ((LivingUnit)component).getCrewmateType() == PinkAlien ){
                    pink_alien=true;
                }

            }
        }
        return pink_alien;
    }

    public boolean findBrownAlien(){
        boolean brown_alien = false;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = this.getComponent(row, col);

                if (component.getComponentType() == BrownAlienUnit && ((LivingUnit)component).getCrewmateType() == BrownAlien ){
                    brown_alien=true;
                }

            }
        }
        return brown_alien;
    }

    private List<CardComponent> getAvailableBatteries() {
        List<CardComponent> batteries = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = ship_plance[row][col];

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
        if(power > 0 && findBrownAlien())   {power +=2;}
        return power;

    }




    public Boolean checkCorrectConnections( int x, int y) {
        CardComponent card = getComponent(x, y);
        if(card.getComponentType() == Empty || card.getComponentType() == NotAccessible) {
            return true;
        }
        for (Direction direction : Direction.values()) {

            if(card.getConnector(direction) == Engine_Connector ) {

                if(direction!=South) return false;

            }

             {
                switch (direction) {

                    case North:


                        if(x != 0 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x - 1, y).getConnector(South))
                                ) return false;

                        break;

                    case East: if (y != COLS-1 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x , y + 1).getConnector(West))
                    ) return false;
                        break;

                    case South:
                        if (x != ROWS - 1 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x + 1, y).getConnector(North))
                                ) return false;
                        break;

                        case West: if (y != 0 && !card.getValidsConnectors(card.getConnector(direction)).contains(getComponent(x , y - 1).getConnector(East))
                               ) return false;
                            break;



                }
            }

        }
        return  true;
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
                    total += ((LivingUnit) tmp).getNum_astronaut();
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
                if (ship_plance[row][col].getComponentType() == Shield) {
                    Shield shieldObject = (Shield) ship_plance[row][col];
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

                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    private boolean isValidComponent(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return false;
        CardComponent component = ship_plance[row][col];
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
                CardComponent neighbor = ship_plance[newX][newY];

                if (neighbor.getComponentType() != Empty && neighbor.getComponentType() != NotAccessible
                        && ship_plance[x][y].getConnector(dir) != Smooth) {

                    if (ship_plance[x][y].getValidsConnectors(ship_plance[x][y].getConnector(dir))
                            .contains(neighbor.getConnector(getOpposite(dir)))) {
                        explorePiece(newX, newY, visited, piece);
                    }
                }
            }
        }
    }

    public int calculateExposedConnectors(){
        int exposed_connectors = 0;
        CardComponent component;


        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                component = this.getComponent(row, col);

                for (Direction direction : Direction.values()){
                    switch (direction) {

                        case North:
                            if((component.getConnector(North)==Universal || component.getConnector(North)==Double || component.getConnector(North)==Single)
                            && this.getComponent(row-1 , col).getConnector(South) == EmptyConnector)
                                exposed_connectors++;
                            break;

                        case South:
                            if((component.getConnector(South)==Universal || component.getConnector(South)==Double || component.getConnector(South)==Single)
                                    && this.getComponent(row+1 , col).getConnector(North) == EmptyConnector)
                                exposed_connectors++;
                            break;

                        case East:
                            if((component.getConnector(East)==Universal || component.getConnector(East)==Double || component.getConnector(East)==Single)
                                    && this.getComponent(row , col+1).getConnector(West) == EmptyConnector)
                                exposed_connectors++;
                            break;

                        case West:
                            if((component.getConnector(West)==Universal || component.getConnector(West)==Double || component.getConnector(West)==Single)
                                    && this.getComponent(row , col-1).getConnector(East) == EmptyConnector)
                                exposed_connectors++;
                            break;

                    }
                }



                switch(col){
                    case 0:
                        if (component.getConnector(West) == Universal || component.getConnector(West) == Double || component.getConnector(West) == Single)
                            exposed_connectors++;
                        break;

                    case 6:
                        if (component.getConnector(East) == Universal || component.getConnector(East) == Double || component.getConnector(East) == Single)
                            exposed_connectors++;
                        break;
                }

                switch(row){
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
        return ship_plance[x][y];
    }

    public Boolean validatePiece(List<Pair<Integer,Integer>> piece) {
        int valide = 0;
        for (Pair<Integer, Integer> pos : piece) {
            int x = pos.getKey();
            int y = pos.getValue();

            CardComponent component = ship_plance[x][y];
            if(component.getComponentType() == Engine || component.getComponentType() == DoubleEngine ) {
            valide++;
        }else if(component.getComponentType() == LivingUnit && ((LivingUnit) component).getNum_astronaut()>=1 ){

                valide++;

            }
        }

        return  valide == 2 ;



    }


   public void removeComponent(int x, int y) {
       Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
       connectors.put(North,EmptyConnector);
       connectors.put(South,EmptyConnector);
       connectors.put(East,EmptyConnector);
       connectors.put(West,EmptyConnector);



       CardComponent EMPTY_CELL = new CardComponent(Empty, connectors);
        ship_plance[x][y] = EMPTY_CELL;
   }

    public void setExtra_components(List<CardComponent> extraComponents) {
        this.extra_components = extraComponents;
    }

    public List<CardComponent> getExtra_components() {
        return extra_components;
    }

        public Direction getOpposite(Direction dir){
            switch (dir){
                case North: return South;
                case East: return West;
                case South: return North;
                default: return East;

            }
        }
}
