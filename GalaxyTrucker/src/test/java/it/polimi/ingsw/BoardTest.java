package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.Gametype;
import it.polimi.ingsw.view.TUI.TUI;
import it.polimi.ingsw.view.View;
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
        Game game = new Game(Gametype.StandardGame);
        player1 = new Player("Alice", Color.YELLOW,game);
        player2 = new Player("Mambre", Color.BLUE,game);
        player3 = new Player("isabel", Color.RED,game);
        player4 = new Player("Raffa", Color.GREEN,game);

        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        board = new Board(24,game);
        board.putPlayersOnBoard(players);
    }

    @Test
    public void testInitialPositions() {
        Map<Integer, Player> playerPositions = board.getBoard();
        assertEquals(player1, playerPositions.get(7));
        assertEquals(player2, playerPositions.get(4));
        assertEquals(player3, playerPositions.get(2));
        assertEquals(player4, playerPositions.get(1));
    }

    //CONSIGLIO PER CHI FA I TEST : li ho fatti a caso io, ma aiutatevi con le grafiche, però questo deve essere lo stile, inoltre per
    //runnarli dovete runnare questo file, non  App.java

    @Test
    public void testmovePlayerForward() {
        board.movePlayer(player1, 3);
        Map<Integer, Player> playerPositions = board.getBoard();
        assertNull(playerPositions.get(7));  // Vecchia posizione vuota
        assertEquals(player1, playerPositions.get(10)); // Nuova posizione corretta
    }

    @Test
    public void testmovePlayerBackward() {
        board.movePlayer(player1, -2);
        Map<Integer, Player> playerPositions = board.getBoard();
        assertNull(playerPositions.get(7)); // Vecchia posizione vuota
        assertEquals(player1, playerPositions.get(5)); // Nuova posizione corretta
    }

    @Test
    public void testmovePlayerSkippingOccupiedSpaces() {
        board.movePlayer(player2, 3); // Mambre dovrebbe andare a 8 (salta 7 perché occupata)
        Map<Integer, Player> playerPositions = board.getBoard();
        assertEquals(player2, playerPositions.get(8));
    }

    @Test
    public void testmovePlayerWrappingAroundBoard() {
        board.movePlayer(player1, 20); // Se la board ha 24 spazi, dovrebbe fare un giro e finire a (7+20) % 24+3=6
        Map<Integer, Player> playerPositions = board.getBoard();
        assertEquals(player1, playerPositions.get(6));
    }

    @Test
    public void testmovePlayerBackwardWrappingAroundBoard() {
        board.movePlayer(player1, -8); // (7-8) + 24 = 20 perchè 3 caselle sono occupate dagli altri 3 player
        Map<Integer, Player> playerPositions = board.getBoard();
        assertEquals(player1, playerPositions.get(20));
    }

    @Test
    public void testmovePlayerForwardWrappingAroundBoardExtremeCase() {

        //iniziali 7 4 2 1
        board.movePlayer(player2, -5); // 4-5-2+24 = 21
        board.movePlayer(player1, -10); // 7-10-3+24 = 18
        Map<Integer, Player> playerPositions = board.getBoard();
        System.out.println(playerPositions);
        assertEquals(player2, playerPositions.get(21));
        assertEquals(player1, playerPositions.get(18));
    }

    @Test
    public void testRankingOrder() {
        board.movePlayer(player1, 5); // Alice va avanti arriva a 12
        board.movePlayer(player2, 10); // Mambre va ancora più avanti
        List<Player> ranking = board.getRanking();


        // 7 4 2 1
        System.out.println(ranking);
        assertEquals(player2, ranking.get(0)); // Mambre è in testa
        assertEquals(player1, ranking.get(1)); // Alice segue
        assertEquals(player3, ranking.get(2)); // isabel
        assertEquals(player4, ranking.get(3)); // Raffa ultimo
    }


    @Test
    public void checkLeaderTest() {

        Map<Integer, Player> playerPositions = board.getBoard();
        assertEquals(player1, playerPositions.get(7));
        assertEquals(player2, playerPositions.get(4));
        assertEquals(player3, playerPositions.get(2));
        assertEquals(player4, playerPositions.get(1));
        board.changeBoard_leader();
        assertEquals(player1, board.getBoard_leader());

        board.movePlayer(player2, 3);

        board.changeBoard_leader();
        assertEquals(player2, board.getBoard_leader());


        board.movePlayer(player4, 21);

        assertEquals(player4, board.getBoard_leader());





    }

    @Test
    public void testShowBoard() {
        Map<Integer, Player> playerPositions = board.getBoard();
        View v= new TUI();

        v.showBoard(playerPositions, board.getLaps());

    }
}
