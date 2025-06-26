package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameControllerTest {
    GameController gameController;
    Lobby lobby;

    @BeforeEach
    public void setUp() {

        lobby = new Lobby("raffa", 3);
        gameController = new GameController(lobby);
    }

    @Test
    public void testGetter() {
        assertTrue(gameController.getDisconnected_players().isEmpty());
        assertTrue(gameController.getCurr_adventure_player().isEmpty());
        gameController.getLobby();
        gameController.getBoard();
        gameController.getCurrentAdventure();
        gameController.getListCannonPower();
        gameController.getEngineValues();
        gameController.getAdv_done();
        gameController.getPlanets();
        gameController.getAdv_index();
        gameController.throwDice();
        gameController.getActivePlayers();
        gameController.getAvailable_colors();
        gameController.getRandomAdventure();
        gameController.getFacedUpCards();


        gameController.addWaitingFlyPlayer("raffa");
        assertEquals(gameController.getWaitingFlyPlayers().getFirst(), "raffa");

        gameController.setGamestate(GameState.FIXING_SHIPS);
        assertTrue(gameController.getGamestate() == GameState.FIXING_SHIPS);

        gameController.setCurr_adventure_player("gio");
        assertEquals(gameController.getCurr_adventure_player(), "gio");

        gameController.setCurr_combatzone("can");
        assertEquals(gameController.getCurr_combatzone(), "can");

        gameController.setIn_pause(1);
        assertEquals(gameController.getIn_pause(), 1);

        gameController.setPirates_coords("2 3");
        assertEquals(gameController.getPirates_coords(), "2 3");


    }

    @Test
    public void testCannonEngine(){
        gameController.addCannonValue("raffa" , 1);
        gameController.addEngineValue("raffa" , 1.0);

        assertEquals(gameController.getLeastCannon(), "raffa");
        assertEquals(gameController.getLeastEngineValue(), "raffa");
    }

    @Test
    public void testAdv(){
        gameController.addPlayer("raffa", Color.YELLOW);
        gameController.startGame();
        gameController.startFlight();
        gameController.initializeAdventure(new Stardust(1,2, CardAdventureType.Stardust, ""));
        gameController.initializeAdventure(new AbandonedStation(1,2, CardAdventureType.AbandonedStation, 2, List.of(Cargo.Green), "" ));
        gameController.initializeAdventure(new AbandonedShip(1,2, CardAdventureType.AbandonedShip, 2, 3, "" ));
        gameController.initializeAdventure(new Planets(1,2, CardAdventureType.Planets,  List.of(List.of(Cargo.Green)),  "" ));

        assertEquals(gameController.getAdventureOrder().size(), 0);
    }

    @Test
    void testPlanets(){
        gameController.addPlanetTaken("planet");
        assertEquals(gameController.getPlanets(), " planet");
    }



}
