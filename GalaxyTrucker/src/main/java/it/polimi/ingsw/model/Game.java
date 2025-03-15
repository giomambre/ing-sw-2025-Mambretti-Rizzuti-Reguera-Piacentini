package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;

public class Game {

    private List<Player> players = new ArrayList<>();
    private int numPlayers;
    private List<CardAdventure> deck_adventure = new ArrayList<>();
    private static final int clock_time = 30; //30 sec messi a caso
    private List<Player> active_players = new ArrayList<>();
    private Player board_leader;
    private List<CardComponent> deck_components = new ArrayList<>();
    private Board board ;


    public void startGame() {

        //Created some components just to test the print and the constructor


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);

        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //due batterie


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Battery, connectors)); //due


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //tre batterie


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Battery, connectors)); //tre


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Battery, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //tre


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //uno


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(CargoBlue, connectors)); //due


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitBlue, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitGreen, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitRed, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitYellow, connectors));



        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));



        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors));












        //print them all
        for (CardComponent element : deck_components) {
            System.out.println(element);
        }

    }

    public void startAssembly() {

        active_players.addAll(players);

    }

    public void startFlight(){
        board = new Board(active_players);

    }

    public void addPlayer(Player player) {

        players.add(player);


    }

    public void checkShipValidity() {

//da pensare bene
    }

    public CardComponent GetRandomCardComponent() {
        Collections.shuffle(deck_components);  //shuffle the list and remove the first (returns it)
        return deck_components.removeFirst();

    }

    public void removePlayer(Player player) {
        active_players.remove(player);
    }


    public Board getBoard() {
        return board;
    }
}