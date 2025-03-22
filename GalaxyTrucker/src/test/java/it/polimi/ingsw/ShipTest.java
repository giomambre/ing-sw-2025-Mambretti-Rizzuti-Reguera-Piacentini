package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.Color.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {


    private Ship ship1, ship2;
    private Player player1, player2;

    @BeforeEach
    void setUp() {
        player1 = new Player("Reff", Green);
        player2 = new Player("Mambre", Yellow);
        ship1 = new Ship(player1);
        ship2 = new Ship(player2);


    }

    @Test
    public void testinitializeShipPlance() {


        ship1.initializeShipPlance();


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


    public void testAddComponentEngine() {
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        CardComponent comp = new CardComponent(DoubleEngine, connectors);
        player1.getShip().AddComponent(comp, 3, 2);
        assertEquals(comp, player1.getShip().getComponent(3, 2));

    }

    @Test
    public void testAddComponentCannon() {
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        CardComponent comp = new CardComponent(Cannon, connectors);
        player1.getShip().AddComponent(comp, 3, 2);
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

        player1.getShip().AddComponent(new CardComponent(DoubleEngine, connectors), 3, 2);
        player1.getShip().AddComponent(new CardComponent(Engine, connectors), 4, 1);

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3, 2), true);
        player1BatteryUsage.put(player1.getShip().getComponent(4, 1), false);


        player2.getShip().AddComponent(new CardComponent(DoubleEngine, connectors), 3, 1);
        player2.getShip().AddComponent(new CardComponent(Engine, connectors), 3, 2);
        player2.getShip().AddComponent(new CardComponent(Engine, connectors), 3, 3);
        player2.getShip().AddComponent(new CardComponent(DoubleEngine, connectors), 3, 4);

        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();

        player2BatteryUsage.put(player2.getShip().getComponent(3, 1), true);



        assertEquals(3 , player1.getShip().calculateEnginePower(player1BatteryUsage));
        assertEquals(5,player2.getShip().calculateEnginePower(player2BatteryUsage));
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
        player1.getShip().AddComponent(new CardComponent(DoubleCannon, connectors), 3, 2);
        player1.getShip().AddComponent(new CardComponent( Cannon, connectors), 4, 1);

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3, 2), true);
        player1BatteryUsage.put(player1.getShip().getComponent(4, 1), false);

        player2.getShip().AddComponent(new CardComponent(DoubleCannon, connectors), 3, 1);
        player2.getShip().AddComponent(new CardComponent(Cannon, connectors), 3, 2);
        player2.getShip().AddComponent(new CardComponent(Cannon, connectors), 3, 3);
        player2.getShip().AddComponent(new CardComponent(DoubleCannon, connectors), 3, 4);


        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();

        player2BatteryUsage.put(player2.getShip().getComponent(3, 1), true);


        assertEquals(1.5 , player1.getShip().calculateCannonPower(player1BatteryUsage));
        assertEquals(2.5 ,player2.getShip().calculateCannonPower(player2BatteryUsage));
    }

    @Test
    public void checkShipConnectionsTest(){
        Ship ship = player1.getShip();
        ship.initializeShipPlance();

        ship.AddComponent(new CardComponent(Engine,0,2));


    }
}
