package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.CombatZone;
import it.polimi.ingsw.model.adventures.MeteorSwarm;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombatZoneTest {

    List<Pair<MeteorType, Direction>> meteors;
    CardAdventure combatZone;

    @BeforeEach
    void setUp() {
        meteors = List.of(
                new Pair<>(MeteorType.LightCannonFire, North),
                new Pair<>(MeteorType.LightCannonFire, West),
                new Pair<>(MeteorType.LightCannonFire, East),
                new Pair<>(MeteorType.HeavyCannonFire, South)
        );

        combatZone = new CombatZone(2, 4, CardAdventureType.CombatZone, 0, 0, 3, meteors, "");

    }


    @Test
    public void combatZoneTest() {
        assertEquals(((CombatZone)combatZone).getId(), 0);
        assertEquals(((CombatZone)combatZone).getMeteors(), meteors);
        assertEquals(((CombatZone)combatZone).getCrewmates_loss(),0);
        assertEquals(((CombatZone)combatZone).getCargo_loss(),3);

    }


}


