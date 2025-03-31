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
        avaible_colors.add(Color.RED);
        avaible_colors.add(Color.GREEN);
        avaible_colors.add(Color.YELLOW);
        avaible_colors.add(Color.BLUE);


    }

        public synchronized Player addPlayer(String nickname, Color color) {
        if(game.getNicknames().contains(nickname)) throw new IllegalArgumentException(" Nickname already in use.");

        if(!avaible_colors.contains(color)) throw new IllegalArgumentException("Color invalid or  already in use .\nthese are the available colors : " + avaible_colors);
        Player  p= new Player(nickname, color);
        avaible_colors.remove(p.getColor());
        game.addPlayer(p);
        return p;

    }

    public synchronized CardComponent getRandomCard() {

        return game.getRandomCardComponent();

    }



    public synchronized int endPlayerBuildPhase(String nickname) {

        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        Map<CardComponent, Boolean> battery_usage = new HashMap<>();
        if(ship.calculateEnginePower(battery_usage)==0){
            System.out.println("Im sorry but you dont have any Eninge you cannot start the game.");
            return -1;

        }

        if(ship.checkShipConnections().isEmpty()) {
            System.out.println("Congratulations, you ship is Valid, you can start the supply!");
            return 1;
        }else{

            System.out.println("You cannot start the fly because your ship isn't valid , here are the incorret pieces : ");
            System.out.println(ship.checkShipConnections());
            return 0;

        }




    }



    public void addComponent(String nickname, CardComponent card,int x, int y) {
        Player p = game.getPlayer(nickname);

        try{
            p.addToShip(card,x,y);
            System.out.println("Added component!");
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

    }


    public void secureComponent(String nickname, CardComponent card) {


        Player p = game.getPlayer(nickname);

        try{
            p.secureComponent( card);

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

    }

public void dismissComponent(String nickname, CardComponent card) {

        Player p = game.getPlayer(nickname);
        try{
        p.dismissComponent(card);
}catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

    }


    public synchronized CardComponent pickComponentFacedUp( int index ) {

        if(index<0 || index > game.getPlayers().size()) throw new IllegalArgumentException("Index out of bounds");

        return  game.getFacedUpCard(index);

    }

    public void startSupply(String nickname) {

        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();



    }




}
