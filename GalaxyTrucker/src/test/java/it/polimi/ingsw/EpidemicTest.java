package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Epidemic;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Color.YELLOW;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpidemicTest {


    private CardAdventure epidemic;
    private Player player;
    private Ship ship;
    private Board board;

    @BeforeEach
    void setUp() {
        Game game = new Game(Gametype.StandardGame);
        player = new Player("Cice", YELLOW,game);
        ship = player.getShip();
        ship.initializeShipPlance();
        board = new Board(Arrays.asList(player),24,game);
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        ship.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors),3,1);
        ((LivingUnit)ship.getComponent(3,1)).addAstronauts();

        ship.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors),3,2);
        ((LivingUnit)ship.getComponent(3,2)).addAlien(CrewmateType.BrownAlien);

        ship.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors),3,3);
        ((LivingUnit)ship.getComponent(3,3)).addAlien(CrewmateType.PinkAlien);

    }

    @Test
    public void epidemicTest() {
        epidemic = new Epidemic(2,0, CardAdventureType.Epidemic, board);

        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North,Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        ship.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors),2,4);
        ((LivingUnit)ship.getComponent(2,4)).addAstronauts();

        connectors.put(North,Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        ship.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors),2,5);
        ((LivingUnit)ship.getComponent(2,5)).addAstronauts();

        ((Epidemic)epidemic).execute(player);

        assertEquals(1,((LivingUnit)ship.getComponent(3,1)).getNum_crewmates());
        assertEquals(0,((LivingUnit)ship.getComponent(3,2)).getNum_crewmates());
        assertEquals(0,((LivingUnit)ship.getComponent(3,3)).getNum_crewmates());
        assertEquals(0,((LivingUnit)ship.getComponent(2,3)).getNum_crewmates());
        assertEquals(1,((LivingUnit)ship.getComponent(2,4)).getNum_crewmates());
        assertEquals(2,((LivingUnit)ship.getComponent(2,5)).getNum_crewmates());
    }
}
