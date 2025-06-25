package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.adventures.Stardust;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertEquals(gameController.getAdventureOrder().size(), 0);
    }


}
