package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Cargo.Green;
import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.Color.YELLOW;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PiratesTest {
    CardAdventure pirates;
    List<Pair<MeteorType, Direction>> meteors;

    @BeforeEach
    public void setup() {
        meteors = List.of(
                new Pair(MeteorType.LightCannonFire, West),
                new Pair<>(MeteorType.HeavyCannonFire, South), new Pair<>(MeteorType.HeavyCannonFire, North)
        );

        pirates = new Pirates(2,2,CardAdventureType.Pirates, 3,3, meteors,"");


    }

    @Test
    public void testPirates() {
        assertEquals(((Pirates) pirates).getMeteors(), meteors);
        assertEquals(((Pirates) pirates).getCannons_strenght(), 3);
        assertEquals(((Pirates) pirates).getCredits(), 3);
    }
}
