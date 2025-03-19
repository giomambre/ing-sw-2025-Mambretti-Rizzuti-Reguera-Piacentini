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


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class OpenSpaceTest {
    private OpenSpace openSpace ;
    private Board board;
    private Player player1, player2, player3, player4;
    private  Map<Player, Map<CardComponent, Boolean>> batteryUsageMap;
    @BeforeEach

    public void setUp() {

//manca da testare con alieni

        player1 = new Player("Alice", Yellow);
        player2 = new Player("Mambre", Blue);
        player3 = new Player("isabel", Red);

        board = new Board(Arrays.asList(player1, player2, player3, player4));

        openSpace = new OpenSpace(2,0,OpenSpace,board);

        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector );
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        player1.getShip().AddComponent(new CardComponent(DoubleEngine,connectors),3,2);
        player1.getShip().AddComponent(new CardComponent(Engine,connectors),4,1);

        player2.getShip().AddComponent(new CardComponent(DoubleEngine,connectors),3,2);
        player2.getShip().AddComponent(new CardComponent(Engine,connectors),4,5);

        player3.getShip().AddComponent(new CardComponent(DoubleEngine,connectors),3,2);



         batteryUsageMap = new HashMap<>();

        Map<CardComponent, Boolean> player1BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player1.getShip().getComponent(3,2), true);   // totale = 3
        player1BatteryUsage.put(player1.getShip().getComponent(4,1),false);
        batteryUsageMap.put(player1, player1BatteryUsage);

        Map<CardComponent, Boolean> player2BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player2.getShip().getComponent(3,2), false); // totale = 2
        player1BatteryUsage.put(player2.getShip().getComponent(4,5),false);
        batteryUsageMap.put(player2, player2BatteryUsage);

        Map<CardComponent, Boolean> player3BatteryUsage = new HashMap<>();
        player1BatteryUsage.put(player3.getShip().getComponent(3,2), true); //totale = 2

        batteryUsageMap.put(player3, player3BatteryUsage);










    }

    @Test
    public void testExecuteAdventureEffects() {

        List <Player> players = Arrays.asList(player1, player2, player3, player4);

        openSpace.executeAdventureEffects(players,batteryUsageMap);

        Map<Integer, Player> playerPositions = board.GetBoard();

        //partenza 2,4,7
        assertEquals(playerPositions.get(10), player1);
        assertNull(playerPositions.get(7));
        assertEquals(playerPositions.get(6), player2);
        assertEquals(playerPositions.get(4), player3);
    }
}
