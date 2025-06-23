package it.polimi.ingsw;

import it.polimi.ingsw.model.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LobbyTest {
    private Lobby lobby;

    @BeforeEach
    public void setUp() {
         lobby = new Lobby("raffa" , 3);
    }

    @Test
    public void testLobby() {
        lobby.join("gio");
        lobby.join("alice");
        assertEquals(lobby.isLobbyFull(), true);
        assertEquals(lobby.isPlayerInLobby("alice"), true);

        lobby.removePlayer("alice");
        assertEquals(lobby.isLobbyFull(), false);
        assertEquals(lobby.isPlayerInLobby("alice"), false);

        assertEquals(lobby.getLobbyId(), 1);
        assertEquals(lobby.getLimit(), 3);
        assertEquals(lobby.getPlayers().size(), 2);
    }
}
