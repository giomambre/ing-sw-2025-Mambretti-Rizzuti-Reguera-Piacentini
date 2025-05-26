package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.Planets;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.view.TUI.TUI;
import it.polimi.ingsw.view.View;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.CrewmateType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Color.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ShipTest2 {
    private Ship ship1, ship2;
    private Player player1, player2;





    @BeforeEach
    void setUp() {
        Game game = new Game(Gametype.StandardGame);
        player1 = new Player("Reff", GREEN,game);
        player2 = new Player("Mambre", YELLOW,game);
        ship1 = new Ship(player1);
        ship2 = new Ship(player2);
        ship1.initializeShipPlance();
        ship2.initializeShipPlance();


        Map<Direction, ConnectorType> connectors = new HashMap<>();
        connectors.put(North, Smooth);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);
        ship1.addComponent(new Battery(Battery, connectors,2 , ""), 0, 2 );

        connectors.put(North, Cannon_Connector);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors, ""), 1, 1);


        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Battery(Battery, connectors,2 , ""), 1, 2);


        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new CardComponent(Tubes, connectors, ""), 1, 3);


        connectors.put(North, Cannon_Connector);
        connectors.put(East, Smooth);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors, ""), 1, 4);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new Shield(Shield, connectors, ""), 1, 5);

        Map<Direction,Boolean> covered_sides = new HashMap<>();
        covered_sides.put(North, true);
        covered_sides.put(East, false);
        covered_sides.put(South, false);
        covered_sides.put(West, true);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(PinkAlienUnit, connectors, ""), 2, 0);


        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new LivingUnit(LivingUnit, connectors, ""), 2, 1);

        ((LivingUnit) ship1.getComponent(2,1)).addAlien(PinkAlien);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Single);

        ship1.addComponent(new Battery(Battery, connectors,3, ""), 2, 2);

        connectors.put(North, Single);
        connectors.put(East, Universal);
        connectors.put(South, Smooth);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(RedStorage, connectors,1, ""), 2, 4);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors, ""), 2, 5);

        connectors.put(North, Smooth);
        connectors.put(East, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(DoubleCannon, connectors, ""), 2, 6);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Smooth);
        connectors.put(West, Cannon_Connector);

        ship1.addComponent(new CardComponent(Cannon, connectors, ""), 3, 0);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Double);
        connectors.put(West, Double);

        ship1.addComponent(new Shield(Shield, connectors, ""), 3, 1);

        connectors.put(North, Smooth);
        connectors.put(East, Universal);
        connectors.put(South, Single);
        connectors.put(West, Smooth);

        ship1.addComponent(new LivingUnit(LivingUnit, connectors, ""), 3, 2);
        ((LivingUnit) ship1.getComponent(3,2)).addAstronauts();

        connectors.put(North, Universal);
        connectors.put(East, Double);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors, ""), 3, 3);

        connectors.put(North, Smooth);
        connectors.put(East, Double);
        connectors.put(South, Single);
        connectors.put(West, Universal);

        ship1.addComponent(new Storage(BlueStorage, connectors,2, ""), 3, 4);

        connectors.put(North, Universal);
        connectors.put(East, Single);
        connectors.put(South, Double);
        connectors.put(West, Universal);

        ship1.addComponent(new CardComponent(Tubes, connectors, ""), 3, 5);

        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Single);

        ship1.addComponent(new CardComponent(DoubleEngine, connectors, ""), 3, 6);

        connectors.put(North, Double);
        connectors.put(East, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors, ""), 4, 1);

        connectors.put(North, Single);
        connectors.put(East, Double);
        connectors.put(South, Universal);
        connectors.put(West, Double);

        ship1.addComponent(new Storage(BlueStorage, connectors,2, ""), 4, 2);


        connectors.put(North, Universal);
        connectors.put(East, Smooth);
        connectors.put(South, Universal);
        connectors.put(West, Smooth);

        ship1.addComponent(new Storage(RedStorage, connectors,1, ""), 4, 4);

        connectors.put(North, Double);
        connectors.put(East, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(West, Smooth);

        ship1.addComponent(new CardComponent(Engine, connectors, ""), 4, 5);
    }


@Test
    public void testExposedConnectors(){

    assertEquals(8, ship1.calculateExposedConnectors());

}

@Test public void testCheckShipConnections(){
    List<Pair<Integer, Integer>> pairs = List.of();
    assertEquals(pairs,ship1.checkShipConnections());


    Map<Direction, ConnectorType> connectors = new HashMap<>();
    connectors.put(North, Double);
    connectors.put(East, Smooth);
    connectors.put(South, Engine_Connector);
    connectors.put(West, Smooth);

    ship1.addComponent(new CardComponent(Engine, connectors ,""), 0, 4);
    pairs = List.of(new Pair<>(0, 4), new Pair<>(1, 4));
    assertEquals(pairs,ship1.checkShipConnections());

    pairs = List.of();
    ship1.removeComponent(0,4);
    assertEquals(pairs,ship1.checkShipConnections());
}

@Test
    public  void  testFindPieces(){
        ship1.removeComponent(3,4);
        ship1.removeComponent(2,4);

    //it creates 3 pieces, but only one of them is valid
    //which is [[0=2, 1=2, 1=3, 1=4, 2=3, 3=3, 3=2, 4=2, 4=1, 3=1, 2=1, 2=2, 2=0, 3=0, 1=1]]
    List<Pair<Integer, Integer>> pairs = List.of(
            new Pair<>(0, 2), new Pair<>(1, 2), new Pair<>(1, 3), new Pair<>(1, 4),
            new Pair<>(2, 3), new Pair<>(3, 3), new Pair<>(3, 2), new Pair<>(4, 2),
            new Pair<>(4, 1), new Pair<>(3, 1), new Pair<>(2, 1), new Pair<>(2, 2),
            new Pair<>(2, 0), new Pair<>(3, 0), new Pair<>(1, 1)
    );
        assertEquals(pairs, ship1.findShipPieces().getFirst());



}
@Test
    public  void  testRemoveComponentKillingAlien() {

    assertEquals(PinkAlien, ((LivingUnit) ship1.getComponent(2,1)).getCrewmate_type());
    assertEquals(1, ((LivingUnit) ship1.getComponent(2,1)).getNum_crewmates());
    ship1.removeComponent(2,0);
    assertEquals(0, ((LivingUnit) ship1.getComponent(2,1)).getNum_crewmates());
    }


@Test
    public  void  testCalculateCannonPower() {

    Map<CardComponent, Boolean> playerBatteryUsage = new HashMap<>();

    assertEquals(5,ship1.calculateCannonPower(playerBatteryUsage));

    playerBatteryUsage.put(ship1.getComponent(1, 1), true);
    playerBatteryUsage.put(ship1.getComponent(2, 6), true);
    assertEquals(6.5 ,ship1.calculateCannonPower(playerBatteryUsage));

    ship1.removeComponent(2,0);
    assertEquals(4.5,ship1.calculateCannonPower(playerBatteryUsage));

}

@Test
public void testCalculateEnginePower() {

    Map<CardComponent, Boolean> playerBatteryUsage = new HashMap<>();
    assertEquals(3, ship1.calculateEnginePower(playerBatteryUsage));

    ship1.removeComponent(2, 0);


    Map<Direction, ConnectorType> connectors = new HashMap<>();
    connectors.put(North, Smooth);
    connectors.put(East, Universal);
    connectors.put(South, Smooth);
    connectors.put(West, Single);

    ship1.addComponent(new CardComponent(BrownAlienUnit, connectors ,""), 2, 0);
    ((LivingUnit) ship1.getComponent(2, 1)).addAlien(BrownAlien);

    assertEquals(5, ship1.calculateEnginePower(playerBatteryUsage));

    playerBatteryUsage.put(ship1.getComponent(3, 6), true);
    assertEquals(6, ship1.calculateEnginePower(playerBatteryUsage));


}

@Test
public void testChoosePiece() {

    ship1.removeComponent(2, 4);
    ship1.removeComponent(2, 3);
    ship1.removeComponent(4, 2);

    ship1.choosePiece(1);
    List<Pair<Integer, Integer>> pairs = List.of(
            new Pair<>(1, 5), new Pair<>(2, 5), new Pair<>(2, 6),
            new Pair<>(3, 2), new Pair<>(3, 3), new Pair<>(3, 4),
            new Pair<>(3, 5), new Pair<>(3,6),new Pair<>(4, 4), new Pair<>(4, 5)
    );

   assertEquals(pairs, ship1.printShipPlance());


}
    public String getWeakerPlayer(){
        Map<String,Integer> ranks = new HashMap<>();
        ranks.put("isa " , 2);
        ranks.put("raffa " , 3);
        ranks.put("yellow " , 2);
        int less = 100;
        String player = "";
        for(Map.Entry<String,Integer> entry : ranks.entrySet()){
            if(entry.getValue() < less){
                less = entry.getValue();
                player = entry.getKey();
            }
        }
        return player;


    }
 @Test
    public  void  testPrintShipPlance() {
        View v= new TUI();



     v.removeCargo(ship1);



 }





}
