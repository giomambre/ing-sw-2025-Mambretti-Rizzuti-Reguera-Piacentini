package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class BoardTest {



    private Board board;
    private Player player1, player2, player3, player4;

    @BeforeEach //serve per fare il setup solo una volta per tutte prima di ogni test
    //prima di ogni test si ha questa situazione non altre modificate dagli altri test!!!!!
    public void setUp() {
        player1 = new Player("Alice", Color.Yellow);
        player2 = new Player("Mambre", Color.Blue);
        player3 = new Player("isabel", Color.Red);
        player4 = new Player("Raffa", Color.Green);

        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        board = new Board(players);
    }

    @Test
    public void testInitialPositions() {
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertEquals(player1, playerPositions.get(7));
        assertEquals(player2, playerPositions.get(4));
        assertEquals(player3, playerPositions.get(2));
        assertEquals(player4, playerPositions.get(1));
    }

    //CONSIGLIO PER CHI FA I TEST : li ho fatti a caso io, ma aiutatevi con le grafiche, però questo deve essere lo stile, inoltre per
    //runnarli dovete runnare questo file, non  App.java

    @Test
    public void testMovePlayerForward() {
        board.MovePlayer(player1, 3);
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertNull(playerPositions.get(7));  // Vecchia posizione vuota
        assertEquals(player1, playerPositions.get(10)); // Nuova posizione corretta
    }

    @Test
    public void testMovePlayerBackward() {
        board.MovePlayer(player1, -2);
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertNull(playerPositions.get(7)); // Vecchia posizione vuota
        assertEquals(player1, playerPositions.get(5)); // Nuova posizione corretta
    }

    @Test
    public void testMovePlayerSkippingOccupiedSpaces() {
        board.MovePlayer(player2, 3); // Mambre dovrebbe andare a 8 (salta 7 perché occupata)
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertEquals(player2, playerPositions.get(8));
    }

    @Test
    public void testMovePlayerWrappingAroundBoard() {
        board.MovePlayer(player1, 20); // Se la board ha 24 spazi, dovrebbe fare un giro e finire a (7+20) % 24 = 3
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertEquals(player1, playerPositions.get(3));
    }

    @Test
    public void testMovePlayerBackwardWrappingAroundBoard() {
        board.MovePlayer(player1, -8); // (7-8) + 24 = 20 perchè 3 caselle sono occupate dagli altri 3 player
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertEquals(player1, playerPositions.get(20));
    }

    @Test
    public void testMovePlayerForwardWrappingAroundBoardExtremeCase() {
        board.MovePlayer(player2, -5); // 4-5-2+24 = 21
        board.MovePlayer(player1, -10); // 7-10-3+24 = 18
        Map<Integer, Player> playerPositions = board.GetBoard();
        assertEquals(player2, playerPositions.get(21));
        assertEquals(player1, playerPositions.get(18));
    }

    @Test
    public void testRankingOrder() {
        board.MovePlayer(player1, 5); // Alice va avanti
        board.MovePlayer(player2, 10); // Mambre va ancora più avanti
        List<Player> ranking = board.GetRanking();

        assertEquals(player2, ranking.get(3)); // Mambre è in testa
        assertEquals(player1, ranking.get(2)); // Alice segue
        assertEquals(player3, ranking.get(1)); // isabel
        assertEquals(player4, ranking.get(0)); // Raffa è ultimo
    }
}
