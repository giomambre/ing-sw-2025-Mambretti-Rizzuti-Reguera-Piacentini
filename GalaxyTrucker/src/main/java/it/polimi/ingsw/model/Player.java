package it.polimi.ingsw.model;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * The Player class rapresent a player that joined the game.
 * When the player send a request to the server to join the game, an instance of the class is created.
 * <ul>
 *     <li> nickname: the nickname is how the player wants to be called during the game. Must be unique for each player</li>
 *     <li> color: Red,Green,Blue,Yellow</li>
 *     <li> ship: every player build a ship</li>
 *     <li>exposed_connectors: indicates how many connectors are exposed in the player's ship</li>
 *     <li>game: a reference to the current game</li>
 *     <li>num_laps: how many lap did the player do on the board</li>
 *
 * </ul>
 */
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
    //exposed connectors lo sposterei in ship (isa)

    /**
     * This function is called when the player finish to build his ship. The player is added to the participant in the game.
     */
    public void endBuild(){
        List<Player> active_players = game.getActivePlayers();
        active_players.add(this);
        game.setActivePlayers(active_players);
    }

    public void secureComponent(CardComponent component) {
        List<CardComponent> extra_components = ship.getExtra_components();
        if(extra_components.size() < 2) {
            extra_components.add(component);
        }
        ship.setExtra_components(extra_components);

    }

    public void addToShip(CardComponent component, int row, int col) {
        ship.addComponent(component, row, col);
    }

    public void dismissComponent(CardComponent component) {
        List<CardComponent> deck_components = game.getDeck_components();
        deck_components.add(component);
        game.setDeck_components(deck_components);
    }

    public void useExtraComponent(CardComponent component) {
        List<CardComponent> extra_components = ship.getExtra_components();
        extra_components.remove(component);
        ship.setExtra_components(extra_components);
    }

    public int throwDice(){
        Random dice1 = new Random();
        Random dice2 = new Random();

        return (dice1.nextInt(6)+1)+(dice2.nextInt(6)+1);
    }

    public void receiveCredits(int credits) {
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

    public int getNum_laps() {
        return num_laps;
    }

    public void addLap(){ this.num_laps++; }

    public void subLap(){ this.num_laps--; }

    public String toString(){
        return this.nickname;
    }
}
