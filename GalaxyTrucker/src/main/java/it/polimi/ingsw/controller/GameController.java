package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Gametype;

import java.util.*;

public class GameController {


    List<Player> disconnected_players = new ArrayList<>();
    List<Color> available_colors = new ArrayList<>();

    Lobby lobby;

    BaseGame game;

    public GameController(Lobby lobby) {
        this.lobby = lobby;
        game = new Game(Gametype.StandardGame);
        available_colors.add(Color.RED);
        available_colors.add(Color.GREEN);
        available_colors.add(Color.YELLOW);
        available_colors.add(Color.BLUE);


    }

    public void startGame() {

        game.startGame();

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

    public synchronized CardComponent getRandomCard() {

        return game.getRandomCardComponent();

    }


    public synchronized int endPlayerBuildPhase(String nickname) {

        Player p = game.getPlayer(nickname);
        Ship ship = p.getShip();
        Map<CardComponent, Boolean> battery_usage = new HashMap<>();
        if (ship.calculateEnginePower(battery_usage) == 0) {
            System.out.println("Im sorry but you dont have any Engine you cannot start the game.");
            return -1;

        }

        if (ship.checkShipConnections().isEmpty()) {
            System.out.println("Congratulations, you ship is Valid, you can start the supply!");
            return 1;
        } else {

            System.out.println("You cannot start the fly because your ship isn't valid , here are the incorret pieces : ");
            System.out.println(ship.checkShipConnections());
            return 0;

        }


    }


    public void addComponent(String nickname, CardComponent card, int x, int y) {
        Player p = game.getPlayer(nickname);

        try {
            p.addToShip(card, x, y);
            System.out.println("Added component!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }


    public void secureComponent(String nickname, CardComponent card) {


        Player p = game.getPlayer(nickname);

        try {
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


    public synchronized CardComponent pickComponentFacedUp(int index) {

        if (index < 0 || index > game.getPlayers().size()) throw new IllegalArgumentException("Index out of bounds");

        return game.getFacedUpCard(index);

    }

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

    public void rotateCard(CardComponent card) {
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

    public CardComponent removeCardFacedUp(int index) {
        return  game.getFacedUpCard(index);
    }

    public List<CardComponent> getFacedUpCards() {
        return game.getCards_faced_up();
    }


}
