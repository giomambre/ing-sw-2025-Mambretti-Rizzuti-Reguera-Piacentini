package it.polimi.ingsw;
import com.sun.source.tree.AssertTree;
import impl.org.controlsfx.tools.rectangle.change.NewChangeStrategy;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.CardAdventureType.*;
import static it.polimi.ingsw.model.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class AbandonedStationTest {

    Player player1, player2, player3;
    Board board;
    CardAdventure abd_station ;
    Map<Direction, ConnectorType> connectors = new HashMap<>();

    @BeforeEach
    void setUp() {
        player1 = new Player("Alice", Yellow);
        player2 = new Player("Mambre", Blue);
        player3 = new Player("isabel", Red);
        board = new Board(Arrays.asList (player1,player2,player3));
        player1.getShip().initializeShipPlance();
        player2.getShip().initializeShipPlance();
        player3.getShip().initializeShipPlance();


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        List<Cargo> cargos = new ArrayList<>();
        cargos.add(Cargo.Green);
        cargos.add(Cargo.Yellow);


        abd_station = new AbandonedStation(1,1,AbandonedStation,5,board,cargos);




    }

    @Test
    void testAbandonedStation() {
        Ship ship_1 = player1.getShip();

        ship_1.AddComponent(new Storage(RedStorage,connectors,2),3,1);
        ship_1.AddComponent(new Storage(BlueStorage,connectors,3),4,1);

        assertEquals(board.GetBoard().get(7),player1);

        Map<CardComponent , Map<Cargo,Integer>> new_cargo_positions = new HashMap<>();

        Map<Cargo,Integer> new_cargo = new HashMap();
        ship_1.getComponent(3,1);
        new_cargo.put(((AbandonedStation) abd_station).getCargo().get(0),0);
        new_cargo.put(((AbandonedStation) abd_station).getCargo().get(1),1);
        new_cargo_positions.put(ship_1.getComponent(3,1),new_cargo);

        assertEquals(0,((Storage) ship_1.getComponent(3,1)).getCargoCount());

        ((AbandonedStation) abd_station).execute(player1,new_cargo_positions);

        assertEquals(2,((Storage) ship_1.getComponent(3,1)).getCargoCount());

        assertEquals(((Storage) ship_1.getComponent(3,1)).getCargo(0),Cargo.Green);
        assertEquals(((Storage) ship_1.getComponent(3,1)).getCargo(1),Cargo.Yellow);



    }

}
