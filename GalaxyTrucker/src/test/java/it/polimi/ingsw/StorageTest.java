package it.polimi.ingsw;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.CardAdventureType.*;
import static it.polimi.ingsw.model.Color.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
public class StorageTest {
    private Player player;
    Map<Direction, ConnectorType> connectors = new HashMap<>();

    @BeforeEach
    public void setUp() {

        player = new Player("jonny",Blue);
        player.getShip().initializeShipPlance();

        connectors.put(North, Universal);
        connectors.put(South, Double );
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        player.getShip().addComponent((new Storage(BlueStorage,connectors,3)),3,1);





    }

    @Test
    //verifico costruttore e Emptyness

    public void testStorage() {

        Storage storage = (Storage) player.getShip().getComponent(3,1);
        assertEquals(Cargo.Empty,storage.getCargo(0));
        assertEquals(Cargo.Empty,storage.getCargo(1));
        assertEquals(Cargo.Empty,storage.getCargo(2));


    }


    @Test


    public void testaddCargo() {

        Map<Cargo, Integer> cargoMap = new HashMap<>();
        cargoMap.put(Cargo.Green,0);
        cargoMap.put(Cargo.Red,1);
        ((Storage) player.getShip().getComponent(3,1)).addCargo(cargoMap); // da capire come gestire la ClassCastException penso nel Controller

        Storage storage = (Storage) player.getShip().getComponent(3,1);
        assertEquals(Cargo.Green,storage.getCargo(0));
        assertEquals(Cargo.Red,storage.getCargo(1));
        assertEquals(Cargo.Empty,storage.getCargo(2));


    }


}
