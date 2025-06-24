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
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        battery = new Battery(ComponentType.Battery, connectors, 2,"");
    }


    @Test
    public void testBattery() {
        battery.copy();
        battery.removeBattery();
        assertEquals(1, battery.getStored());

        battery.addBattery(1);
        assertEquals(2, battery.getStored());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            battery.addBattery(2);
        });

        assertEquals("More batteries than the size of this component", exception.getMessage());

        battery.removeBattery();
        battery.removeBattery();

         exception = assertThrows(IllegalStateException.class, () -> {
             battery.removeBattery();
        });
         assertEquals("Cannot remove batteries from this component, is already empty", exception.getMessage());


        battery.setStored(2);
         assertEquals( 2, battery.getStored());

        assertEquals( 2, battery.getSize());

    }
}
