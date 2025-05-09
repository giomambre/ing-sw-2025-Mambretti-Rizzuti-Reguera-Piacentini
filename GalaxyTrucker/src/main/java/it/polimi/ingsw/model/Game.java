package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.util.*;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;

/**
 * This class represents the STANDARD GAME  during the various phases.
 * <ul>
 *     <li>clock_time: the time measured twisting the hourglass once</li>
 * </ul>
 */
public class Game extends BaseGame {

    private List<CardAdventure> deck_left = new ArrayList<>();
    private List<CardAdventure> deck_right = new ArrayList<>();
    private List<CardAdventure> deck_top = new ArrayList<>();
    private List<CardAdventure> deck_middle = new ArrayList<>();

    private static final int clock_time = 30; //30 sec messi a caso

    public Game(Gametype type) {
        super(type);
    }

    /**
     * This method sets up the adventure deck and board, initializes all component cards,
     * starts the assembly phase, and prepares each player's ship by initializing its board.
     * It should be called once at the beginning of the game.
     */
    public void startGame() {

        createDecksAdventureBoard();
        initializeDeckComponents();
        startAssembly();
        for (Player player : players) {

            player.getShip().initializeShipPlance();

        }


        board = new Board(24, this);

    }

    /**
     * This method is called before the assembly phase to create the 4 decks of card adventure that will be put
     * on the board.
     */
    public void createDecksAdventureBoard() {
        List<CardAdventure> deck_adventure_liv1 = new ArrayList<>();
        List<CardAdventure> deck_adventure_liv2 = new ArrayList<>();

        deck_adventure_liv1.add(new Slavers(1, 1, CardAdventureType.Slavers, 6, 3, 5));
        deck_adventure_liv1.add(new Smugglers(1, 1, CardAdventureType.Smugglers, 4,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Green,
                        Cargo.Blue
                ),
                2));
        deck_adventure_liv1.add(new Pirates(1, 1, CardAdventureType.Pirates, 5, 4,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.HeavyCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, North)
                )
        ));
        deck_adventure_liv1.add(new Stardust(1, 0, CardAdventureType.Stardust));
        deck_adventure_liv1.add(new OpenSpace(1, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv1.add(new OpenSpace(1, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv1.add(new OpenSpace(1, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv1.add(new OpenSpace(1, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv1.add(new MeteorSwarm(1, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, West)
                )
        ));
        deck_adventure_liv1.add(new MeteorSwarm(1, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, South)
                )
        ));
        deck_adventure_liv1.add(new MeteorSwarm(1, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, North)
                )
        ));
        deck_adventure_liv1.add(new Planets(1, 3, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Green, Cargo.Blue, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Yellow, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Green)
                )
        ));
        deck_adventure_liv1.add(new Planets(1, 2, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue),
                        List.of(Cargo.Yellow)
                )
        ));
        deck_adventure_liv1.add(new Planets(1, 3, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Yellow, Cargo.Green, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Yellow, Cargo.Yellow)
                )
        ));
        deck_adventure_liv1.add(new Planets(1, 1, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Green, Cargo.Green),
                        List.of(Cargo.Yellow),
                        Arrays.asList(Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv1.add(new CombatZone(1, 3, CardAdventureType.CombatZone, 2, 0, 0,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, South),
                        new Pair<>(MeteorType.HeavyCannonFire, South)
                )
        ));
        deck_adventure_liv1.add(new AbandonedShip(1, 1, CardAdventureType.AbandonedShip, 3, 2));
        deck_adventure_liv1.add(new AbandonedShip(1, 1, CardAdventureType.AbandonedShip, 4, 3));
        deck_adventure_liv1.add(new AbandonedStation(1, 1, CardAdventureType.AbandonedStation, 5,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Green
                )
        ));
        deck_adventure_liv1.add(new AbandonedStation(1, 1, CardAdventureType.AbandonedStation, 6,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Red
                )
        ));

        deck_adventure_liv2.add(new Slavers(2, 2, CardAdventureType.Slavers, 7, 4, 8));
        deck_adventure_liv2.add(new Smugglers(2, 1, CardAdventureType.Smugglers, 8,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Yellow,
                        Cargo.Yellow
                ),
                3));
        deck_adventure_liv2.add(new Pirates(2, 2, CardAdventureType.Pirates, 6, 7,
                List.of(
                        new Pair<>(MeteorType.HeavyCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.HeavyCannonFire, North)
                )
        ));
        deck_adventure_liv2.add(new Stardust(2, 0, CardAdventureType.Stardust));
        deck_adventure_liv2.add(new Epidemic(2, 0, CardAdventureType.Epidemic));
        deck_adventure_liv2.add(new OpenSpace(2, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv2.add(new OpenSpace(2, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv2.add(new OpenSpace(2, 0, CardAdventureType.OpenSpace));
        deck_adventure_liv2.add(new MeteorSwarm(2, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, West)
                )
        ));
        deck_adventure_liv2.add(new MeteorSwarm(2, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, South),
                        new Pair<>(MeteorType.SmallMeteor, South)
                )
        ));
        deck_adventure_liv2.add(new MeteorSwarm(2, 0, CardAdventureType.MeteorSwarm,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, East)
                )
        ));
        deck_adventure_liv2.add(new Planets(2, 4, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red, Cargo.Red, Cargo.Yellow),
                        Arrays.asList(Cargo.Red, Cargo.Red, Cargo.Green, Cargo.Green),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv2.add(new Planets(2, 3, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red),
                        Arrays.asList(Cargo.Green, Cargo.Green, Cargo.Green, Cargo.Green)
                )
        ));
        deck_adventure_liv2.add(new Planets(2, 2, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Yellow),
                        Arrays.asList(Cargo.Yellow, Cargo.Green, Cargo.Blue),
                        Arrays.asList(Cargo.Green, Cargo.Green),
                        List.of(Cargo.Yellow)
                )
        ));
        deck_adventure_liv2.add(new Planets(2, 3, CardAdventureType.Planets,
                Arrays.asList(
                        Arrays.asList(Cargo.Green, Cargo.Green, Cargo.Green, Cargo.Green),
                        Arrays.asList(Cargo.Yellow, Cargo.Yellow),
                        Arrays.asList(Cargo.Blue, Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv2.add(new CombatZone(2, 4, CardAdventureType.CombatZone, 0, 3, 3,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, West),
                        new Pair<>(MeteorType.LightCannonFire, East),
                        new Pair<>(MeteorType.HeavyCannonFire, South)
                )
        ));
        deck_adventure_liv2.add(new AbandonedShip(2, 1, CardAdventureType.AbandonedShip, 6, 4));
        deck_adventure_liv2.add(new AbandonedShip(2, 2, CardAdventureType.AbandonedShip, 8, 5));
        deck_adventure_liv2.add(new AbandonedStation(2, 1, CardAdventureType.AbandonedStation, 7,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Yellow
                )
        ));
        deck_adventure_liv2.add(new AbandonedStation(2, 2, CardAdventureType.AbandonedStation, 8,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Yellow,
                        Cargo.Green
                )
        ));

        Collections.shuffle(deck_adventure_liv1);
        Collections.shuffle(deck_adventure_liv2);
        this.deck_left.add(deck_adventure_liv1.removeFirst());
        this.deck_top.add(deck_adventure_liv1.removeFirst());
        this.deck_middle.add(deck_adventure_liv1.removeFirst());
        this.deck_top.add(deck_adventure_liv1.removeFirst());

        int i = 0;
        while (i < 2) {
            this.deck_left.add(deck_adventure_liv2.removeFirst());
            this.deck_top.add(deck_adventure_liv2.removeFirst());
            this.deck_middle.add(deck_adventure_liv2.removeFirst());
            this.deck_right.add(deck_adventure_liv2.removeFirst());
            i++;
        }


    }

    /**
     * called by the controller to give to each player a local copy of the decks present on the board
     *
     * @return a map having as key a direction that corresponds to a deck. (South=middle, East=Right, West=Left)
     */
    public Map<Direction, List<CardAdventure>> seeDecksOnBoard() {
        Map<Direction, List<CardAdventure>> seeDecksOnBoard = new HashMap<>();
        seeDecksOnBoard.put(South, deck_middle);
        seeDecksOnBoard.put(East, deck_right);
        seeDecksOnBoard.put(West, deck_left);

        return seeDecksOnBoard;

    }


    @Override
    public CardAdventure getRandomCardAdventure() {
        if (deck_adventure.isEmpty()) return  null; //manca da fare la gestione della fine del gioco

        CardAdventure adventure = deck_adventure.removeFirst();
        adventure.changeFace();
        return adventure;
    }



    /**
     * This method is called by the controller when the assembly phase is finished to merge all the 4 decks present on the board
     * in one (our DeckAdventure).
     */
    public void createDeckAdventure() {
        this.deck_adventure.addAll(this.deck_left);
        this.deck_adventure.addAll(this.deck_top);
        this.deck_adventure.addAll(this.deck_middle);
        this.deck_adventure.addAll(this.deck_right);

        do {
            Collections.shuffle(this.deck_adventure);
        } while (this.deck_adventure.removeFirst().getLevel() != 2);

        for (CardAdventure card_adventure : this.deck_adventure) {
            card_adventure.setBoard(this.board);
        }
    }

    /**
     * Initializes and returns the full deck of component cards used in the game.
     * The deck includes various types of {@code CardComponent}, such as batteries, storage units,
     * engines, cannons, alien units, shields, and main units, each with predefined connector configurations.
     * After all components are created, the deck is shuffled.
     *
     * @return a shuffled list of {@code CardComponent} objects representing the component deck
     */
    public List<CardComponent> initializeDeckComponents() {


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);

        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_1.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_2.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_3.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_4.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_5.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_6.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_7.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_8.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_9.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_10.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2, "images/cardComponent/GT-battery_2_11.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_1.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_2.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_3.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_4.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_5.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 3, "images/cardComponent/GT-battery_3_6.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_1.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_2.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_3.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_4.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_5.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_6.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_7.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_8.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2, "images/cardComponent/GT-blueStorage_2_9.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_1.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_2.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_3.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_4.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_5.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3, "images/cardComponent/GT-blueStorage_3_6.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_1.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_2.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_3.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_4.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_5.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1, "images/cardComponent/GT-redStorage_1_6.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2, "images/cardComponent/GT-redStorage_2_1.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2, "images/cardComponent/GT-redStorage_2_2.jpg"));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(RedStorage, connectors, 2, "images/cardComponent/GT-redStorage_2_3.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_1.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_2.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_3.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_4.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_5.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_6.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_7.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_8.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_9.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_10.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_11.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_12.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_13.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_14.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_15.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_16.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors, "images/cardComponent/GT-livingUnit_17.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_1.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_2.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_3.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_4.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_5.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_6.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_7.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors, "images/cardComponent/GT-tubes_8.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_1.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_2.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_3.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_4.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_5.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_6.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_7.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_8.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_9.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_10.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_11.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_12.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_13.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_14.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_15.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_16.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_17.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_18.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_19.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_20.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors, "images/cardComponent/GT-engine_21.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_1.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_2.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_3.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_4.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_5.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_6.jpg"));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_7.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_8.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors, "images/cardComponent/GT-doubleEngine_9.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_1.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_2.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_3.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_4.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_5.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_6.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_7.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_8.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_9.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_10.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_11.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_12.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_13.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_14.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_15.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_16.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_17.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_18.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_19.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_20.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_21.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_22.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_23.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_24.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors, "images/cardComponent/GT-cannon_25.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_1.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_2.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_3.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_4.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_5.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_6.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_7.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_8.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_9.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_10.jpg"));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors, "images/cardComponent/GT-doubleCannon_11.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_1.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_2.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_3.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_4.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_5.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors, "images/cardComponent/brownAlienUnit_6.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_1.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_2.jpg"));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_3.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_4.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_5.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors, "images/cardComponent/pinkAlienUnit_6.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_1.jpg")); //di defaul rivolti nord-est (da capire se fare sottoclasse)


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_2.jpg"));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_3.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_4.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_5.jpg"));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_6.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_7.jpg"));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors, "images/cardComponent/shield_8.jpg"));


        //to add : adventure deck creation and allocation

        Collections.shuffle(deck_components);

        return deck_components;
    }

    /**
     * this method is called to start the flight phase putting al the rockets on the board.
     * Before starting the group leader creates the deck adventure merging the decks already present on the board
     */
    public void startFlight() {
        createDeckAdventure();
        board.putPlayersOnBoard(active_players);
    }

    public List<CardAdventure> getDeck_left() {
        return deck_left;
    }

    public List<CardAdventure> getDeck_right() {
        return deck_right;
    }

    public List<CardAdventure> getDeck_middle() {
        return deck_middle;
    }
}