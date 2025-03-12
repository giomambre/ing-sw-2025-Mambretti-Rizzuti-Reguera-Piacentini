package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.List;

public class Game {

private Board board;
private List<Player> players = new ArrayList<>();
private int numPlayers;
private List<CardAdventure> deck_adventure = new ArrayList<>();
private static final int clock_time = 30; //30 sec messi a caso
private List<Player> active_players = new ArrayList<>();
private Player board_leader ;
private List<CardComponent> deck_components = new ArrayList<>();



public void startGame(){



}

public void startAssembly(){


}


public void addPlayer(Player player){

    players.add(player);



}
public void checkShipValidity(){


}
}