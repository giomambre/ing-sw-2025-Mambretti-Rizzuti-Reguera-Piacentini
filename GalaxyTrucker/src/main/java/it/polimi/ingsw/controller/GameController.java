package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.*;
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

        public  void addPlayer(String nickname, Color color) {
        if(game.getNicknames().contains(nickname)) throw new IllegalArgumentException("Nickname already in use.");

        if(!avaible_colors.contains(color)) throw new IllegalArgumentException("Color already in use.");

        game.addPlayer(new Player(nickname, color));

    }




}
