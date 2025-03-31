package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.AbandonedShip;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.CardAdventureType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class AbandonedShipTest {
    private CardAdventure abandonedShip;
    private Player player;
    private Ship ship;
    private Board board;

    @BeforeEach
    void setUp() {

        player = new Player("Cice", YELLOW);
        ship = player.getShip();
        ship.initializeShipPlance();
        board = new Board(Arrays.asList(player));
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        ship.addComponent(new LivingUnit(LivingUnit, connectors),3,1);
        ((LivingUnit)ship.getComponent(3,1)).addAlien(CrewmateType.Astronaut);

        ship.addComponent(new LivingUnit(LivingUnit, connectors),3,2);
        ((LivingUnit)ship.getComponent(3,2)).addAlien(CrewmateType.BrownAlien);

        ship.addComponent(new LivingUnit(LivingUnit, connectors),3,3);
        ((LivingUnit)ship.getComponent(3,3)).addAlien(CrewmateType.PinkAlien);

    }

    @Test

    public void testAbandonedShip() {
        abandonedShip = new AbandonedShip(2,5,AbandonedShip,board,5,3);

        Map<CardComponent,Integer> astronaut_losses = new HashMap<>();
        astronaut_losses.put(ship.getComponent(3,1),2);
        astronaut_losses.put(ship.getComponent(3,2),1);
        astronaut_losses.put(ship.getComponent(3,3),0);

        assertEquals(0,player.getCredits());
        assertEquals(4,ship.getNumOfCrewmates());

        ((AbandonedShip)abandonedShip).execute(player,astronaut_losses);

        assertEquals(5,player.getCredits());

        assertEquals(1,ship.getNumOfCrewmates());
        assertEquals(0,((LivingUnit)ship.getComponent(3,1)).getNum_crewmates());
        assertEquals(0,((LivingUnit)ship.getComponent(3,2)).getNum_crewmates());
        assertEquals(1,((LivingUnit)ship.getComponent(3,3)).getNum_crewmates());

         Map<Integer, Player> playerPositions = board.getBoard();
         assertNull(playerPositions.get(7));
         assertEquals(player,playerPositions.get(2));



    }

}
