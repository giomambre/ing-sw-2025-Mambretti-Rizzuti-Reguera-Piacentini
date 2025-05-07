package it.polimi.ingsw;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
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

public class LivingUnitTest {
    private CardComponent component;
    private Map<Direction, ConnectorType> connectors;


    @BeforeEach
    void setUp() {
        connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);


    }

    @Test
    public void testAddAstronaut() {
        component = new LivingUnit(ComponentType.LivingUnit, connectors,"");
        ((LivingUnit)component).addAstronauts();
        assertEquals(2, ((LivingUnit)component).getNum_crewmates());
        assertEquals(CrewmateType.Astronaut, ((LivingUnit)component).getCrewmate_type());

    }

    @Test
    public void testAddAlien() {
        component = new LivingUnit(ComponentType.PinkAlienUnit, connectors,"");
        ((LivingUnit)component).addAlien(CrewmateType.PinkAlien);
        assertEquals(1, ((LivingUnit)component).getNum_crewmates());
        assertEquals(CrewmateType.PinkAlien, ((LivingUnit)component).getCrewmate_type());

    }


}
