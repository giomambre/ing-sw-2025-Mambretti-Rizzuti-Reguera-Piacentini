package it.polimi.ingsw;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardComponentTest {

    private CardComponent component;
    private Map<Direction, ConnectorType> connectors;


    @BeforeEach
    void setUp() {
        connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        component = new CardComponent(ComponentType.BlueStorage, connectors,"");
    }



    @Test
    public void testrotate(){
        component.rotate();
        component.rotate();
        assertEquals(Universal, component.getConnector_type(South));

        component.setRotationAngle(90);
        assertEquals(90, component.getRotationAngle());
    }

    @Test
    public void testConnector(){
        component.copy();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            component.getValidConnectors(Empty_Connector);
        });

        assertEquals("You cant use an empty connector", exception.getMessage());

        assertEquals(connectors, component.getConnectors());
        assertEquals("", component.getImagePath());

    }

    @Test
    public void testUuid(){
        UUID uuid = UUID.randomUUID();
        component.setCard_uuid(uuid);
        assertEquals(uuid, component.getCard_uuid());
    }


}
