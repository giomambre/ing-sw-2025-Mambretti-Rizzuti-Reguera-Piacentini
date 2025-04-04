package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Smugglers;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Cargo.Green;
import static it.polimi.ingsw.model.enumerates.Color.YELLOW;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SmugglersTest {
    CardAdventure smugglers;
    Player player;
    Ship ship;
    Board board;

    @BeforeEach
    public void setup() {

        player = new Player("Cice", YELLOW);
        ship = player.getShip();
        ship.initializeShipPlance();
        board = new Board(Arrays.asList(player));
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        ship.addComponent(new Storage(ComponentType.BlueStorage, connectors, 3),3,1);
        Map<Cargo, Integer> cargos = new HashMap<>();
        cargos.put(Blue, 2);
        ((Storage)ship.getComponent(3,1)).addCargo(cargos);

        ship.addComponent(new Storage(ComponentType.RedStorage, connectors, 3),3,2);
        cargos.put(Cargo.Red, 1);
        ((Storage)ship.getComponent(3,2)).addCargo(cargos);

        ship.addComponent(new Storage(ComponentType.BlueStorage, connectors, 3),3,3);

        List<Cargo> cargo_rewards = new ArrayList<>();
        cargo_rewards.add(Cargo.Blue);
        cargo_rewards.add(Cargo.Blue);

        smugglers = new Smugglers(2,2,CardAdventureType.Smugglers, board, 3,cargo_rewards, 2);

    }

    @Test
    public void testExecuteWin() {

        Map<CardComponent, Map<Cargo, Integer>> choosen_planets = new HashMap<>();

        Map<Cargo, Integer> cargoMap= new HashMap<>();
        cargoMap.put(Blue, 0);
        cargoMap.put(Green, 1);

        choosen_planets.put(ship.getComponent(3,3), cargoMap);

        ((Smugglers)smugglers).executeWin(player, choosen_planets,true);

        assertEquals(2, ((Storage)ship.getComponent(3,3)).getCargoCount());
        Assertions.assertEquals(board.getBoard().get(5),player);
        assertNull(board.getBoard().get(7));
    }

    @Test
    public void testExecuteLoss() {

        Map<CardComponent, Map<Cargo, Integer>> cargo_loss = new HashMap<>();

        Map<Cargo, Integer> cargoMap= new HashMap<>();
        cargoMap.put(Blue, 2);

        cargo_loss.put(ship.getComponent(3,1), cargoMap);
        ((Smugglers)smugglers).executeLoss(player, cargo_loss);

        assertEquals(0, ((Storage)ship.getComponent(3,1)).getCargoCount());
        Assertions.assertEquals(board.getBoard().get(7),player);

    }
}
