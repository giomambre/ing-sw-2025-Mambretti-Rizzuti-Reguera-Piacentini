package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.OpenSpace;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.Shield;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;

public class QuickGame extends BaseGame {

    public QuickGame(Gametype type) {
        super(type);
        initializeDeckComponents();
        createDeckAdventure();
    }

    public void createDeckAdventure() {
        List<CardAdventure> deck_adventure = new ArrayList<>();

        deck_adventure.add(new OpenSpace(1,0,CardAdventureType.OpenSpace,board));
        //Ali crea le altre 7

        Collections.shuffle(deck_adventure);
        this.deck_adventure = deck_adventure;
        return;
    }

    /**
     * this method is called to start the flight phase putting al the rockets on the board
     */
    public void startFlight() {
        board = new Board(active_players,18);
    }

    /**
     * this method adds every Card Component existent (EXCEPT ALIENS LIVING UNITS) to the deck of card components and then shuffles it
     */
    public List<CardComponent> initializeDeckComponents() {


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);

        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 2));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(ComponentType.Battery, connectors, 3));


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

        return deck_components;
    }

}
