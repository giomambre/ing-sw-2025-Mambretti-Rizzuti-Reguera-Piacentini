package it.polimi.ingsw;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.MeteorSwarm;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.Color.YELLOW;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeteorSwarmTest {
    Player player1;
    Ship ship1;
    List<Pair<MeteorType, Direction>> meteors;
    CardAdventure meteorSwarm;
    Board board;

@BeforeEach
void setUp() {
    meteors = List.of(
            new Pair(MeteorType.SmallMeteor, West),
            new Pair<>(MeteorType.LargeMeteor, South), new Pair<>(MeteorType.LargeMeteor, North)
    );
    meteorSwarm = new MeteorSwarm(2,0, CardAdventureType.MeteorSwarm, meteors,"");
}


@Test
    public void meteorSwarmTest(){
        assertEquals(((MeteorSwarm)meteorSwarm).getMeteors(), meteors);
    }
}
