package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private CardComponent component;
    private Map<Direction, ConnectorType> connectors;


    @BeforeEach
    public void setUp() {
        player = new Player("raffa", Color.Yellow);

        connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        component = new CardComponent(ComponentType.Cannon, connectors);
    }

    @Test
    public void testsecureComponent() {
        player.secureComponent(component);
        assertEquals(1, player.getShip().getExtra_components().size());
    }

    @Test
    public void testuseExtraComponent() {
        player.secureComponent(component);
        player.useExtraComponent(component);
        assertEquals(0, player.getShip().getExtra_components().size());

    }
}
