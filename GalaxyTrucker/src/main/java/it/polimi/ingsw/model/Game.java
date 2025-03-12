package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;

public class Game {

private Board board;
private List<Player> players = new ArrayList<>();
private int numPlayers;
private List<CardAdventure> deck_adventure = new ArrayList<>();
private static final int clock_time = 30; //30 sec messi a caso
private List<Player> active_players = new ArrayList<>();
private Player board_leader ;
private List<CardComponent> deck_components = new ArrayList<>();

Random random = new Random();

public void startGame(){

    //Created some components just to test the print and the constructor



    Map<Direction,ConnectorType> connectors = new EnumMap<>(Direction.class);
    connectors.put(Direction.North,ConnectorType.Universal);
    connectors.put(Direction.South,ConnectorType.Double);
    connectors.put(Direction.East,ConnectorType.Smooth);
    connectors.put(Direction.West,ConnectorType.Cannon);

    deck_components.add(new CardComponent(ComponentType.DoubleCannon,connectors));



    connectors.put(Direction.North,ConnectorType.Double);
    connectors.put(Direction.South,ConnectorType.Smooth);
    connectors.put(Direction.East,ConnectorType.Engine);
    connectors.put(Direction.West,ConnectorType.Smooth);

    deck_components.add(new CardComponent(ComponentType.Engine,connectors));

    connectors.put(Direction.North,ConnectorType.Single);
    connectors.put(Direction.South,ConnectorType.Double);
    connectors.put(Direction.East,ConnectorType.Double);
    connectors.put(Direction.West,ConnectorType.Double);

    deck_components.add(new CardComponent(ComponentType.Tubes,connectors));

    //print them all
    for (CardComponent element : deck_components) {
        System.out.println(element);
    }

}

public void startAssembly(){


}


public void addPlayer(Player player){




}
public void checkShipValidity(){


}

public CardComponent GetRandomCardComponent(){
    Collections.shuffle(deck_components);  //shuffle the list and remove the first (returns it)
    return deck_components.remove(0);

}
}