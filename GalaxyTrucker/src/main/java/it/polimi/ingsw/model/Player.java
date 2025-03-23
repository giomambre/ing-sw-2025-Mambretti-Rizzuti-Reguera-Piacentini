package it.polimi.ingsw.model;
import java.util.List;
import java.util.Random;

public class Player {
    private String nickname;
    private Color color;
    private Ship ship = new Ship(this);
    private int exposed_connectors = 0;
    private Game game = new Game();
    private int credits;
    private int num_laps;
    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.credits = 0;
        this.num_laps = 0;
    }

    public void EndBuild(){
        List<Player> active_players = game.getActivePlayers();
        active_players.add(this);
        game.setActivePlayers(active_players);
    }

    public void SecureComponent(CardComponent component) {
        List<CardComponent> extra_components = ship.getExtra_components();
        if(extra_components.size() < 2) {
            extra_components.add(component);
        }
        ship.setExtra_components(extra_components);

    }

    public void AddToShip(CardComponent component, int row, int col) {
        ship.AddComponent(component, row, col);
    }

    public void DismissComponent(CardComponent component) {
        List<CardComponent> deck_components = game.getDeck_components();
        deck_components.add(component);
        game.setDeck_components(deck_components);
    }

    public void UseExtraComponent(CardComponent component) {
        List<CardComponent> extra_components = ship.getExtra_components();
        extra_components.remove(component);
        ship.setExtra_components(extra_components);
    }

    public int ThrowDice(){
        Random dice1 = new Random();
        Random dice2 = new Random();

        return (dice1.nextInt(6)+1)+(dice2.nextInt(6)+1);
    }

    public void reciveCredits(int credits) {
        this.credits += credits ;
    }


    public int getExposed_connectors() {
        return exposed_connectors;
    }

    public int getCredits() {
        return credits;
    }


    public Ship getShip() {
        return ship;
    }

    public Color getColor() {
        return color;
    }

    public String getNickname() {
        return nickname;
    }

    public Game getGame() {
        return game;
    }

    public int getNum_laps() {return num_laps;}

    public void addLap(){ this.num_laps++; }

    public void subLap(){ this.num_laps--; }

}
