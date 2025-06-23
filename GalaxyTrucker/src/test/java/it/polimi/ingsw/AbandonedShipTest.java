package it.polimi.ingsw;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.AbandonedShip;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.CardAdventureType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class AbandonedShipTest {
    private CardAdventure abandonedShip;


    @BeforeEach
    void setUp() {
        abandonedShip = new AbandonedShip(2,5,AbandonedShip,5,3,"");


    }

    @Test

    public void testAbandonedShip() {

        assertEquals(((AbandonedShip)abandonedShip).getCrewmates_loss(),3);
        assertEquals(((AbandonedShip)abandonedShip).getGiven_credits(),5);

    }

}
