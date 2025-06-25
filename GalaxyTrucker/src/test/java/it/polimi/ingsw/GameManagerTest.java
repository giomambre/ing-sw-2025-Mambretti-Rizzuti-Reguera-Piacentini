package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameManagerTest {
    GameManager gameManager;

    @BeforeEach
    public void setUp() {
        gameManager = new GameManager();
    }
    @Test
    void testGameManager() {
        int l=gameManager.createLobby("r",3);
        gameManager.joinLobby("s", l);

        assertEquals(gameManager.getAvaibleLobbies().size(), 1);
        assertEquals(gameManager.getAllLobbies().getFirst(), gameManager.getLobby(l));
    }
}
