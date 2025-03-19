package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.CardAdventureType.*;

public class Game {

    private List<Player> players = new ArrayList<>();
    private int numPlayers;
    private List<CardAdventure> deck_adventure = new ArrayList<>();
    private static final int clock_time = 30; //30 sec messi a caso
    private List<Player> active_players = new ArrayList<>();
    private Player board_leader;
    private List<CardComponent> deck_components = new ArrayList<>();
    private Board board;


    public void setDeck_components(List<CardComponent> deck_components) {
        this.deck_components = deck_components;
    }

    public List<CardComponent> getDeck_components() {
        return deck_components;
    }

    public void startGame() {


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);

        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(RedStorage, connectors, 2));


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


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors)); //di defaul rivolti nord-est (da capire se fare sottoclasse)


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors));


        //to add : adventure deck creation and allocation

        Collections.shuffle(deck_components);


    }

    public void startAssembly() {

        active_players.addAll(players);

    }

    public void startFlight() {
        board = new Board(active_players);

    }

    public void addPlayer(Player player) {

        players.add(player);


    }

    public void checkShipValidity() {

//da pensare bene
    }

    public CardComponent GetRandomCardComponent() {
        return deck_components.removeFirst();

    }

    public CardAdventure GetRandomCardAdventure() {
        CardAdventure adventure = deck_adventure.removeFirst();
        if (deck_adventure.isEmpty()) System.out.println("GIOCO FINITO");
        return adventure;

    }

    public void startAdventure() {

        CardAdventure adventure = GetRandomCardAdventure();

        switch (adventure.getType()) {

            case OpenSpace:

                break;
            case AbandonedShip:
                break;
            case AbandonedStation:
                break;
            case MeteorSwarm:
                break;
            case Planets:
                break;

            case CombatZone:
                break;
            case Smugglers:
                break;
            case Pirates:
                break;

            case Slayers:
                break;
            case Epidemic:
                break;


        }

    }

    public void removePlayer(Player player) {
        active_players.remove(player);
    }


    public Board getBoard() {
        return board;
    }


}