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
    Map<String, Double> list_engine_power = new HashMap<>();
    Map<String, Double> list_cannon_power = new HashMap<>();
    private static final Random random = new Random();
    int in_pause = 0;
    Lobby lobby;
    GameState game_state;
    BaseGame game;
    private String curr_adventure_player = "";

    public List<String> getDisconnected_players() {
        return disconnected_players;
    }

    public String getCurr_adventure_player() {
        return curr_adventure_player;
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

    public void reConnect(String nickname) {
        disconnected_players.remove(nickname);
    }

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

    public void addCannonValue(String name, double value) {

        list_cannon_power.put(name, value);
    }

    public void addEngineValue(String name, Double value) {

        list_engine_power.put(name, value);
    }


    public Map<String, Double> getListCannonPower() {
        return list_cannon_power;
    }

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

  /*  public String getLeastEngineValue() {
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
*/
    public Map<String, Double> getEngineValues() {
        return list_engine_power;
    }

    public void removeFromAdventure(String nickname) {

        if (adventureOrder.contains(getPlayer(nickname))) {
            adventureOrder.remove(getPlayer(nickname));

        } else adv_index--;


    }

    public void initializeAdventure(CardAdventure adventure) {


        adv_index = 0;
        this.adventure = adventure;

        adventureOrder = new ArrayList<>();
        switch (adventure.getType()) {
            case AbandonedStation:
                AbandonedStation abandonedStation = (AbandonedStation) adventure;
                for (Player p : game.getBoard().getRanking()) {

                    if (p.getShip().getNumOfCrewmates() >= abandonedStation.getNeeded_crewmates() && !disconnected_players.contains(p.getNickname())) {

                        adventureOrder.add(p);
                    }

                }
                break;


            case AbandonedShip:
                AbandonedShip AbandonedShip = (AbandonedShip) adventure;
                for (Player p : game.getBoard().getRanking()) {

                    if (p.getShip().getNumOfCrewmates() > AbandonedShip.getCrewmates_loss() && !disconnected_players.contains(p.getNickname())) {

                        adventureOrder.add(p);
                    }

                }
                break;
            case Planets:
                planets = "";

                adventureOrder = game.getBoard().getRanking();

                break;

            case MeteorSwarm:
                adv_index = getActivePlayers().size();

            default:
                adventureOrder = game.getBoard().getRanking();
                break;

        }


        for (Player p : adventureOrder) {

            if (!getActivePlayers().contains(p) && disconnected_players.contains(p.getNickname())) {
                adventureOrder.remove(p);
            }

        }

    }


    public int getIn_pause() {
        return in_pause;
    }

    public String getPlanets() {
        return planets;
    }

    public void addPlanetTaken(String planet) {
        if (planet.isEmpty()) {
            planets = planet;
        } else {
            planets = planets + " " + planet;
        }
    }

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


    public String nextAdventurePlayer() {

        Player player = adventureOrder.get(adv_index);
        adv_index++;
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

  /*  public String getPlayerNickname(Player player) {
        return player.getNickname();
    }*/

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

    public List<Player> getFinished_supply_players() {
        return finished_supply_players;
    }


    public void putPlayersOnBoard(List<Player> players) {
        game.getBoard().putPlayersOnBoard(players);
    }

   /* public List<Player> ordinaPlayers(List<Player> players, List<String> order) {


        Map<String, Player> playerMap = new HashMap<>();
        for (Player p : players) {
            playerMap.put(p.getNickname(), p);
        }

        List<Player> order_player = new ArrayList<>();
        for (String nick : order) {
            if (playerMap.containsKey(nick)) {
                order_player.add(playerMap.get(nick));
            }
        }

        return order_player;
    }*/


    public CardAdventure getRandomAdventure() {
        return game.getRandomCardAdventure();
    }

    public synchronized int endPlayerBuildPhase(String nickname) {

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


    public void secureComponent(String nickname, CardComponent card) {


        Player p = game.getPlayer(nickname);


        try {
            if (p.getShip().getExtra_components().size() < 2)
                p.secureComponent(card);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    public void dismissComponent(String nickname, CardComponent card) {

        Player p = game.getPlayer(nickname);
        try {
            p.dismissComponent(card);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }


/*
    public synchronized CardComponent pickComponentFacedUp(int index) {

        if (index < 0 || index > game.getPlayers().size()) throw new IllegalArgumentException("Index out of bounds");

        return game.getFacedUpCard(index);

    }
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

  /*  public void rotateCard(CardComponent card) {
        card.rotate();
    }

    public int getExposedConnector(String nickname) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();

        return ship.calculateExposedConnectors();
    }

    public CardComponent[][] getShipPlance(String nickname) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();

        return ship.getShipBoard();
    }
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

    public List<List<Pair<Integer, Integer>>> getValidPieces(String nickname) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();

//        for (List<Pair<Integer, Integer>> piece : pieces) {
//            CardComponent[][] matrix = new CardComponent[5][7]; // 5 righe, 7 colonne
//
//            for (Pair<Integer, Integer> coord : piece) {
//                int x = coord.getKey();
//                int y = coord.getValue();
//                matrix[x][y] = ship.getComponent(x, y);
//            }
//
//            validPieces.add(matrix);
//        }

        return ship.findShipPieces();

    }

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


/*
    public List<Cargo> getCargoReward(CardAdventure cardAdventure) {
        CardAdventureType type = cardAdventure.getType();
        switch (type) {
            case AbandonedStation -> {
                return ((AbandonedStation) cardAdventure).getCargo();
            }

            default -> throw new IllegalArgumentException("err");

        }
    }


    public void executeAbandonedStation(String nickname, Map<CardComponent, Map<Cargo, Integer>> new_cargo_positions, AbandonedStation abandonedStation) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        int total_crewmates = 0;
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                if (ship.getComponent(i, j).getComponentType() == ComponentType.LivingUnit)
                    total_crewmates += ((LivingUnit) ship.getComponent(i, j)).getNum_crewmates();
            }
        }

        if (total_crewmates > abandonedStation.getNeeded_crewmates()) {
            abandonedStation.execute(p, new_cargo_positions);
        } else {
            throw new IllegalArgumentException("Non hai abbastanza membri dell'equipaggiamento!");
        }
    }


    //Serie di metodi di Combat Zone che vanno gestiti poi dal controller in base a quale id esce
    public List<Pair<MeteorType, Direction>> getCombatZoneMeteors(CombatZone combatZone) {
        return combatZone.getMeteors();
    }

    public void executeMeteors(String nickname, CardAdventure meteor, Direction direction, MeteorType meteor_type, Boolean shield_usage, CardComponent battery, int position, Boolean double_cannon_usage) {
        Player p = game.getPlayer(nickname);
        //((MeteorSwarm) meteor).execute(p, direction, meteor_type, shield_usage, battery, position, double_cannon_usage);
    }*/

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


  /*  public void executeLessCrewmates1(String nickname, CombatZone combatZone) {
        Player p = game.getPlayer(nickname);
        combatZone.executeLessCrewmates1(p);
    }

    public void executeLessEnginePower1(String nickname, Map<CardComponent, Integer> astronaut_losses, CombatZone combatZone) {
        Player p = game.getPlayer(nickname);
        combatZone.executeLessEnginePower1(p, astronaut_losses);
    }

    //Metodi Combat Zone 2

    public void executeLessCannonPower2(String nickname, CombatZone combatZone) {
        Player p = game.getPlayer(nickname);
        combatZone.executeLessCannonPower2(p);
    }

    public void executeLessEnginePower2(String nickname, Map<CardComponent, Map<Cargo, Integer>> cargo_position, CombatZone combatZone) {
        Player p = game.getPlayer(nickname);
        combatZone.executeLessEnginePower2(p, cargo_position);
    }

    public void executeEpidemic(String nickname, Epidemic epidemic) {
        Player p = game.getPlayer(nickname);
        epidemic.execute(p.getShip());
    }

    public List<Pair<MeteorType, Direction>> getMeteorSwarmMeteors(MeteorSwarm meteor) {
        return meteor.getMeteors();
    }


    public List<Pair<MeteorType, Direction>> getMeteorSwarm(Pirates pirates) {
        return pirates.getMeteors();
    }


    public List<Cargo> getCargosPlanets(Planets planets, int choice) {
        return planets.getCargos(choice);
    }

    public void executePlanets(Planets planets, String nickname, Map<CardComponent, Map<Cargo, Integer>> cargos) {
        Player p = game.getPlayer(nickname);
        planets.execute(p, cargos);
    }


    public void executeWinSlavers(String nickname, Slavers slavers) {
        Player p = game.getPlayer(nickname);
        slavers.executeWin(p);
    }

    public void executeLossSlavers(String nickname, Map<CardComponent, Integer> astronaut_losses, Slavers slavers) {
        Player p = game.getPlayer(nickname);
        slavers.executeLoss(p, astronaut_losses);
    }


   public void executeWinSmugglers(String nickname, Smugglers smugglers, Map<CardComponent, Map<Cargo, Integer>> new_cargo_position, Boolean choice) {
        Player p = game.getPlayer(nickname);
        smugglers.executeWin(p, new_cargo_position, choice);
    }

    public void executeLossSmugglers(String nickname, Smugglers smugglers, Map<CardComponent, Map<Cargo, Integer>> cargo_position) {
        Player p = game.getPlayer(nickname);
        smugglers.executeLoss(p, cargo_position);
    }*/

    public void executeStardust(Stardust stardust) {
        initializeAdventure(stardust);
        for (Player p : adventureOrder) {

            movePlayer(p.getNickname(), -p.getShip().calculateExposedConnectors());


        }
    }

    public void setRewards() {

        game.setRewards();


    }


    public List<Player> winOrder() {
        List<Player> orderedList = new ArrayList<>(game.getActive_players());


        Collections.sort(orderedList, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.getCredits(), p1.getCredits());
            }
        });

        return orderedList;


    }

    public void addCredits(String nickname, int credits) {

        for (Player p : getActivePlayers()) {

            if (p.getNickname().equals(nickname)) {

                p.setCredits(p.getCredits() + credits);

            }

        }


    }


    public List<Player> getBuildPhasePlayers() {
        return game.getBuildPhasePlayers();
    }

}
