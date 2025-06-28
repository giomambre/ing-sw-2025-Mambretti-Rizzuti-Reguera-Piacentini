package controllerTests;

import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameManagerTest {

    GameManager gameManager = new GameManager();
    Game game = new Game(Gametype.StandardGame);
    Player player = new Player("Isa", Color.RED,game);
    Player player_1 = new Player("Raffa", Color.BLUE,game);

    @BeforeEach
    public void init() {
        game.addPlayer(player);
        game.addPlayer(player_1);
        gameManager.createLobby("Isa",2);

    }

    @Test
    public void testCreateLobby() {

        assertEquals(1,gameManager.getAvaibleLobbies().size());
        assertEquals( 1,gameManager.getAllLobbies().size());


    }

    @Test
    public void testJoinPlayer() {
        gameManager.joinLobby("Raffa",1);
        assertEquals(0,gameManager.getAvaibleLobbies().size());
        assertEquals(2,gameManager.getLobby(1).getPlayers().size());
        assertEquals(2,gameManager.getLobby(1).getLimit());
        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.joinLobby("Raffa", 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameManager.joinLobby("Raffa", 1);
        });



    }
}
