package it.polimi.ingsw;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.view.TUI.TUI;
import it.polimi.ingsw.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

public class BaseGameTest {

    private BaseGame baseGame;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    Board board = new Board(24,null);

    @BeforeEach
    void setUp() {
        baseGame = new Game(Gametype.StandardGame);
        player1 = new Player("Player1", Color.BLUE, baseGame);
        player2 = new Player("Player2", Color.GREEN,baseGame);
        player3 = new Player("Player3", Color.RED, baseGame);
        player4 = new Player("Player4", Color.RED, baseGame);

        baseGame.addPlayer(player1);
        baseGame.addPlayer(player2);
        baseGame.addPlayer(player3);
        player1.getShip().initializeShipPlance();
        player2.getShip().initializeShipPlance();
        player1.utilePerTestare();
        player2.utilePerTestare();
        player3.getShip().initializeShipPlance();
        player4.getShip().initializeShipPlance();
        player4.utilePerTestare();
        baseGame.addPlayer(player4);
        baseGame.setBoard(board);
        baseGame.setActivePlayers(List.of(player1, player2, player3));
        board.putPlayersOnBoard(List.of(player1, player2, player3,player4));

    }
    @Test
    void testSetAndGetNumPlayers() {
        baseGame.setNumPlayers(2);
        assertEquals(2, baseGame.getNumPlayers());
    }


    @Test
    void testSetRewards(){
        baseGame.setRewards();
        assertEquals(14,player1.getCredits());
        assertEquals(13,player2.getCredits());
        assertEquals(4,player3.getCredits());
        assertEquals(5,player4.getCredits());


    }


    @Test
    void testSetAndGetActivePlayers() {
        List<Player> activePlayers = new ArrayList<>();
        activePlayers.add(player1);
        activePlayers.add(player2);
        baseGame.setActive_players(activePlayers);
        assertEquals(activePlayers, baseGame.getActivePlayers());
    }

    @Test
    void testSetAndGetBoard() {
        Board board = new Board(24, baseGame);
        baseGame.setBoard(board);
        assertEquals(board, baseGame.getBoard());
    }


    @Test
    void testAddPlayer() {

        baseGame.addPlayer(player1);
        assertTrue(baseGame.getPlayers().contains(player1));
        assertEquals(5, baseGame.getPlayers().size());
    }




}