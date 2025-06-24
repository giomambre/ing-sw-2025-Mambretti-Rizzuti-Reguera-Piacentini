package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
public class StorageTest {
    private Player player;
    Map<Direction, ConnectorType> connectors = new HashMap<>();

    @BeforeEach
    public void setUp() {
        Game game = new Game(Gametype.StandardGame);
        player = new Player("jonny", BLUE,game);
        player.getShip().initializeShipPlance();

        connectors.put(North, Universal);
        connectors.put(South, Double );
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);
        player.getShip().addComponent((new Storage(BlueStorage,connectors,3,"")),3,1);





    }

    @Test
    public void testStorage() {
        Storage storage = (Storage) player.getShip().getComponent(3,1);
        storage.copy();
        assertEquals(Cargo.Empty,storage.getCargo(0));
        assertEquals(Cargo.Empty,storage.getCargo(1));
        assertEquals(Cargo.Empty,storage.getCargo(2));


    }


    @Test
    public void testaddCargo() {

        Map<Cargo, Integer> cargoMap = new HashMap<>();
        cargoMap.put(Cargo.Green,0);
        cargoMap.put(Cargo.Red,1);
        ((Storage) player.getShip().getComponent(3,1)).addCargo(cargoMap);

        Storage storage = (Storage) player.getShip().getComponent(3,1);
        assertEquals(Cargo.Green,storage.getCargo(0));
        assertEquals(Cargo.Red,storage.getCargo(1));
        assertEquals(Cargo.Empty,storage.getCargo(2));





    }

    @Test
    public void testRemoveCargo() {
        Map<Cargo, Integer> cargoMap = new HashMap<>();
        cargoMap.put(Cargo.Green,0);
        cargoMap.put(Cargo.Red,1);
        ((Storage) player.getShip().getComponent(3,1)).addCargo(cargoMap);

        Storage storage = (Storage) player.getShip().getComponent(3,1);


        ((Storage) player.getShip().getComponent(3,1)).removeCargo(Cargo.Red);

        assertEquals(Cargo.Green,storage.getCargo(0));
        assertEquals(Cargo.Empty,storage.getCargo(1));

        storage.addCargo(Cargo.Red,2);
        assertEquals(Cargo.Red,storage.getCargo(2));

        assertEquals(2,storage.getCargoCount());
        assertEquals(3,storage.getSize());
        assertEquals(true,storage.containsCargo(Cargo.Red));

        storage.setCarried_cargos(List.of(Cargo.Red));

    }


}
