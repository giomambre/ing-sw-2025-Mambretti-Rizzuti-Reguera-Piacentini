package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private CardComponent component;
    private Ship ship;
    private Map<Direction, ConnectorType> Map;


    @BeforeEach
    public void setUp() {
        player = new Player("raffa", Color.Yellow);
        ship = new Ship(player);
        Map = java.util.Map.of(Direction.East , ConnectorType.Universal);
        component = new CardComponent(ComponentType.Cannon, Map);
    }






}
