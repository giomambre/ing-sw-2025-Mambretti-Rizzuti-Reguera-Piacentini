package it.polimi.ingsw.model;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.Gametype;
import it.polimi.ingsw.model.view.InvalidGameActionException;

import java.io.Serializable;
import java.util.*;

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
public class Player implements Serializable {
    private String nickname;
    private Color color;
    private Ship ship = new Ship(this);
    private int exposed_connectors = 0;
    private transient BaseGame game;
    private int credits;
    private int num_laps;

    /*
    The type of match (quick or standard) is set at run time
     */
    public Player(String nickname, Color color, BaseGame game) {
        this.nickname = nickname;
        this.color = color;
        this.credits = 0;
        this.num_laps = 0;
        this.game = game;
    }


    /**
     * This function is called when the player finish to build his ship. The player is added to the active participant of the game.
     */
    public void endBuild(){
        List<Player> active_players = game.getActivePlayers();
        active_players.add(this);

    }

    /**
     * This method is called when a player wants to look at one of the decks placed on the board during the construction phase
     * @param direction witch of the group the player wants to look
     *                  <ul>
     *                  <li>North: deck on top (forbidden action)</li>
     *                  <li>South: middle deck</li>
     *                  <li>West:right deck</li>
     *                  <li>East: left deck</li>
     *                  </ul>
     * @return the deck that the player wants to see
     */
    public List<CardAdventure> watchcardsonboard(Direction direction) {
        if(game.getType()!=Gametype.StandardGame){
            throw new InvalidGameActionException("This action is forbidden in quick game");
        }
        Game game=(Game)this.game;
        switch (direction) {
            case North:
                throw new InvalidGameActionException("This group of card is secret until the flight start!");

            case East:
                return game.getDeck_right();

            case South:
                return game.getDeck_middle();

            case West:
                return game.getDeck_left();

            default:
                return Collections.emptyList();
        }
    }

    /**
     * This method is called by the controller when the player during the building phase wants to secure a component so he could use it later (or not, penalty at the end of the game).
     * This method is available only for the standard game. (not in quick game)
     * Each player can have maximum 2 secured component at the same time.
     * @param component the component that the player wants to secure
     */
    public void secureComponent(CardComponent component) {
        if(game.getType() == Gametype.StandardGame) {
            List<CardComponent> extra_components = ship.getExtra_components();
            if (extra_components.size() < 2) {
                extra_components.add(component);
            } else throw new IllegalArgumentException("Already has more than 2 components");
        }else throw new InvalidGameActionException("This action is forbidden in quick game");

    }

    /**
     * This method adds a CardComponent to the ship
     * @param component the Card to add
     * @param row to identify in witch row on the plance the card will be added
     * @param col to identify in witch col on the plance the card will be added
     */
    public void addToShip(CardComponent component, int row, int col) {
        ship.addComponent(component, row, col);
    }

    /**
     * This method is used to re-add a drawn but not used CardComponent to the deck.
     * @param component the card that needs to be re-added
     */
    public void dismissComponent(CardComponent component) {
        List<CardComponent> deck = game.getCards_faced_up();
        deck.add(component);
      
    }

    /**
     * This method is called by the controller when the player wants to add to his ship a CardComponent that has been secured in the past.
     * That is possible only in the standard game, not in the quick version.
     * @param component
     */
    public void useExtraComponent(CardComponent component) {
        if(game.getType()==Gametype.StandardGame) {
            List<CardComponent> extra_components = ship.getExtra_components();
            if (!extra_components.contains(component)) {

                throw new IllegalArgumentException("Extra card component not found");

            }
            extra_components.remove(component);
            ship.setExtra_components(extra_components);
        }else throw new InvalidGameActionException("This action is forbidden in quick game");
    }

    /**
     *This method
     * @return the result of the dices
     */
    public int throwDice(){
        Random dice1 = new Random();
        Random dice2 = new Random();

        return (dice1.nextInt(6)+1)+(dice2.nextInt(6)+1);
    }

    /**
     * This method is used when the player has to receive credits at the end of the game
     * @param credits
     */
    public void receiveCredits(int credits) {
        this.credits += credits ;
    }
    /**
     * This method is used when the player loose credits due to extra components on his plance(secured but not used or destroyed)
     * @param credits
     */
    public void LostCredits(int credits) {
        this.credits -= credits ;
        if (credits<0) this.credits = 0;
    }


    /**
     * This method is used to find out how many exposed connectors a ship has.
     * @return the number of exposed connectors
     */
    public int getExposed_connectors() {
        return exposed_connectors;
    }

    /**
     * This method updates the list of exposed connectors for the player's ship.
     * Calls {@code calculateExposedConnectors()} on the player's ship to determine
     * which connectors are currently not connected to other components.
     */
    public void setExposed_connectors() {
        this.exposed_connectors = ship.calculateExposedConnectors();
    }

    /**
     * This method is used to find out how many credits a player has.
     * @return the number of credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * This method is called by the controller when a player chooses to leave the game,
     * or is forced to leave because he has lost all humans, have no engine strength in open space, or have been lapped by the leader.
     * It calls the 'removePlayer' function from the Game class.
     */
    public void leaveGame(){
        game.removePlayer(this);


    }

    /**@return player's ship*/
    public Ship getShip() {
        return this.ship;
    }

    /**@return player's rocket color*/
    public Color getColor() {
        return color;
    }

    /**@return player's nickname*/
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the {@code BaseGame} object the player is part of
     */
    public BaseGame getGame() {
        return game;
    }

    /**@return the number of laps*/
    public int getNum_laps() {
        return num_laps;
    }

    /**
     * This method is called when a player does a complete lap on the board.
     */
    public void addLap(){ this.num_laps++; }

    /**
     * This method is called when a player lose days of flight and the new position is before of the 1 cell.
     */
    public void subLap(){ this.num_laps--; }

    public String toString(){
        return "Player with Nickname : " + this.nickname + "and color : " + this.getColor().toString();
    }

    public Player copyPlayer() {
        Player original = this;
        Player copy = new Player(getNickname(),getColor(),getGame());  // o costruttore adatto

        // Copia la Ship e il suo stato
        Ship copiedShip = new Ship(this);  // dipende dalla tua implementazione
        copiedShip.setShip_board(ship.deepCopyBoard(original.getShip().getShipBoard()));
        copy.setShip(copiedShip);
        copiedShip.setExtra_components(original.getShip().getExtra_components());
        // Se servono altri campi visibili al client (es. colore, punti, ecc.)
        copy.setColor(original.getColor());
        copy.setCredits(original.getCredits());

        return copy;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setExposed_connectors(int exposed_connectors) {
        this.exposed_connectors = exposed_connectors;
    }

    public void setGame(BaseGame game) {
        this.game = game;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setNum_laps(int num_laps) {
        this.num_laps = num_laps;
    }
}
