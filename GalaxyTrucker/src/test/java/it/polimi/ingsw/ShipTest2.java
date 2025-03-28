package it.polimi.ingsw;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.util.Pair;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;


public class ShipTest2 {
    private Ship ship1, ship2;
    private Player player1, player2;





    @BeforeEach
    void setUp() {
        player1 = new Player("Reff", Green);
        player2 = new Player("Mambre", Yellow);
        ship1 = new Ship(player1);
        ship2 = new Ship(player2);
        ship1.initializeShipPlance();
        ship2.initializeShipPlance();


        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);
        ship1.addComponent(new Battery(Battery, connectors,2), 0, 2);

        connectors.put(North, Cannon_Connector);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors), 1, 1);


        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Battery(Battery, connectors,2), 1, 2);


        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new CardComponent(Tubes, connectors), 1, 3);


        connectors.put(North, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleEngine, connectors), 1, 4);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new Shield(Shield, connectors), 1, 5);

        Map<Direction,Boolean> covered_sides = new HashMap<>();
        covered_sides.put(North, true);
        covered_sides.put(East, false);
        covered_sides.put(South, false);
        covered_sides.put(West, true);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(PinkAlienUnit, connectors), 2, 0);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new LivingUnit(LivingUnit, connectors), 2, 1);


        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new Battery(Battery, connectors,3), 2, 2);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(RedStorage, connectors,1), 2, 4);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors), 2, 5);

        connectors.put(North, Smooth);
        connectors.put(East, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors), 2, 6);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Cannon_Connector);

        ship1.addComponent(new CardComponent(Cannon, connectors), 3, 0);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors), 3, 1);

        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new LivingUnit(LivingUnit, connectors), 3, 2);

        connectors.put(North, Universal);
        connectors.put(East, Double);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors), 3, 3);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new Storage(BlueStorage, connectors,2), 3, 4);

        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors), 3, 5);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(Engine, connectors), 3, 6);

        connectors.put(North, Double);
        connectors.put(East, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors), 4, 1);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(BlueStorage, connectors,2), 4, 2);


        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);

        ship1.addComponent(new Storage(RedStorage, connectors,1), 4, 4);

        connectors.put(North, Double);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors), 4, 5);
    }


@Test
    public void testExposedConnectors(){
    System.out.println( ship1.calculateExposedConnectors());
    Assert.assertEquals(0, ship1.checkShipConnections().size());

}

@Test
    public  void  testFindPieces(){
        ship1.removeComponent(3,4);
        ship1.removeComponent(2,4);
    System.out.println(ship1.findShipPieces()); //ritorna solo i tronconi validi (con motori e astronauti)



}




}
