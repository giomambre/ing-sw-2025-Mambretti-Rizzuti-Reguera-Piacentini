package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;

import java.util.*;
public class GameController {


    List<Player> disconnected_players = new ArrayList<>();
    List<Color> avaible_colors = new ArrayList<>();

    Game game;

    public GameController() {

        game = new Game();
        avaible_colors.add(Color.Red);
        avaible_colors.add(Color.Green);
        avaible_colors.add(Color.Yellow);
        avaible_colors.add(Color.Blue);


    }

        public synchronized void addPlayer(String nickname, Color color) {
        if(game.getNicknames().contains(nickname)) throw new IllegalArgumentException("Nickname already in use.");

        if(!avaible_colors.contains(color)) throw new IllegalArgumentException("Color already in use.");

        game.addPlayer(new Player(nickname, color));

    }


    public synchronized void addToActivePlayers(String nickname) {

        Player p = game.getPlayer(nickname);
        p.endBuild();

        if(game.getActive_players().size() == (game.getPlayers().size() - disconnected_players.size())) {
            game.startFlight();
        }

    }

    public void addComponent(String nickname, CardComponent card,int x, int y) {
        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();

        try{
            ship.addComponent(card,x,y);
            System.out.println("Added component!");
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

    }




}
