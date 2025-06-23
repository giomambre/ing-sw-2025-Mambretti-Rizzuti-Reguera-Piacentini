package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Slavers;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SlaversTest {
    CardAdventure slavers;


    @BeforeEach
    public void setup() {
        slavers = new Slavers(2,2,CardAdventureType.Slavers, 3,3, 9,"");
    }

    @Test
    public void testSlavers() {
        assertEquals(((Slavers)slavers).getCannons_strenght(), 3);
        assertEquals(((Slavers)slavers).getAstronaut_loss(), 3);
        assertEquals(((Slavers)slavers).getCredits(), 9);
    }
}
