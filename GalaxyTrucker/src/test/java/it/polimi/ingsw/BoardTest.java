package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;

public class BoardTest {

    @Test
    public void testMovePlayer() {

        Player player1 = new Player("raffa", Color.Yellow);
        List<Player> players = new ArrayList<>(Arrays.asList(player1));
        Board board = new Board(players);

        board.MovePlayer(player1, 3);

        Map<Integer, Player> player_position = new HashMap<>();
        player_position = board.GetBoard();
        Integer playerPosition = 0;

        for (var entry : player_position.entrySet()) {
            if (entry.getValue().equals(player1)) {
                playerPosition = entry.getKey();
            }
        }

        assertEquals(Optional.of(10), playerPosition);

    }
}
