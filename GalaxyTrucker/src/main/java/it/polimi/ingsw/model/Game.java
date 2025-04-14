package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
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
public class Game extends BaseGame{

    private List<CardAdventure> deck_adventure_liv1 = new ArrayList<>();
    private List<CardAdventure> deck_adventure_liv2 = new ArrayList<>();

    private static final int clock_time = 30; //30 sec messi a caso

    public Game(Gametype type) {
        super(type);
        initializeDeckComponents();
        createDeckAdventure();
    }

    @Override
    public void createDeckAdventure() {
        List<CardAdventure> deck_adventure_liv1 = new ArrayList<>();
        List<CardAdventure> deck_adventure_liv2 = new ArrayList<>();

        deck_adventure_liv1.add(new Slavers(1,1, CardAdventureType.Slavers,board,6,3,5));
        deck_adventure_liv1.add(new Smugglers(1, 1,CardAdventureType.Smugglers,board,4,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Green,
                        Cargo.Blue
                ),
                2));
        deck_adventure_liv1.add(new Pirates(1,1,CardAdventureType.Pirates,board,5,4,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.HeavyCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, North)
                )
        ));
        deck_adventure_liv1.add(new Stardust(1,0,CardAdventureType.Stardust,board));
        deck_adventure_liv1.add(new OpenSpace(1,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv1.add(new OpenSpace(1,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv1.add(new OpenSpace(1,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv1.add(new OpenSpace(1,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv1.add(new MeteorSwarm(1,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, West)
                )
        ));
        deck_adventure_liv1.add(new MeteorSwarm(1,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, South)
                )
        ));
        deck_adventure_liv1.add(new MeteorSwarm(1,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, North)
                )
        ));
        deck_adventure_liv1.add(new Planets(1,3,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Green, Cargo.Blue, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Yellow, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Red, Cargo.Green)
                )
        ));
        deck_adventure_liv1.add(new Planets(1,2,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue),
                        List.of(Cargo.Yellow)
                )
        ));
        deck_adventure_liv1.add(new Planets(1,3,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Yellow, Cargo.Green, Cargo.Blue, Cargo.Blue),
                        Arrays.asList(Cargo.Yellow, Cargo.Yellow)
                )
        ));
        deck_adventure_liv1.add(new Planets(1,1,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Green, Cargo.Green),
                        List.of(Cargo.Yellow),
                        Arrays.asList(Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv1.add(new CombatZone(1,3,CardAdventureType.CombatZone,board,2,0,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, South),
                        new Pair<>(MeteorType.HeavyCannonFire, South)
                )
        ));
        deck_adventure_liv1.add(new AbandonedShip(1,1,CardAdventureType.AbandonedShip,board,3,2));
        deck_adventure_liv1.add(new AbandonedShip(1,1,CardAdventureType.AbandonedShip,board,4,3));
        deck_adventure_liv1.add(new AbandonedStation(1,1,CardAdventureType.AbandonedStation,5,board,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Green
                )
        ));
        deck_adventure_liv1.add(new AbandonedStation(1,1,CardAdventureType.AbandonedStation,6,board,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Red
                )
        ));

        deck_adventure_liv2.add(new Slavers(2,2, CardAdventureType.Slavers,board,7,4,8));
        deck_adventure_liv2.add(new Smugglers(2, 1,CardAdventureType.Smugglers,board,8,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Yellow,
                        Cargo.Yellow
                ),
                3));
        deck_adventure_liv2.add(new Pirates(2,2,CardAdventureType.Pirates,board,6,7,
                List.of(
                        new Pair<>(MeteorType.HeavyCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.HeavyCannonFire, North)
                )
        ));
        deck_adventure_liv2.add(new Stardust(2,0,CardAdventureType.Stardust,board));
        deck_adventure_liv2.add(new Epidemic(2,0,CardAdventureType.Epidemic,board));
        deck_adventure_liv2.add(new OpenSpace(2,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv2.add(new OpenSpace(2,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv2.add(new OpenSpace(2,0,CardAdventureType.OpenSpace,board));
        deck_adventure_liv2.add(new MeteorSwarm(2,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, West),
                        new Pair<>(MeteorType.SmallMeteor, West)
                )
        ));
        deck_adventure_liv2.add(new MeteorSwarm(2,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, South),
                        new Pair<>(MeteorType.SmallMeteor, South)
                )
        ));
        deck_adventure_liv2.add(new MeteorSwarm(2,0,CardAdventureType.MeteorSwarm,board,
                List.of(
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.SmallMeteor, North),
                        new Pair<>(MeteorType.LargeMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, East),
                        new Pair<>(MeteorType.SmallMeteor, East)
                )
        ));
        deck_adventure_liv2.add(new Planets(2,4,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red, Cargo.Red, Cargo.Yellow),
                        Arrays.asList(Cargo.Red, Cargo.Red, Cargo.Green, Cargo.Green),
                        Arrays.asList(Cargo.Red, Cargo.Blue, Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv2.add(new Planets(2,3,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Red),
                        Arrays.asList(Cargo.Green, Cargo.Green, Cargo.Green, Cargo.Green)
                )
        ));
        deck_adventure_liv2.add(new Planets(2,2,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Red, Cargo.Yellow),
                        Arrays.asList(Cargo.Yellow, Cargo.Green, Cargo.Blue),
                        Arrays.asList(Cargo.Green, Cargo.Green),
                        List.of(Cargo.Yellow)
                )
        ));
        deck_adventure_liv2.add(new Planets(2,3,CardAdventureType.Planets,board,
                Arrays.asList(
                        Arrays.asList(Cargo.Green, Cargo.Green, Cargo.Green, Cargo.Green),
                        Arrays.asList(Cargo.Yellow, Cargo.Yellow),
                        Arrays.asList(Cargo.Blue, Cargo.Blue, Cargo.Blue, Cargo.Blue)
                )
        ));
        deck_adventure_liv2.add(new CombatZone(2,4,CardAdventureType.CombatZone,board,0,3,
                List.of(
                        new Pair<>(MeteorType.LightCannonFire, North),
                        new Pair<>(MeteorType.LightCannonFire, West),
                        new Pair<>(MeteorType.LightCannonFire, East),
                        new Pair<>(MeteorType.HeavyCannonFire, South)
                )
        ));
        deck_adventure_liv2.add(new AbandonedShip(2,1,CardAdventureType.AbandonedShip,board,6,4));
        deck_adventure_liv2.add(new AbandonedShip(2,2,CardAdventureType.AbandonedShip,board,8,5));
        deck_adventure_liv2.add(new AbandonedStation(2,1,CardAdventureType.AbandonedStation,7,board,
                Arrays.asList(
                        Cargo.Red,
                        Cargo.Yellow
                )
        ));
        deck_adventure_liv2.add(new AbandonedStation(2,2,CardAdventureType.AbandonedStation,8,board,
                Arrays.asList(
                        Cargo.Yellow,
                        Cargo.Yellow,
                        Cargo.Green
                )
        ));

        Collections.shuffle(deck_adventure_liv1);
        Collections.shuffle(deck_adventure_liv2);
        this.deck_adventure_liv1 = deck_adventure_liv1;
        this.deck_adventure_liv2 = deck_adventure_liv2;
        return;
    }

    /**
     * This method is called at the end of the game to give to each player its rewards.Based on:
     * <li>Order of arrival(bonus)</li>
     * <li>delivered cargos(bonus)</li>
     * <li>best ship(bonus)</li>
     * <li>extra_components(malus)</li>
     */
    /*public void setRewards() {
        //gives credits based on arrival order and carried cargos (only for players that ended the game)
        for (Player p : active_players) {
            if (p.equals(board.getRanking().get(0))) {
                p.receiveCredits(4);
            }
            if (p.equals(board.getRanking().get(1))) {
                p.receiveCredits(3);
            }
            if (p.equals(board.getRanking().get(2))) {
                p.receiveCredits(2);
            }
            if (p.equals(board.getRanking().get(3))) {
                p.receiveCredits(1);
            }

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 7; col++) {
                    if (p.getShip().getComponent(row, col).getComponentType().equals(BlueStorage)||p.getShip().getComponent(row, col).getComponentType().equals(RedStorage)){
                        Storage storage=(Storage) p.getShip().getComponent(row, col);
                        List<Cargo> cargos= storage.getCarried_cargos();
                        for (Cargo c : cargos){
                            switch (c){
                                case Red:

                                    p.receiveCredits(4);
                                    break;

                                case Yellow:

                                    p.receiveCredits(3);
                                    break;

                                case Green:
                                    p.receiveCredits(2);
                                    break;

                                case Blue:
                                    p.receiveCredits(1);
                                    break;

                                default:
                                    break;

                            }
                        }
                    }
                }

            }
        }
        //gives credits to the players who have the best ship (only for players that ended the game)
            List<Player> best_ships = new ArrayList<>();
            best_ships.add(active_players.get(0));
            for (Player p : active_players) {
                if (p.getExposed_connectors() < best_ships.get(0).getExposed_connectors()) {
                    best_ships.clear();
                    best_ships.add(p);
                }
                if (p.getExposed_connectors() == best_ships.get(0).getExposed_connectors()) {
                    best_ships.add(p);
                }
            }
            for (Player p : best_ships) {
                p.receiveCredits(2);
            }

            //remove credits due to extra components on the plance (secured but not used and/or eliminated)
            for (Player p : players) {
                p.LostCredits(p.getShip().getExtra_components().size());
            }


            //gives the reward for the cargo delivered by the players who gave up
            for (Player p : players) {
                if(!active_players.contains(p)){
                    int reward_cargo=0;
                    for (int row = 0; row < 5; row++) {
                        for (int col = 0; col < 7; col++) {
                            if (p.getShip().getComponent(row, col).getComponentType().equals(BlueStorage)||p.getShip().getComponent(row, col).getComponentType().equals(RedStorage)){
                                Storage storage=(Storage) p.getShip().getComponent(row, col);
                                List<Cargo> cargos= storage.getCarried_cargos();
                                for (Cargo c : cargos){
                                    switch (c){
                                        case Red:

                                            reward_cargo+=4;
                                            break;

                                        case Yellow:

                                            reward_cargo+=3;
                                            break;

                                        case Green:
                                            reward_cargo+=2;
                                            break;

                                        case Blue:

                                            reward_cargo+=1;
                                            break;

                                        default:
                                            break;

                                    }
                                }
                            }
                        }

                    }
                    p.receiveCredits((reward_cargo+1)/2);
                }
            }
    }*/


    /**
     * Gives the final ranking of the players based on the criteria of SetRewards method.
     * @param players
     */
    /*public void setRanking(List<Player> players) {
        players.sort(Comparator.comparingInt(Player::getCredits).reversed());
        this.ranking=players;
    }*/

    /**
     *this method is used to initialise the deck of CardComponents before building the ship
     * @param deck_components the entire list of CardComponent used to build the ships
     */
    /*public void setDeck_components(List<CardComponent> deck_components) {
        this.deck_components = deck_components;
    }*/

    /**
     *
     * @return a list containing the deck of CardCardComponents
     */
    /*public List<CardComponent> getDeck_components() {
        return deck_components;
    }*/


    /**
     * this method adds every Card Component existent to the deck of card components and then shuffles it
     */

    public List<CardComponent> initializeDeckComponents() {


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);

        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Battery(Battery, connectors, 2));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Battery(Battery, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(BlueStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Double);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(BlueStorage, connectors, 3));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new Storage(RedStorage, connectors, 1));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new Storage(RedStorage, connectors, 2));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new Storage(RedStorage, connectors, 2));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitBlue, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitGreen, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitRed, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Universal);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(MainUnitYellow, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(LivingUnit, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Single);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Tubes, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Engine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Single);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Universal);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Universal);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Double);
        connectors.put(South, Engine_Connector);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Cannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Universal);
        connectors.put(West, Smooth);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Cannon_Connector);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(BrownAlienUnit, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));

        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Smooth);
        connectors.put(East, Single);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Double);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(PinkAlienUnit, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors)); //di defaul rivolti nord-est (da capire se fare sottoclasse)


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Double);
        connectors.put(South, Double);
        connectors.put(East, Single);
        connectors.put(West, Single);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Universal);
        connectors.put(East, Smooth);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Single);
        connectors.put(South, Single);
        connectors.put(East, Double);
        connectors.put(West, Double);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Single);
        connectors.put(East, Smooth);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors));


        connectors.put(North, Smooth);
        connectors.put(South, Double);
        connectors.put(East, Double);
        connectors.put(West, Universal);

        deck_components.add(new CardComponent(Shield, connectors));


        //to add : adventure deck creation and allocation

        Collections.shuffle(deck_components);

return deck_components;
    }

    /**
     * this method is called to start the assembly phase
     */
    /*public void startAssembly() {
        active_players.addAll(players);
    }*/

    /**
     * this method is called to start the flight phase putting al the rockets on the board
     */
    public void startFlight() {
        board = new Board(active_players,24,this);
    }

    /**
     * @return return the list of players who are still competing
     */
    /*public List<Player> getActivePlayers() {
        return active_players;
    }*/

    /**
     * this method is called at the beginning of the game to add all the participants to the list of active_players
     * @param players the list of participants in the game
     */
    /*public void setActivePlayers(List<Player> players) {
        this.active_players = players;
    }*/

    /**
     * this method is called when a player join the game
     * @param player the participant
     */
    /*public void addPlayer(Player player) {
        players.add(player);
    }*/


    /**
     * @return return a random card component from the deck that is faced up
     * @see CardComponent as 'random card component' we mean a card component whose face_down==true
     */
    /*public CardComponent getRandomCardComponent() {
        CardComponent card_drawn= deck_components.removeFirst(); //picks the first card of the list (shuffled )
        return card_drawn;
    }*/

    /**
     * This method is called when the player wants to pick a card from the faced up cards
     * @return  the card of Card selected
     */
    /*public CardComponent getFacedUpCard(int index) {
        return cards_faced_up.remove(index);
    }*/

    /**
     * this method is called by the leader to draw the next card adventure
     * @return it returns the first card adventure of the deck, if we already solved all the adventures it returns null
     */
    //secondo me potrebbe avere senso una exception
    public CardAdventure getRandomCardAdventure() {
        CardAdventure adventure = deck_adventure.removeFirst();
        if (deck_adventure.isEmpty()) System.out.println("GIOCO FINITO"); //manca da fare la gestione della fine del gioco
        return adventure;

    }

    /**
     * this method is called when a player loose or choose to give up
     * @param player
     */
    /*public void removePlayer(Player player) {
        active_players.remove(player);
    }*/

    /**
     * This method is used to find a player
     * @return player called 'nickname'
     */
   /* public Player getPlayer(String nickname){


        for (Player p : players) {

            if (p.getNickname().equals(nickname)) {
                return p;
            }

        }
throw new IllegalArgumentException("Nickname not found");

    }*/


    /**
     * This method
     * @return list of all players
     */
    /*public List<Player> getPlayers() {
        return players;
    }*/

    /**
     * This method
     * @return number of players
     */
    /*public int getNumPlayers() {
        return numPlayers;
    }*/

    /**
     * @return list of adventure cards
     */
   /* public List<CardAdventure> getDeck_adventure() {
        return deck_adventure;
    }*/

    /**
     * This method
     * @return list of active players, that is, those who have not left the game
     */
    /*public List<Player> getActive_players() {
        return active_players;
    }*/

    /**
     * This method
     * @return list of player's nicknames
     */
    /*public List<String> getNicknames() {
        List<String> nicknames = new ArrayList<>();
        for (Player player : players) {
            nicknames.add(player.getNickname());
        }
        return nicknames;
    }*/

    /*public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }*/

    /*public void setDeck_adventure(List<CardAdventure> deck_adventure) {
        this.deck_adventure = deck_adventure;
    }*/

    /*public void setActive_players(List<Player> active_players) {
        this.active_players = active_players;
    }*/



   /* public List<CardComponent> getCards_faced_up() {
        return cards_faced_up;
    }

    public void setCards_faced_up(List<CardComponent> cards_faced_up) {
        this.cards_faced_up = cards_faced_up;
    }*/

}