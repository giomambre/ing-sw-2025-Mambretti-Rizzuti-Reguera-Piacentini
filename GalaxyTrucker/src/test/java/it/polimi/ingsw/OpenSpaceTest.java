package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.OpenSpace;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.CardAdventureType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
public class OpenSpaceTest {
    private CardAdventure openSpace ;
    private Board board;
    private Player player1, player2, player3, player4;
    private  Map<Player, Map<CardComponent, Boolean>> batteryUsageMap;
    Map<Direction, ConnectorType> connectors = new HashMap<>();

    @BeforeEach

    public void setUp() {

//manca da testare con alieni
        Game game = new Game(Gametype.StandardGame);
        player1 = new Player("Alice", YELLOW,game);
        player2 = new Player("Mambre", BLUE,game);
        player3 = new Player("isabel", RED,game);

        player1.getShip().initializeShipPlance();
        player2.getShip().initializeShipPlance();
        player3.getShip().initializeShipPlance();
        board = new Board(24,game);
        board.putPlayersOnBoard(Arrays.asList(player1, player2, player3));

        openSpace = new OpenSpace(2,0,OpenSpace);
        openSpace.setBoard(board);

        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector );
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        player1.getShip().addComponent(new CardComponent(DoubleEngine,connectors,""),3,2);
        player1.getShip().addComponent(new CardComponent(Engine,connectors,""),4,1);

        player2.getShip().addComponent(new CardComponent(DoubleEngine,connectors,""),3,2);
        player2.getShip().addComponent(new CardComponent(Engine,connectors,""),4,5);

        player3.getShip().addComponent(new CardComponent(DoubleEngine,connectors,""),3,2);











    }

    @Test
    public void testExecuteAdventureEffects() {

        batteryUsageMap = new HashMap<>();

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3,2), true);   // totale = 3
        player1BatteryUsage.put(player1.getShip().getComponent(4,1),false);
        batteryUsageMap.put(player1, player1BatteryUsage);

        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();
        player2BatteryUsage.put(player2.getShip().getComponent(3,2), false); // totale = 2
        player2BatteryUsage.put(player2.getShip().getComponent(4,5),false);
        batteryUsageMap.put(player2, player2BatteryUsage);

        Map<CardComponent, Boolean> player3BatteryUsage = new HashMap<>();
        player3BatteryUsage.put(player3.getShip().getComponent(3,2), true); //totale = 2

        batteryUsageMap.put(player3, player3BatteryUsage);


        List <Player> players = Arrays.asList(player1, player2, player3);

       // ((OpenSpace)openSpace).execute(players,batteryUsageMap);

        Map<Integer, Player> playerPositions = board.getBoard();

        //partenza 2,4,7
        assertEquals(playerPositions.get(10), player1);
        assertEquals(playerPositions.get(6), player2);
        assertEquals(playerPositions.get(4), player3);

        assertNull(playerPositions.get(7));
        assertNull(playerPositions.get(2));

    }

    @Test
    public void testExecuteAdventureEffects_NoBatteries() {

        batteryUsageMap = new HashMap<>();



        // Disattiva tutte le batterie
        for (Map<CardComponent, Boolean> batteryMap : batteryUsageMap.values()) {
            batteryMap.replaceAll((k, v) -> false);
        }

        List<Player> players = Arrays.asList(player1, player2, player3);
       // ((OpenSpace)openSpace).execute(players,batteryUsageMap);

        Map<Integer, Player> playerPositions = board.getBoard();

        //partenza 2,4,7

        assertEquals(player1, playerPositions.get(9));
        assertEquals(player2, playerPositions.get(6));
        assertEquals(player3, playerPositions.get(3));
        assertNull(playerPositions.get(7));
        assertNull(playerPositions.get(2));
        assertNull(playerPositions.get(4));

    }



    @Test

    public void testExecuteAdventureEffects_CorrectOrder() {
        player2.getShip().addComponent(new CardComponent(Engine,connectors,""),3,3);
        player2.getShip().addComponent(new CardComponent(Engine,connectors,""),3,4);
        player2.getShip().addComponent(new CardComponent(Engine,connectors,""),3,5);
        player2.getShip().addComponent(new CardComponent(Engine,connectors,""),3,6);

        batteryUsageMap = new HashMap<>();

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3,2), true);   // totale = 3
        player1BatteryUsage.put(player1.getShip().getComponent(4,1),false);
        batteryUsageMap.put(player1, player1BatteryUsage);

        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();
        player2BatteryUsage.put(player2.getShip().getComponent(3,2), false); // totale = 6
        player2BatteryUsage.put(player2.getShip().getComponent(4,5),false);
        batteryUsageMap.put(player2, player2BatteryUsage);

        Map<CardComponent, Boolean> player3BatteryUsage = new HashMap<>();
        player3BatteryUsage.put(player3.getShip().getComponent(3,2), true); //totale = 2

        batteryUsageMap.put(player3, player3BatteryUsage);


        List <Player> players = Arrays.asList(player1, player2, player3);

        //((OpenSpace)openSpace).execute(players,batteryUsageMap);

        Map<Integer, Player> playerPositions = board.getBoard();

        //partenza 2,4,7
        //player 1 si muove prima di player 2 che arrivano entrambi a 10, ma il player 2 lo sorpassa

        assertEquals(playerPositions.get(10), player1);
        assertEquals(playerPositions.get(11),player2);
        assertNull(playerPositions.get(7));
        assertNull(playerPositions.get(2));
        assertEquals(playerPositions.get(4), player3);



    }

}
