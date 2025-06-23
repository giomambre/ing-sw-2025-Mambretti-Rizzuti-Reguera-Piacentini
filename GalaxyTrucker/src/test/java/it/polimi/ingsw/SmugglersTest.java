package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Smugglers;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Cargo.Green;
import static it.polimi.ingsw.model.enumerates.Color.YELLOW;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Smooth;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SmugglersTest {
    CardAdventure smugglers;
    List<Cargo> cargo_reward;


    @BeforeEach
    public void setup() {
        cargo_reward = new ArrayList<>(List.of(Blue, Green));
        smugglers = new Smugglers(2, 2, CardAdventureType.Smugglers, 3, cargo_reward, 2, "");
    }

    @Test
    public void testSmugglers() {
        assertEquals(((Smugglers) smugglers).getCannons_strenght(), 3);
        assertEquals(((Smugglers) smugglers).getCargo_loss(), 2);
        assertEquals(((Smugglers)smugglers).getCargo_rewards(), cargo_reward);
    }

}
