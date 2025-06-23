package it.polimi.ingsw;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.AbandonedStation;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.CardAdventureType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class AbandonedStationTest {

    CardAdventure abd_station ;

    @BeforeEach
    void setUp() {

        List<Cargo> cargos = new ArrayList<>();
        cargos.add(Cargo.Green);
        cargos.add(Cargo.Yellow);


        abd_station = new AbandonedStation(1,1,AbandonedStation,5,cargos,"");
    }

    @Test
    void testAbandonedStation() {
        assertEquals(((AbandonedStation)abd_station).getCargo(), Arrays.asList(Cargo.Green,Cargo.Yellow));
        assertEquals(((AbandonedStation)abd_station).getNeeded_crewmates(), 5);

    }

}
