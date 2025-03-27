package it.polimi.ingsw;

import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatteryTest {

    private Battery battery;
    private Map<Direction, ConnectorType> connectors;


    @BeforeEach
    void setUp() {
        connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        battery = new Battery(ComponentType.Battery, connectors, 2);
    }

    @Test
    public void testAddBattery() {
        battery.addBattery(1);
        assertEquals(1, battery.getStored());
    }

    @Test
    public void testRemoveBattery() {
        battery.addBattery(2);
        battery.removeBattery();
        assertEquals(1, battery.getStored());
    }
}
