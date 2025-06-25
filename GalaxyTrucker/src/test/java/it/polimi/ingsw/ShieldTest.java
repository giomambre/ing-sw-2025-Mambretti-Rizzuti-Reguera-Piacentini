package it.polimi.ingsw;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Shield;
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
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShieldTest {
    private Shield component;
    private Map<Direction, ConnectorType> connectors;


    @BeforeEach
    void setUp() {
        connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        component = new Shield(ComponentType.Shield, connectors,"");
    }

    @Test
    void testShield() {
        component.copy();
        component.rotate();

        Map<Direction, Boolean> coveredSide = new HashMap<>();
        coveredSide.put(North, FALSE);
        coveredSide.put(South, FALSE);
        coveredSide.put(East, FALSE);
        coveredSide.put(West, TRUE);
        component.setCovered_sides(coveredSide);

        assertEquals(coveredSide,component.getCoveredSides());

    }
}
