package it.polimi.ingsw;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;
import static it.polimi.ingsw.model.CardAdventureType.*;
import static it.polimi.ingsw.model.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class AbandonedStationTest {

    Player player1, player2, player3;

    @BeforeEach
    void setUp() {
        player1 = new Player("Alice", Yellow);
        player2 = new Player("Mambre", Blue);
        player3 = new Player("isabel", Red);
    }


}
