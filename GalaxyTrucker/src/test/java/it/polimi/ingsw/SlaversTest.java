package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Slavers;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SlaversTest {
    CardAdventure slayers;
    Player player1;
    Ship ship1;
    Board board;

    @BeforeEach
    public void setup() {
        player1 = new Player("Reff", GREEN);
        ship1 = player1.getShip();
        board = new Board(Arrays.asList (player1));
        ship1.initializeShipPlance();


        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);
        ship1.addComponent(new Battery(ComponentType.Battery, connectors,2), 0, 2);

        connectors.put(North, Cannon_Connector);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors), 1, 1);


        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Battery(ComponentType.Battery, connectors,2), 1, 2);


        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new CardComponent(Tubes, connectors), 1, 3);


        connectors.put(North, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors), 1, 4);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new Shield(ComponentType.Shield, connectors), 1, 5);

        Map<Direction,Boolean> covered_sides = new HashMap<>();
        covered_sides.put(North, true);
        covered_sides.put(East, false);
        covered_sides.put(South, false);
        covered_sides.put(West, true);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(PinkAlienUnit, connectors), 2, 0);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors), 2, 1);

        ((LivingUnit) ship1.getComponent(2,1)).addAlien(PinkAlien);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new Battery(ComponentType.Battery, connectors,3), 2, 2);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(RedStorage, connectors,1), 2, 4);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors), 2, 5);

        connectors.put(North, Smooth);
        connectors.put(East, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors), 2, 6);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Cannon_Connector);

        ship1.addComponent(new CardComponent(Cannon, connectors), 3, 0);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors), 3, 1);

        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new LivingUnit(LivingUnit, connectors), 3, 2);
        ((LivingUnit) ship1.getComponent(3,2)).addAstronauts();

        connectors.put(North, Universal);
        connectors.put(East, Double);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors), 3, 3);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new Storage(BlueStorage, connectors,2), 3, 4);

        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors), 3, 5);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(DoubleEngine, connectors), 3, 6);

        connectors.put(North, Double);
        connectors.put(East, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors), 4, 1);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(BlueStorage, connectors,2), 4, 2);


        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);

        ship1.addComponent(new Storage(RedStorage, connectors,1), 4, 4);

        connectors.put(North, Double);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors), 4, 5);

        slayers = new Slavers(2,2,CardAdventureType.Slayers, board, 3,3, 9);

    }

    @Test
    public void testExecuteWin() {
        ((Slavers)slayers).executeWin(player1);
        assertEquals(9, player1.getCredits());
        Assertions.assertEquals(board.getBoard().get(5),player1);
    }

    @Test
    public void testExecuteLoss() {
        Map<CardComponent,Integer> astronaut_losses = new HashMap<>();
        astronaut_losses.put(ship1.getComponent(3,2),1);

        ((Slavers)slayers).executeLoss(player1,astronaut_losses);
        Assertions.assertEquals(1, ((LivingUnit)ship1.getComponent(3,2)).getNum_crewmates());

    }
}
