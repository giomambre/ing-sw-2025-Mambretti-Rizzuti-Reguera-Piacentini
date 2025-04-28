package controllerTests;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Cargo.Green;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.CrewmateType.Astronaut;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameControllerTest {
    GameController controller;
    Lobby lobby;
    GameState game_state;
    BaseGame game;
    @BeforeEach
    public void setup() {
        lobby = new Lobby("Gio",2);

        controller = new GameController(lobby);
        controller.addPlayer("Gio",GREEN);
        controller.addPlayer("Isabel",RED);
        controller.startGame();
    }


@Test
    public void testAddPlayer(){





        Assertions.assertEquals(2, controller.getPlayers().size());
        assertEquals("Gio",controller.getPlayers().get(0).getNickname());
        assertEquals("Isabel",controller.getPlayers().get(1).getNickname());
        assertEquals(GREEN,controller.getPlayers().get(0).getColor());
        assertEquals(RED,controller.getPlayers().get(1).getColor());


    }

    @Test
    public void testEndPlayerBuildPhase(){
assertEquals(-1,controller.endPlayerBuildPhase("Gio"));
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

controller.addComponent("Isabel",new CardComponent(Engine, connectors), 1, 4);
assertEquals(1,controller.endPlayerBuildPhase("Isabel"));


    }


    @Test
    public void testAddComponent(){


        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        controller.addComponent("Isabel",new CardComponent(Engine, connectors), 1, 4);

        assertEquals(Engine,controller.getPlayers().get(1).getShip().getComponent(1,4).getComponentType());
        controller.addComponent("Isabel",new CardComponent(Cannon, connectors), 1, 5);
        assertEquals(Cannon,controller.getPlayers().get(1).getShip().getComponent(1,5).getComponentType());



    }

    @Test
    public void testsecureComponent(){

        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        controller.secureComponent("Isabel",new CardComponent(Engine, connectors));

        assertEquals(1,controller.getPlayers().get(1).getShip().getExtra_components().size());
        assertEquals(Engine,controller.getPlayers().get(1).getShip().getExtra_components().getFirst().getComponentType());


    }

    @Test
    public void testCrewmateSupply(){
        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);
        controller.addComponent("Isabel",new LivingUnit(LivingUnit, connectors), 1, 4);

        controller.crewmatesSupply("Isabel",1,4,Astronaut);
        assertEquals(2,((LivingUnit) controller.getPlayers().get(1).getShip().getComponent(1,4)).getNum_crewmates());
        assertEquals(Astronaut,((LivingUnit) controller.getPlayers().get(1).getShip().getComponent(1,4)).getCrewmate_type());




    }
}
