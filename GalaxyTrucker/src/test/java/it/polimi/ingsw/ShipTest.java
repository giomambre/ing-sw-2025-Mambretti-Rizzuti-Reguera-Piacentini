package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.CardAdventureType.*;
import static it.polimi.ingsw.model.Color.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class ShipTest {


    private Ship ship;
    private Player player;
    @BeforeEach
    void setUp() {
        player = new Player("Reff",Green);
        ship = new Ship(player);



    }

    @Test
    public void testinitializeShipPlance() {


        ship.initializeShipPlance();


        assertEquals(MainUnitGreen,ship.getComponent(2,3).GetComponent_type());

        assertEquals(NotAccessible, ship.getComponent(0, 0).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(0, 1).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(0, 3).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(0, 5).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(0, 6).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(1, 0).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(1, 6).GetComponent_type());
        assertEquals(NotAccessible, ship.getComponent(4, 3).GetComponent_type());



    }
}
