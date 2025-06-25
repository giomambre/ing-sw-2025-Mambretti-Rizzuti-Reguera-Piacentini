package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.QuickGame;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuickGameTest {

    private QuickGame quickGame;
    private Player p;

    @BeforeEach
    void setUp() {
        quickGame = new QuickGame(Gametype.QuickGame);

    }

    @Test
    void testQuickGameConstructor() {
        assertNotNull(quickGame);
        assertEquals(Gametype.QuickGame, quickGame.getType());
        quickGame.startGame();
        quickGame.addPlayer(p);
        assertFalse(quickGame.getPlayers().isEmpty());
    }

    @Test
    void testRandomCard(){
        quickGame.createDeckAdventure();
        quickGame.getRandomCardAdventure();
        quickGame.initializeDeckComponents();

        assertEquals(Gametype.QuickGame, quickGame.getType());
    }


}