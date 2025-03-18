package it.polimi.ingsw.model;

import com.sun.tools.javac.Main;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.ConnectorType.Universal;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.Direction.West;

public class Ship {
    private static final CardComponent EMPTY_CELL = new CardComponent(Empty, new EnumMap<>(Direction.class));
    int ROWS = 5,COLS = 7;
    private CardComponent[][] ship_plance = new CardComponent[ROWS][COLS];
    private List<CardComponent> extra_components = new ArrayList<>();
    private Map<Direction, Boolean> covered_side = new EnumMap<>(Direction.class);
    private int batteries = 0;


    Ship(){
        this.initializeShipPlance();

    }

    public List<CardComponent> getExtra_components() {
        return extra_components;
    }

    public void setExtra_components(List<CardComponent> extra_components) {
        this.extra_components = extra_components;
    }

    public void AddComponent(CardComponent component, int row, int col) {

        //Controllo se è gia presente un elemento in quella pos, da capire se farlo nel controller o qui

        ship_plance[row][col] = component;


    }


    private void initializeShipPlance() {

        //set all covered_side  all to False

        covered_side.put(North, Boolean.FALSE);
        covered_side.put(South, Boolean.FALSE);
        covered_side.put(West, Boolean.FALSE);
        covered_side.put(East, Boolean.FALSE);


        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (row == 2 && col == 3) {
                    Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Universal);
                    connectors.put(South, Universal);
                    connectors.put(East, Universal);
                    connectors.put(West, Universal);

                    ship_plance[row][col] = new CardComponent(MainUnit, connectors);
                } else {
                    ship_plance[row][col] = EMPTY_CELL;
                }
            }
        }
    }
    public void PrintShipPlance() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                    System.out.println(ship_plance[row][col]);



            }
        }
    }



}
