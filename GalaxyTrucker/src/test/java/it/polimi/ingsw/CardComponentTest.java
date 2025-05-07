package it.polimi.ingsw;

import it.polimi.ingsw.model.components.CardComponent;
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
    }


}
