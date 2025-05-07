package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {


    private Ship ship1, ship2;
    private Player player1, player2;

    @BeforeEach
    void setUp() {
        Game game = new Game(Gametype.StandardGame);
        player1 = new Player("Reff", GREEN,game);
        player2 = new Player("Mambre", YELLOW,game);
        ship1 = new Ship(player1);
        player1.getShip().initializeShipPlance();
        ship2 = new Ship(player2);


    }

    @Test
    public void testinitializeShipPlance() {



        ship1 = player1.getShip();
        assertEquals(MainUnitGreen, ship1.getComponent(2, 3).getComponentType());

        assertEquals(NotAccessible, ship1.getComponent(0, 0).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(0, 1).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(0, 3).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(0, 5).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(0, 6).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(1, 0).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(1, 6).getComponentType());
        assertEquals(NotAccessible, ship1.getComponent(4, 3).getComponentType());


    }

    @Test


    public void testaddComponentEngine() {
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        CardComponent comp = new CardComponent(DoubleEngine, connectors,"");
        player1.getShip().addComponent(comp, 3, 2);
        assertEquals(comp, player1.getShip().getComponent(3, 2));

    }

    @Test
    public void testaddComponentCannon() {
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        CardComponent comp = new CardComponent(Cannon, connectors,"");
        player1.getShip().addComponent(comp, 3, 2);
        assertEquals(comp, player1.getShip().getComponent(3, 2));

    }

    @Test
    public void testcalculateEnginePower() {
        player2.getShip().initializeShipPlance();
        player1.getShip().initializeShipPlance();
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        player1.getShip().addComponent(new CardComponent(DoubleEngine, connectors,""), 3, 2);
        player1.getShip().addComponent(new CardComponent(Engine, connectors,""), 4, 1);

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3, 2), true);
        player1BatteryUsage.put(player1.getShip().getComponent(4, 1), false);


        player2.getShip().addComponent(new CardComponent(DoubleEngine, connectors,""), 3, 1);
        player2.getShip().addComponent(new CardComponent(Engine, connectors,""), 3, 2);
        player2.getShip().addComponent(new CardComponent(Engine, connectors,""), 3, 3);
        player2.getShip().addComponent(new CardComponent(DoubleEngine, connectors,""), 3, 4);

        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();

        player2BatteryUsage.put(player2.getShip().getComponent(3, 1), true);


        assertEquals(3, player1.getShip().calculateEnginePower(player1BatteryUsage));
        assertEquals(5, player2.getShip().calculateEnginePower(player2BatteryUsage));
    }


    @Test
    public void testcalculateCannonPower() {
        player2.getShip().initializeShipPlance();
        player1.getShip().initializeShipPlance();
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        player1.getShip().addComponent(new CardComponent(DoubleCannon, connectors,""), 3, 2);
        player1.getShip().addComponent(new CardComponent(Cannon, connectors,""), 4, 1);

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3, 2), true);
        player1BatteryUsage.put(player1.getShip().getComponent(4, 1), false);

        player2.getShip().addComponent(new CardComponent(DoubleCannon, connectors,""), 3, 1);
        player2.getShip().addComponent(new CardComponent(Cannon, connectors,""), 3, 2);
        player2.getShip().addComponent(new CardComponent(Cannon, connectors,""), 3, 3);
        player2.getShip().addComponent(new CardComponent(DoubleCannon, connectors,""), 3, 4);


        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();

        player2BatteryUsage.put(player2.getShip().getComponent(3, 1), true);


        assertEquals(1.5, player1.getShip().calculateCannonPower(player1BatteryUsage));
        assertEquals(2.5, player2.getShip().calculateCannonPower(player2BatteryUsage));
    }


    @Test
    public void checkConnection() {
        Ship ship = player1.getShip();
        ship.initializeShipPlance(); //2,3 main unit
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Double);
        connectors.put(West, Single);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 1, 2);

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 1, 3);

        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        ship.addComponent(new CardComponent(Engine, connectors,""), 1, 4);


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);
        ship.addComponent(new Battery(Battery, connectors, 2,""), 2, 1);

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Double);
        ship.addComponent(new Storage(BlueStorage, connectors, 2,""), 2, 2);



        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Cannon_Connector);
        connectors.put(West, Smooth);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 2, 5);

        connectors.put(North, Single);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Double);
        ship.addComponent(new Storage(BlueStorage, connectors, 2,""), 3, 1);


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 3, 2);

        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Cannon_Connector);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 3, 3);

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 3, 4);

        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        ship.addComponent(new Battery(Battery, connectors, 2,""), 3, 5);

        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Cannon_Connector);
        connectors.put(West, Smooth);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 4, 1);

        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        ship.addComponent(new CardComponent(Engine, connectors,""), 4, 2);

        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Cannon_Connector);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 4, 4);

        connectors.put(North, Smooth);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);
        ship.addComponent(new CardComponent(Engine, connectors,""), 4, 5);

        List<Pair<Integer, Integer>> invalids = ship.checkShipConnections();



        assertTrue(invalids.contains(new Pair<>(1, 2)));
        assertTrue(invalids.contains(new Pair<>(2, 2)));
        assertTrue(invalids.contains(new Pair<>(3, 2)));
        assertTrue(invalids.contains(new Pair<>(3, 3)));
        assertTrue(invalids.contains(new Pair<>(4, 2)));
        assertTrue(invalids.contains(new Pair<>(4, 1)));

    }

    @Test
    public void findShipPieces() {

        Ship ship = player1.getShip();
        ship.initializeShipPlance(); //2,3 main unit
        Map<Direction, ConnectorType> connectors = new HashMap<>();

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 1, 2);

        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);
        ship.addComponent(new LivingUnit(LivingUnit, connectors,""), 1, 3);
        ((LivingUnit) ship.getComponent(1,3)).addAstronauts();

        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        ship.addComponent(new Storage(BlueStorage, connectors, 2,""), 3, 1);

        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        ship.addComponent(new CardComponent(Engine, connectors,""), 2, 4);

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 3, 2);

        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);
        ship.addComponent(new Storage(RedStorage, connectors, 1,""), 3, 3);

        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        ship.addComponent(new CardComponent(Engine, connectors,""), 4, 1);

        connectors.put(North, Smooth);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 4, 2);

        assertTrue(ship.checkShipConnections().isEmpty());


        List<Pair<Integer,Integer>> tronconi_1 = new ArrayList<>();
        tronconi_1.add(new Pair<>(1, 2));
        tronconi_1.add(new Pair<>(1, 3));
        tronconi_1.add(new Pair<>(2, 3));
        tronconi_1.add(new Pair<>(3, 3));
        tronconi_1.add(new Pair<>(3, 2));
        tronconi_1.add(new Pair<>(2, 4));


        assertTrue( ship.findShipPieces().get(0).containsAll(tronconi_1));















    }

    @Test
    public void testcalculateExposedConnectors() {
        Ship ship = player1.getShip();
        ship.initializeShipPlance(); //2,3 main unit
        Map<Direction, ConnectorType> connectors = new HashMap<>();

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 1, 2);

        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);
        ship.addComponent(new LivingUnit(LivingUnit, connectors,""), 1, 3);
        ((LivingUnit) ship.getComponent(1,3)).addAstronauts();;


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        ship.addComponent(new CardComponent(Engine, connectors,""), 2, 4);

        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);
        ship.addComponent(new CardComponent(Cannon, connectors,""), 3, 2);

        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);
        ship.addComponent(new Storage(RedStorage, connectors, 1,""), 3, 3);

        assertEquals(5,ship.calculateExposedConnectors());

    }




}
