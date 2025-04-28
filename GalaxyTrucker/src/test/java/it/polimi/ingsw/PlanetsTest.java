package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Planets;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.messages.StandardMessageClient;
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

public class PlanetsTest {
    CardAdventure planets;
    Player player;
    Ship ship;
    Board board;

    @BeforeEach
    public void setup() {
        Game game = new Game(Gametype.StandardGame);
        player = new Player("Cice", YELLOW,game);
        ship = player.getShip();
        ship.initializeShipPlance();
        board = new Board(24,game);
        board.putPlayersOnBoard(Arrays.asList(player));
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


        List<List<Cargo>> cargo_reward = new ArrayList<>();
        cargo_reward.add(new ArrayList<>(List.of(Blue, Green)));
        cargo_reward.add(new ArrayList<>(List.of(Blue, Green)));


        planets = new Planets(2,2, CardAdventureType.Planets, cargo_reward );
        planets.setBoard(board);



    }

    @Test
    public void planetsTest() {

        Map<CardComponent, Map<Cargo, Integer>> choosen_planets = new HashMap<>();

        Map<Cargo, Integer> cargoMap= new HashMap<>();
        cargoMap.put(Blue, 0);
        cargoMap.put(Green, 1);

        choosen_planets.put(ship.getComponent(3,3), cargoMap);

        ((Planets)planets).execute(player, choosen_planets);

        assertEquals(2, ((Storage)ship.getComponent(3,3)).getCargoCount());
        Assertions.assertEquals(board.getBoard().get(5),player);
        assertNull(board.getBoard().get(7));
    }
}
