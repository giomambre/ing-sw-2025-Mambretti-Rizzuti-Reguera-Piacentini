package it.polimi.ingsw.model;

import com.sun.tools.javac.Main;
import javafx.util.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.ConnectorType.*;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.Color.*;

public class Ship {

    int ROWS = 5, COLS = 7;
    private CardComponent[][] ship_plance = new CardComponent[ROWS][COLS];
    private List<CardComponent> extra_components = new ArrayList<>();
    private int batteries = 0;
    private Player player;


    public Ship(Player player) {
        this.player = player;

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
        CardComponent EMPTY_CELL = new CardComponent(Empty, new EnumMap<>(Direction.class));
        CardComponent NOT_ACCESSIBLE_CELL = new CardComponent(NotAccessible, new EnumMap<>(Direction.class));
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (row == 2 && col == 3) {
                    Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Universal);
                    connectors.put(South, Universal);
                    connectors.put(East, Universal);
                    connectors.put(West, Universal);

                    ship_plance[row][col] = new CardComponent(main_unit, connectors); //da mettere main unit in base al colore
                } else if (row == 0 && (col == 0 || col == 1 || col == 3 || col == 5 || col == 6)) {
                    ship_plance[row][col] = NOT_ACCESSIBLE_CELL;
                } else if ((row == 1 && (col == 0 || col == 6)) ||( row == 4 && col == 3)) {
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

                switch (tmp.GetComponent_type()) {

                    case Cannon:

                        if (tmp.getConnector(North) == Cannon_Connector) power++;
                        else power += 0.5;


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
        return power;

    }

    private List<CardComponent> getAvailableBatteries() {
        List<CardComponent> batteries = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                CardComponent component = ship_plance[row][col];

                if (component != null && component.GetComponent_type() == ComponentType.Battery) {
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

                switch (tmp.GetComponent_type()) {

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


  //  public List<Pair<Integer, Integer>> checkShipValidity(){


    //}

    public boolean isProtected(Direction direction) {
        for(int row = 0; row < ROWS; row++) {
            for(int col = 0; col < COLS; col++) {
                if(ship_plance[row][col].GetComponent_type() == Shield ) {
                    Shield shieldObject = (Shield) ship_plance[row][col];
                    if(shieldObject.getCoveredSides().get(direction) == Boolean.TRUE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public CardComponent getComponent(int x, int y){
        return ship_plance[x][y];
    }

    public void PrintShipPlance() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                System.out.println(ship_plance[row][col]);


            }
        }
    }



    public void setExtra_components(List<CardComponent> extraComponents) {
        this.extra_components = extraComponents;
    }

    public List<CardComponent> getExtra_components() {
        return extra_components;
    }
}
