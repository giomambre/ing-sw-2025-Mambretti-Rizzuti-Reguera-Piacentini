package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.QuickShip;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Color.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

class QuickShipTest {

    private Player redPlayer;
    private Player yellowPlayer;
    private Player greenPlayer;
    private Player bluePlayer;
    private QuickShip quickShip;

    @BeforeEach
    void setUp() {
        Game game = new Game(Gametype.QuickGame);
        redPlayer = new Player("Red Player", RED, game);
        yellowPlayer = new Player("Yellow Player", YELLOW, game);
        greenPlayer = new Player("Green Player", GREEN, game);
        bluePlayer = new Player("Blue Player", BLUE, game);
        quickShip = new QuickShip(redPlayer);

    }


    @Test
    void testInitializeShipPlanceWithRedPlayer() {
        quickShip.initializeShipPlance();

        CardComponent mainUnit = quickShip.getComponent(2, 3);
        assertNotNull(mainUnit);
        assertEquals(MainUnitRed, mainUnit.getComponentType());
        assertEquals(Universal, mainUnit.getConnector(North));
        assertEquals(Universal, mainUnit.getConnector(South));
        assertEquals(Universal, mainUnit.getConnector(East));
        assertEquals(Universal, mainUnit.getConnector(West));
    }

    @Test
    void testInitializeShipPlanceWithYellowPlayer() {
        QuickShip yellowShip = new QuickShip(yellowPlayer);
        yellowShip.initializeShipPlance();

        CardComponent mainUnit = yellowShip.getComponent(2, 3);
        assertEquals(MainUnitYellow, mainUnit.getComponentType());
    }

    @Test
    void testInitializeShipPlanceWithGreenPlayer() {
        QuickShip greenShip = new QuickShip(greenPlayer);
        greenShip.initializeShipPlance();

        CardComponent mainUnit = greenShip.getComponent(2, 3);
        assertEquals(MainUnitGreen, mainUnit.getComponentType());
    }

    @Test
    void testInitializeShipPlanceWithBluePlayer() {
        QuickShip blueShip = new QuickShip(bluePlayer);
        blueShip.initializeShipPlance();

        CardComponent mainUnit = blueShip.getComponent(2, 3);
        assertEquals(MainUnitBlue, mainUnit.getComponentType());
    }

    @Test
    void testInitializeShipPlanceNotAccessibleCells() {
        quickShip.initializeShipPlance();

        // Test some not accessible positions based on the logic in initializeShipPlance
        assertEquals(NotAccessible, quickShip.getComponent(0, 0).getComponentType());
        assertEquals(NotAccessible, quickShip.getComponent(0, 1).getComponentType());
        assertEquals(NotAccessible, quickShip.getComponent(0, 2).getComponentType());
        assertEquals(NotAccessible, quickShip.getComponent(1, 0).getComponentType());
        assertEquals(NotAccessible, quickShip.getComponent(2, 0).getComponentType());
        assertEquals(NotAccessible, quickShip.getComponent(4, 6).getComponentType());
    }

    @Test
    void testInitializeShipPlanceEmptyCells() {
        quickShip.initializeShipPlance();

        // Test some empty positions
        assertEquals(Empty, quickShip.getComponent(1, 2).getComponentType());
        assertEquals(Empty, quickShip.getComponent(2, 1).getComponentType());
        assertEquals(Empty, quickShip.getComponent(3, 3).getComponentType());
        assertEquals(Empty, quickShip.getComponent(1, 3).getComponentType());
    }

    @Test
    void testCalculateCannonPowerWithNoCannons() {
        quickShip.initializeShipPlance();
        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();

        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(0.0, power);
    }

    @Test
    void testCalculateCannonPowerWithSingleCannon() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Cannon_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent cannon = new CardComponent(Cannon, connectors, "");
        shipBoard[1][2] = cannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(1.0, power);
    }

    @Test
    void testCalculateCannonPowerWithCannonNoNorthConnector() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        // Add a cannon without Cannon_Connector on North
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent cannon = new CardComponent(Cannon, connectors, "");
        shipBoard[1][2] = cannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(0.5, power);
    }

    @Test
    void testCalculateCannonPowerWithDoubleCannon() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Cannon_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleCannon = new CardComponent(DoubleCannon, connectors, "");
        shipBoard[1][2] = doubleCannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleCannon, true); // Use battery

        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(2.0, power); // 1.0 * 2 = 2.0
    }

    @Test
    void testCalculateCannonPowerWithDoubleCannonNoBattery() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Cannon_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleCannon = new CardComponent(DoubleCannon, connectors, "");
        shipBoard[1][2] = doubleCannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleCannon, false); // Don't use battery

        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(1.0, power);
    }

    @Test
    void testCalculateCannonPowerWithDoubleCannonNoNorthConnector() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleCannon = new CardComponent(DoubleCannon, connectors, "");
        shipBoard[1][2] = doubleCannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleCannon, true); // Use battery

        double power = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(1.0, power); // 0.5 * 2 = 1.0
    }

    @Test
    void testCalculateEnginePowerWithNoEngines() {
        quickShip.initializeShipPlance();
        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();

        double power = quickShip.calculateEnginePower(batteryUsage);
        assertEquals(0.0, power);
    }

    @Test
    void testCalculateEnginePowerWithSingleEngine() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent engine = new CardComponent(Engine, connectors, "");
        shipBoard[1][2] = engine;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        double power = quickShip.calculateEnginePower(batteryUsage);
        assertEquals(1.0, power);
    }

    @Test
    void testCalculateEnginePowerWithDoubleEngine() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();
        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleEngine = new CardComponent(DoubleEngine, connectors, "");
        shipBoard[1][2] = doubleEngine;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleEngine, true); // Use battery

        double power = quickShip.calculateEnginePower(batteryUsage);
        assertEquals(2.0, power);
    }

    @Test
    void testCalculateEnginePowerWithDoubleEngineNoBattery() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleEngine = new CardComponent(DoubleEngine, connectors, "");
        shipBoard[1][2] = doubleEngine;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleEngine, false); // Don't use battery

        double power = quickShip.calculateEnginePower(batteryUsage);
        assertEquals(1.0, power);
    }

    @Test
    void testRemoveComponent() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent testComponent = new CardComponent(Engine, connectors, "");
        shipBoard[1][2] = testComponent;

        assertEquals(Engine, quickShip.getComponent(1, 2).getComponentType());

        quickShip.removeComponent(1, 2);

        assertEquals(Empty, quickShip.getComponent(1, 2).getComponentType());
        assertEquals(Empty_Connector, quickShip.getComponent(1, 2).getConnector(North));
        assertEquals(Empty_Connector, quickShip.getComponent(1, 2).getConnector(South));
        assertEquals(Empty_Connector, quickShip.getComponent(1, 2).getConnector(East));
        assertEquals(Empty_Connector, quickShip.getComponent(1, 2).getConnector(West));
    }

    @Test
    void testMixedCannonPowerCalculation() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors1 = new EnumMap<>(Direction.class);
        connectors1.put(North, Cannon_Connector);
        connectors1.put(South, Empty_Connector);
        connectors1.put(East, Empty_Connector);
        connectors1.put(West, Empty_Connector);
        CardComponent cannon = new CardComponent(Cannon, connectors1, "");

        Map<Direction, ConnectorType> connectors2 = new EnumMap<>(Direction.class);
        connectors2.put(North, Empty_Connector);
        connectors2.put(South, Empty_Connector);
        connectors2.put(East, Empty_Connector);
        connectors2.put(West, Empty_Connector);
        CardComponent doubleCannon = new CardComponent(DoubleCannon, connectors2, "");

        shipBoard[1][2] = cannon;
        shipBoard[2][1] = doubleCannon;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleCannon, true);

        double cannonPower = quickShip.calculateCannonPower(batteryUsage);
        assertEquals(2.0, cannonPower); // 1.0 (cannon) + 1.0 (double cannon 0.5 * 2)
    }

    @Test
    void testMixedEnginePowerCalculation() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);

        CardComponent engine = new CardComponent(Engine, connectors, "");
        CardComponent doubleEngine = new CardComponent(DoubleEngine, connectors, "");

        shipBoard[1][2] = engine;
        shipBoard[3][2] = doubleEngine;

        Map<CardComponent, Boolean> batteryUsage = new HashMap<>();
        batteryUsage.put(doubleEngine, false); // Don't use battery

        double enginePower = quickShip.calculateEnginePower(batteryUsage);
        assertEquals(2.0, enginePower); // 1.0 (engine) + 1.0 (double engine without battery)
    }


    @Test
    void testEmptyBatteryUsageMap() {
        quickShip.initializeShipPlance();
        CardComponent[][] shipBoard = quickShip.getShipBoard();

        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Empty_Connector);
        connectors.put(South, Empty_Connector);
        connectors.put(East, Empty_Connector);
        connectors.put(West, Empty_Connector);
        CardComponent doubleEngine = new CardComponent(DoubleEngine, connectors, "");
        shipBoard[1][2] = doubleEngine;

        Map<CardComponent, Boolean> emptyBatteryUsage = new HashMap<>();

        double power = quickShip.calculateEnginePower(emptyBatteryUsage);
        assertEquals(1.0, power); // Default behavior when not in map should be no battery usage
    }
}