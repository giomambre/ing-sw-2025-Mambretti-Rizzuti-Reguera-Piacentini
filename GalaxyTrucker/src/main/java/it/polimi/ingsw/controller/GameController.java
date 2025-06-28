package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.Client;
import javafx.util.Pair;

import java.util.*;

import static it.polimi.ingsw.controller.GameState.BUILD_PHASE;
import static it.polimi.ingsw.controller.GameState.SETTINGS;

public class GameController {


    private CardAdventure adventure;
    private int adv_index = 0;
    String pirates_coords = "";
    String planets = "";
    List<Player> adventureOrder = new ArrayList<>();
    List<Player> finished_supply_players = new ArrayList<>();
    List<String> waiting_fly_players = new ArrayList<>();
    List<String> disconnected_players = new ArrayList<>();
    List<Color> available_colors = new ArrayList<>();
    List<Player> build_order_players = new ArrayList<>();
    List<Player> adv_done = new ArrayList<>();
    Map<String, Double> list_engine_power = new HashMap<>();
    Map<String, Double> list_cannon_power = new HashMap<>();
    private  Random random = new Random();
    String curr_combatzone = "";
    int in_pause = 0;
    Lobby lobby;
    GameState game_state;
    BaseGame game;
    int adventureOrderSize = 0;
    private String curr_adventure_player = "";

    public List<String> getDisconnected_players() {
        return disconnected_players;
    }

    public String getCurr_adventure_player() {
        return curr_adventure_player;
    }


    public String getCurr_combatzone() {
        return curr_combatzone;
    }

    public void setCurr_combatzone(String curr_combatzone) {
        this.curr_combatzone = curr_combatzone;
    }

    public void setCurr_adventure_player(String curr_adventure_player) {
        this.curr_adventure_player = curr_adventure_player;
    }


    public GameController(Lobby lobby) {
        this.lobby = lobby;
        game = new Game(Gametype.StandardGame);
        available_colors.add(Color.RED);
        available_colors.add(Color.GREEN);
        available_colors.add(Color.YELLOW);
        available_colors.add(Color.BLUE);

        this.game_state = SETTINGS;


    }

    public void setIn_pause(int in_pause) {
        this.in_pause = in_pause;
    }

    /**
     * Remove the Player from the disconnected Player List
     * @param nickname
     */

    public void reConnect(String nickname) {
        disconnected_players.remove(nickname);
    }

    /**
     * Adde the Player to the disconnected Player List
     * @param nickname
     */

    public void disconnect(String nickname) {
        disconnected_players.add(nickname);
    }

    public String getPirates_coords() {
        return pirates_coords;
    }

    public void setPirates_coords(String pirates_coords) {
        this.pirates_coords = pirates_coords;
    }

    public CardAdventure getCurrentAdventure() {

        return adventure;

    }




    public void removeFromActivePlayers(String nickname) {
        Player player = game.getPlayer(nickname);

        game.removePlayer(player);

    }


    /**
     * Add the Cannon declared of the Player to the List, used for some adventures
     * @param name
     * @param value
     */
    public void addCannonValue(String name, double value) {

        list_cannon_power.put(name, value);
    }

    /**
     * Add the Engine declared of the Player to the List, used for some adventures
     * @param name
     * @param value
     */

    public void addEngineValue(String name, Double value) {

        list_engine_power.put(name, value);
    }


    public Map<String, Double> getListCannonPower() {
        return list_cannon_power;
    }


    /**
     * It calculates the Player with the least Cannon Power
     * @return The player Nickname
     */
    public String getLeastCannon() {

        String result = "";
        Double min = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : list_cannon_power.entrySet()) {
            if (entry.getValue() < min) {
                min = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;

    }

    /**
     * It calculates the Player with the least Engine Power
     * @return The player Nickname
     */

    public String getLeastEngineValue() {
        String result = "";
        Double min = Double.MAX_VALUE;
        for (Map.Entry<String, Double> entry : list_engine_power.entrySet()) {
            if (entry.getValue() < min) {
                min = entry.getValue();
                result = entry.getKey();
            }
        }
        return result;
    }

    public Map<String, Double> getEngineValues() {
        return list_engine_power;
    }

    /**
     * Removes the Player from the adventure, if the adventureOrder List contains him that means that the player has not done the adventure
     * @return The player Nickname
     */

    public void removeFromAdventure(String nickname) {

        if (adventureOrder.contains(getPlayer(nickname))) {

            adventureOrder.remove(getPlayer(nickname));

        } else adv_index--;

    }

    public List<Player> getAdv_done() {
        return adv_done;
    }


    /**
     * For each adventure creates the proper AdventureOrder, which depends on the rules of the specific card
     * Its runned only at the beginning for most cards because the order is defined at the Start of the Round
     * In the card Combat Zone its run multiple times, in fact this card has multiple phases
     * @param adventure
     */
    public void initializeAdventure(CardAdventure adventure) {
        adv_index = 0;
        this.adventure = adventure;
        if(!adv_done.isEmpty())
            adv_done.clear();
        List<Player> ranking = game.getBoard().getRanking();

        switch (adventure.getType()) {
            case AbandonedStation:
                adventureOrder = new ArrayList<>();
                AbandonedStation abandonedStation = (AbandonedStation) adventure;
                for (Player p : ranking) {
                    if (p.getShip().getNumOfCrewmates() >= abandonedStation.getNeeded_crewmates() && !disconnected_players.contains(p.getNickname())) {
                        adventureOrder.add(p);
                    }
                }
                break;

            case AbandonedShip:
                adventureOrder = new ArrayList<>();
                AbandonedShip abandonedShip = (AbandonedShip) adventure;
                for (Player p : ranking) {
                    if (p.getShip().getNumOfCrewmates() > abandonedShip.getCrewmates_loss() && !disconnected_players.contains(p.getNickname())) {
                        adventureOrder.add(p);
                    }
                }
                break;

            case Planets:
                planets = "";
                adventureOrder = new ArrayList<>(ranking);
                break;

            case MeteorSwarm:
                adv_index = getActivePlayers().size();
                adventureOrder = new ArrayList<>(ranking);
                break;

            default:
                adventureOrder = new ArrayList<>(ranking);
                break;
        }

        adventureOrder.removeIf(p -> !getActivePlayers().contains(p) || disconnected_players.contains(p.getNickname()));
        adventureOrderSize = adventureOrder.size();
    }


    public int getIn_pause() {
        return in_pause;
    }

    public String getPlanets() {
        return planets;
    }

    /**
     * Method Specific for the Planers Card , is used for take a Reference of which Planet is already taken or free
     * @param planet
     */
    public void addPlanetTaken(String planet) {
        if (planet.isEmpty()) {
            planets = planet;
        } else {
            planets = planets + " " + planet;
        }
    }

    /**
     * From the Nickname it returns the Instance of the Player Object
      * @param nickname
     * @return Player Object
     */
    public Player getPlayer(String nickname) {
        for (Player p : getActivePlayers()) {

            if (p.getNickname().equals(nickname)) {
                return p;
            }
        }
        return null;
    }

    public List<Player> getAdventureOrder() {
        return adventureOrder;
    }

    public int getAdv_index() {
        return adv_index;
    }

    /**
     * Return the next Player on the Adventure Order
     * @return
     */
    public String nextAdventurePlayer() {

        if(adv_index > 0) {
            Player player = adventureOrder.get(adv_index - 1);
            adv_done.add(player);

        }

        Player player = adventureOrder.get(adv_index);
        adv_index++;
        setCurr_adventure_player(player.getNickname());
        return player.getNickname();


    }

    public void movePlayer(String nickname, int pos) {
        Player p = game.getPlayer(nickname);
        game.getBoard().movePlayer(p, pos);

    }


    public GameState getGamestate() {
        return game_state;
    }


    public void setBuild_order_players(List<String> players) {

        for (String nick : players) {

            Player player = game.getPlayer(nick);
            if (!build_order_players.contains(player)) {
                build_order_players.add(player);
            }
        }

    }

    public void setGamestate(GameState game_state) {
        this.game_state = game_state;
    }

    /**
     * Calculate a random Value from 2 dice
     * @return
     */
    public int throwDice() {

        return (random.nextInt(6) + 1) + (random.nextInt(6) + 1);

    }


    public void startGame() {
        game_state = BUILD_PHASE;
        game.startGame();

    }


    public Map<Direction, List<CardAdventure>> seeDecksOnBoard() {


        return game.seeDecksOnBoard();
    }


    public List<Player> getActivePlayers() {
        return game.getActivePlayers();
    }

    /**
     * Put all the players with a valid ship in the Active Players List
     */
    public void startFlight() {
        game.setActivePlayers(getBuild_order_players());
        game.startFlight();
    }

    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    public Lobby getLobby() {
        return lobby;
    }

    public List<Color> getAvailable_colors() {
        return available_colors;
    }

    /**
     * When a Player joins a lobby the Controller adds him to the Game
     * @param nickname
     * @param color
     * @return
     */
    public synchronized Player addPlayer(String nickname, Color color) {
        if (game.getNicknames().contains(nickname)) throw new IllegalArgumentException(" Nickname already in use.");

        if (!available_colors.contains(color))
            throw new IllegalArgumentException("Color invalid or  already in use .\nthese are the available colors : " + available_colors);
        Player p = new Player(nickname, color, game);
        available_colors.remove(p.getColor());
        game.addPlayer(p);
        return p;

    }

    public Board getBoard() {
        return game.getBoard();
    }

    public synchronized CardComponent getRandomCard() {

        return game.getRandomCardComponent();

    }


    public void addWaitingFlyPlayer(String nick) {
        waiting_fly_players.add(nick);
    }

    public List<String> getWaitingFlyPlayers() {
        return waiting_fly_players;
    }

    public void finishSupplyPhase(String nickname) {
        Player p = game.getPlayer(nickname);
        finished_supply_players.add(p);

    }

    public List<Player> getBuild_order_players() {
        return build_order_players;
    }

    public CardAdventure getRandomAdventure() {
        return game.getRandomCardAdventure();
    }

    /**
     * When a player declares the Final ship it checks the validity of It and return the correspondent value
     * @param nickname
     * @return
     */
    public  int endPlayerBuildPhase(String nickname) {

        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        Map<Pair<Integer, Integer>, Boolean> battery_usage = new HashMap<>();
        if (ship.calculateEnginePower(battery_usage) == 0) {
            System.out.println("Im sorry but you dont have any Engine you cannot start the game.");
            return -1;

        }


        if (ship.checkShipConnections().isEmpty()) {
            System.out.println("Congratulations, you ship is Valid, you can start the supply!");
            return 1;
        } else {

            System.out.println("You cannot start the fly because your ship isn't valid , here are the invalid pieces : ");
            System.out.println(ship.checkShipConnections());
            return 0;

        }


    }

    public void removePlayerFromOrder(String nickname) {
        build_order_players.remove(nickname);
    }

    /**
     * Add a component in the player ship board
     * @param nickname
     * @param card
     * @param x row
     * @param y column
     */
    public void addComponent(String nickname, CardComponent card, int x, int y) {
        Player p = game.getPlayer(nickname);

        try {
            p.addToShip(card, x, y);

            p.getShip().getExtra_components().remove(card);
            System.out.println("Added component!");
            System.out.println(card);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    /**
     * Add the component to the ExtraComponents deck of the player
     * @param nickname
     * @param card
     */
    public void secureComponent(String nickname, CardComponent card) {


        Player p = game.getPlayer(nickname);


        try {
            if (p.getShip().getExtra_components().size() < 2)
                p.secureComponent(card);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    /**
     * Add the Card to the dismissed (faced up) deck
     * @param nickname
     * @param card
     */
    public void dismissComponent(String nickname, CardComponent card) {

        Player p = game.getPlayer(nickname);
        try {
            p.dismissComponent(card);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }


    /**
     * Add the Crewmate in the Living Unit in x,y
     * @param nickname
     * @param x row
     * @param y column
     * @param type The color of the alien or normale crewmmate
     */
    public void crewmatesSupply(String nickname, int x, int y, CrewmateType type) {


        try {
            Player p = game.getPlayer(nickname);
            Ship ship = p.getShip();
            CardComponent card = ship.getComponent(x, y);

            if (card.getComponentType() != ComponentType.LivingUnit)
                throw new IllegalArgumentException("The component isn't a LivingUnit");

            if (type == CrewmateType.Astronaut) ((LivingUnit) card).addAstronauts();
            else if (ship.checkAlienSupport(card).contains(type)) {
                ((LivingUnit) card).addAlien(type);


            } else {
                System.out.println("Alien Support not found.");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    /**
     * Calculate and return the list of the invalids components
     * @param nickname
     * @return
     */
    public List<Pair<Integer, Integer>> checkShipConnectors(String nickname) {

        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        return ship.checkShipConnections();
    }

    public void setShipPlance(String nickname, Ship ship) {
        Player p = game.getPlayer(nickname);

        p.setShip(ship);

    }

    /**
     * Returns the "Tronconi" of the ship, if the size is 0 the ship is invalid
     * The Invalids "Tronconi" or Single Components are removed automatically
     * @param nickname
     * @return
     */
    public List<List<Pair<Integer, Integer>>> getValidPieces(String nickname) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();


        return ship.findShipPieces();

    }

    /**
     * Remove all the others "Tronconi" and keeps the chosen one
     * @param choice
     * @param nickname
     * @return
     */
    public CardComponent[][] choosePieces(int choice, String nickname) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        ship.choosePiece(choice);

        return ship.getShipBoard();
    }

    public CardComponent removeCardFacedUp(int index) {
        return game.getFacedUpCard(index);
    }

    public List<CardComponent> getFacedUpCards() {
        return game.getCards_faced_up();
    }


    /**
     * It Returns the nickname of the Player with fewer crewmates
     * @return
     */
    public String calculateLessCrewmates() {
        List<Player> players = getActivePlayers();
        String lessCrewmatesNickname = "";
        int lessCrewmates = 100;
        int total_crewmates = 0;
        Ship ship;
        for (Player p : players) {
            ship = p.getShip();
            for (int i = 0; i < ship.getROWS(); i++) {
                for (int j = 0; j < ship.getCOLS(); j++) {
                    if (ship.getComponent(i, j).getComponentType() == ComponentType.LivingUnit)
                        total_crewmates += ((LivingUnit) ship.getComponent(i, j)).getNum_crewmates();
                }
            }

            if (total_crewmates < lessCrewmates) {
                lessCrewmatesNickname = p.getNickname();
                lessCrewmates = total_crewmates;
            }
        }

        return lessCrewmatesNickname;
    }


    /**
     * Execute this specific Adventure Card (the Client has no power to will in this one)
     * @param stardust
     */
    public void executeStardust(Stardust stardust) {
        initializeAdventure(stardust);
        for (Player p : adventureOrder) {

            movePlayer(p.getNickname(), -p.getShip().calculateExposedConnectors());


        }
    }

    /**
     * Called at the end of the Game
     * Assign each player credits for their ,cargo, ship, position and removes 1 credit for each destroyed Component
     */
    public void setRewards() {

        game.setRewards();


    }


    /**
     * Add the player the number of credits
     * @param nickname
     * @param credits
     */

    public void addCredits(String nickname, int credits) {

        for (Player p : getActivePlayers()) {

            if (p.getNickname().equals(nickname)) {

                p.setCredits(p.getCredits() + credits);

            }

        }


    }


}
