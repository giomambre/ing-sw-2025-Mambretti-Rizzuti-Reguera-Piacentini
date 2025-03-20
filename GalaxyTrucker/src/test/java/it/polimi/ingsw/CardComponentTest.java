package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ConnectorType.Smooth;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.Direction.West;
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

        component = new CardComponent(ComponentType.BlueStorage, connectors);
    }

    @Test
    public void testchangeFaceShowed(){
        component.changeFaceShowed();
        assertEquals(false, component.getFaceDown() );
    }

    @Test
    public void testrotate(){
        component.rotate();
        component.rotate();
        assertEquals(Universal, component.getConnector_type(South));
    }


}
