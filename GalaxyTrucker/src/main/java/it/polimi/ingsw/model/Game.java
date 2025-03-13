package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;

import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;
import static it.polimi.ingsw.model.ComponentType.*;

public class Game {

    private List<Player> players = new ArrayList<>();
    private int numPlayers;
    private List<CardAdventure> deck_adventure = new ArrayList<>();
    private static final int clock_time = 30; //30 sec messi a caso
    private List<Player> active_players = new ArrayList<>();
    private Player board_leader;
    private List<CardComponent> deck_components = new ArrayList<>();
    private Board board ;


    public void startGame() {

        //Created some components just to test the print and the constructor


        Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
        connectors.put(North, Universal);
        connectors.put(South, Double);
        connectors.put(East, Smooth);
        connectors.put(West, Cannon_Connector);

        deck_components.add(new CardComponent(DoubleCannon, connectors));


        connectors.put(North, Single);
        connectors.put(South, Smooth);
        connectors.put(East, Smooth);
        connectors.put(West, Engine_Connector);

        deck_components.add(new CardComponent(DoubleEngine, connectors));


        //print them all
        for (CardComponent element : deck_components) {
            System.out.println(element);
        }

    }

    public void startAssembly() {

        active_players.addAll(players);

    }

    public void startFlight(){
        board = new Board(active_players);

    }

    public void addPlayer(Player player) {

        players.add(player);


    }

    public void checkShipValidity() {

//da pensare bene
    }

    public CardComponent GetRandomCardComponent() {
        Collections.shuffle(deck_components);  //shuffle the list and remove the first (returns it)
        return deck_components.removeFirst();

    }

    public void removePlayer(Player player) {
        active_players.remove(player);
    }


    public Board getBoard() {
        return board;
    }
}