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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Cargo.Blue;
import static it.polimi.ingsw.model.enumerates.Color.GREEN;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.CrewmateType.PinkAlien;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombatZoneTest {
        Player player1;
        Ship ship1;
        List<Pair<MeteorType, Direction>> meteors;
        CardAdventure meteorSwarm, combatZone;
        Board board;

        @BeforeEach
        void setUp() {
            Game game = new Game(Gametype.StandardGame);
            player1 = new Player("Reff", GREEN,game);
            ship1 = player1.getShip();
            board = new Board(24,game);
            board.putPlayersOnBoard(Arrays.asList (player1));
            ship1.initializeShipPlance();


            Map<Direction, ConnectorType> connectors = new HashMap<>();
            connectors.put(North, Smooth);
            connectors.put(East, Smooth);
            connectors.put(South, Universal);
            connectors.put(West, Smooth);
            ship1.addComponent(new Battery(ComponentType.Battery, connectors,2), 0, 2);

            connectors.put(North, Cannon_Connector);
            connectors.put(East, Universal);
            connectors.put(South, Smooth);
            connectors.put(West, Smooth);

            ship1.addComponent(new CardComponent(DoubleCannon, connectors), 1, 1);


            connectors.put(North, Single);
            connectors.put(East, Double);
            connectors.put(South, Single);
            connectors.put(West, Double);

            ship1.addComponent(new Battery(ComponentType.Battery, connectors,2), 1, 2);


            connectors.put(North, Universal);
            connectors.put(East, Single);
            connectors.put(South, Universal);
            connectors.put(West, Double);

            ship1.addComponent(new CardComponent(Tubes, connectors), 1, 3);


            connectors.put(North, Cannon_Connector);
            connectors.put(East, Smooth);
            connectors.put(South, Single);
            connectors.put(West, Universal);

            ship1.addComponent(new CardComponent(DoubleCannon, connectors), 1, 4);

            connectors.put(North, Single);
            connectors.put(East, Universal);
            connectors.put(South, Single);
            connectors.put(West, Smooth);

            ship1.addComponent(new Shield(ComponentType.Shield, connectors), 1, 5);

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

            ship1.addComponent(new LivingUnit(ComponentType.LivingUnit, connectors), 2, 1);

            ((LivingUnit) ship1.getComponent(2,1)).addAlien(PinkAlien);

            connectors.put(North, Single);
            connectors.put(East, Double);
            connectors.put(South, Smooth);
            connectors.put(West, Single);

            ship1.addComponent(new Battery(ComponentType.Battery, connectors,3), 2, 2);

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
            ((LivingUnit) ship1.getComponent(3,2)).addAstronauts();

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

            ship1.addComponent(new CardComponent(DoubleEngine, connectors), 3, 6);

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
        public void combatZoneTest(){


            meteors = List.of(
                    new Pair(MeteorType.LightCannonFire, West),
                    new Pair<>(MeteorType.HeavyCannonFire, South), new Pair<>(MeteorType.HeavyCannonFire, North)
            );

            combatZone = new CombatZone(2,2, CardAdventureType.CombatZone,2,0,3,meteors);
            meteors = ((CombatZone)combatZone).getMeteors();

            meteorSwarm = new MeteorSwarm(2,0, CardAdventureType.MeteorSwarm, meteors);
            meteorSwarm.setBoard(board);
            System.out.println(ship1.printShipPlance());

            for (Pair<MeteorType, Direction> pair : meteors) {
                ((MeteorSwarm)meteorSwarm).execute(player1, pair.getValue(), pair.getKey(), true, ship1.getComponent(0,2) , 8, true);
            }

            System.out.println(ship1.printShipPlance());

            ((CombatZone)combatZone).executeLessCrewmates1(player1);
            assertEquals(board.getBoard().get(5),player1);


            Map<CardComponent,Integer> astronaut_losses = new HashMap<>();
            astronaut_losses.put(ship1.getComponent(3,2),1);


            ((CombatZone)combatZone).executeLessEnginePower1(player1, astronaut_losses);
            assertEquals(1, ((LivingUnit)ship1.getComponent(3,2)).getNum_crewmates());


            Map<CardComponent, Map<Cargo, Integer>> cargo_loss = new HashMap<>();

            Map<Cargo, Integer> cargoMap= new HashMap<>();
            cargoMap.put(Blue, 0);
            cargo_loss.put(ship1.getComponent(4,2), cargoMap);
            ((Storage)ship1.getComponent(4,2)).addCargo(cargoMap);

            ((CombatZone)combatZone).executeLessEnginePower2(player1, cargo_loss);
            assertEquals(0, ((Storage)ship1.getComponent(4,2)).getCargoCount());




        }



    }


