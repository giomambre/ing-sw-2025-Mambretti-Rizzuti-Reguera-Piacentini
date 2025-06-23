package it.polimi.ingsw;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Stardust;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardAdventureTest {
    CardAdventure adventure;

    @BeforeEach
    public void setUp() {
        adventure = new Stardust(1,2, CardAdventureType.Stardust, "");
    }

    @Test
    public void testAdventure() {
        assertEquals(adventure.getCost_of_days(), 2);
        assertEquals(adventure.getLevel(), 1);
        assertEquals(adventure.getType(), CardAdventureType.Stardust);
        assertEquals(adventure.getImagePath(), "");
    }
}
