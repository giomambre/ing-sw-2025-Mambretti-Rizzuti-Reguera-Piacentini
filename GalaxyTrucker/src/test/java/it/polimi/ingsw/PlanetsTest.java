package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Planets;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.messages.StandardMessageClient;
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

public class PlanetsTest {
    CardAdventure planets;
    List<List<Cargo>> cargo_reward;

    @BeforeEach
    public void setup() {
        cargo_reward = new ArrayList<>();
        cargo_reward.add(new ArrayList<>(List.of(Blue, Green)));
        cargo_reward.add(new ArrayList<>(List.of(Blue, Green)));

        planets = new Planets(2,2, CardAdventureType.Planets, cargo_reward ,"");

    }

    @Test
    public void planetsTest() {
        assertEquals(((Planets)planets).getCargo_reward(), cargo_reward);
        assertEquals(((Planets)planets).getCargos(0), cargo_reward.get(0));
        assertEquals(((Planets)planets).getCargos(1), cargo_reward.get(1));
    }
}
