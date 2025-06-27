package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.Shield;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;

/**
 * This class represents the game in general during the various phases. It's an abstract class because on run time it will be a Game or a QuickGame
 * <ul>
 *     <li>players: the list of players who joined the game</li>
 *     <li>numPlayers: how many people are playing</li>
 *     <li>active_players: the list of players that are still playing (means that they didn't lose or retire)</li>
 *     <li>cards_faced_up: the deck of the cards that are face up</li>
 *     <li>deck_components: the deck of the card component that are face down (all of them at first)</li>
 *     <li>board: where the players move their rockets</li>
 *     <li>ranking: the ranking of the players at the end of the game starting with the winner</li>
 *     <li>type: standard or quick</li>
 *
 * </ul>
 */
public abstract class BaseGame implements Serializable {
    protected List<Player> players = new ArrayList<>();
    protected List<Player> build_phase_players = new ArrayList<>();
    protected int numPlayers;
    protected List<Player> active_players = new ArrayList<>();
    protected List<CardComponent> deck_components = new ArrayList<>();
    protected List<CardComponent> cards_faced_up = new ArrayList<>();
    protected List<Player> ranking = new ArrayList<>();
    protected Board board;
    protected List<CardAdventure> deck_adventure = new ArrayList<>();
    protected Gametype type;

    public BaseGame(Gametype type) {
        this.type = type;
    }

    /**
     * This method is called at the beginning to start the game.
     * It is abstract because the game initialization differs between {@code QuickGame} and {@code Game},
     * and each subclass provides its own implementation.
     */
    public abstract void startGame();

    /**
     * @return type of Game (standard or quick)
     */
    public Gametype getType() {
        return type;
    }

    /**
     * This method is called at the beginning to create the adventure deck.
     * It is abstract because the deck creation logic differs between {@code QuickGame}
     * and {@code Game}, and each subclass provides its own implementation.
     */
    public abstract void createDeckAdventure();


    /**
     * called by the controller to give to each player a local copy of the decks present on the board
     *
     * @return a map having as key a direction that corresponds to a deck. (South=middle, East=Right, West=Left)
     */
    public abstract Map<Direction,List<CardAdventure>> seeDecksOnBoard();



    /**
     * This method is called at the end of the game to give to each player its rewards.Based on:
     * <li>Order of arrival(bonus)</li>
     * <li>delivered cargos(bonus)</li>
     * <li>best ship(bonus)</li>
     * <li>extra_components(malus)</li>
     */
    public void setRewards() {
        //gives credits based on arrival order and carried cargos (only for players that ended the game)
        int i = 0;
        for (Player p : board.getRanking()) {

            if(active_players.contains(p)) {


                switch (i) {

                    case 0:
                        p.receiveCredits(4);
                        break;
                    case 1:
                        p.receiveCredits(3);
                        break;
                    case 2:
                        p.receiveCredits(2);
                        break;
                    case 3:
                        p.receiveCredits(1);
                        break;


                }
            }
            i++;

        }
        List<Player> best_ships = new ArrayList<>();
        best_ships.add(players.getFirst());
        for (Player p : players) {
            if (p.getExposed_connectors() < best_ships.getFirst().getExposed_connectors()) {
                best_ships.clear();
                best_ships.add(p);
            }
            if (p.getExposed_connectors() == best_ships.getFirst().getExposed_connectors()) {
                best_ships.add(p);
            }
        }
        best_ships = best_ships.stream()
                .distinct()
                .collect(Collectors.toList());        for (Player p : best_ships) {
            p.receiveCredits(2);
        }



        for (Player p : players) {

                int reward_cargo=0;
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 7; col++) {
                        if (p.getShip().getComponent(row, col).getComponentType().equals(BlueStorage)||p.getShip().getComponent(row, col).getComponentType().equals(RedStorage)){
                            Storage storage=(Storage) p.getShip().getComponent(row, col);
                            List<Cargo> cargos= storage.getCarried_cargos();
                            for (Cargo c : cargos){
                                switch (c){
                                    case Red:

                                        reward_cargo+=4;
                                        break;

                                    case Yellow:

                                        reward_cargo+=3;
                                        break;

                                    case Green:
                                        reward_cargo+=2;
                                        break;

                                    case Blue:

                                        reward_cargo+=1;
                                        break;

                                    default:
                                        break;

                                }
                            }
                        }


                }

            }
            if(active_players.contains(p)) p.receiveCredits(reward_cargo);
            else p.receiveCredits((reward_cargo)/2);

        }
        for (Player p : players) {
            p.LostCredits(p.getShip().getExtra_components().size());
        }
    }

    /**
     * this method adds every Card Component existent to the deck of card components and then shuffles it. It's an abstract method because the deck depends on the type of match (quick or standard)
     */
    public abstract List<CardComponent> initializeDeckComponents();


    /**
     * this method is called to start the assembly phase
     */
    public void startAssembly() {
        active_players.addAll(players);
    }

    /*
    This method is called to start the flight and putting all the rockets on the board. the board is initialized here.
     */
    public abstract void startFlight() ;

    /**
     * @return return the list of players who are still competing
     */
    public List<Player> getActivePlayers() {
        return active_players;
    }

    /**
     * this method is called at the beginning of the game to add all the participants to the list of active_players
     *
     * @param players the list of participants in the game
     */
    public void setActivePlayers(List<Player> players) {
        this.active_players = players;
    }

    /**
     * this method is called when a player join the game
     *
     * @param player the participant
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * @return return a random card component from the deck that is faced up
     *
     * @see CardComponent as 'random card component' we mean a card component whose face_down==true
     */
    public CardComponent getRandomCardComponent() {
        CardComponent card_drawn= deck_components.removeFirst(); //picks the first card of the list (shuffled )
        return card_drawn;
    }

    /**
     * This method is called when the player wants to pick a card from the faced up cards
     *
     * @return  the card of Card selected
     */
    public CardComponent getFacedUpCard(int index) {
        return cards_faced_up.remove(index);
    }

    /**
     * this method is called when a player loose or choose to give up
     *
     * @param player
     */
    public void removePlayer(Player player) {
        active_players.remove(player);
    }

    /**
     * This method is used to find a player
     *
     * @param nickname
     * @return player called 'nickname'
     */
    public Player getPlayer(String nickname){


        for (Player p : players) {

            if (p.getNickname().equals(nickname)) {
                return p;
            }

        }
        throw new IllegalArgumentException("Nickname not found");

    }

    /** @return list of all players*/
    public List<Player> getPlayers() {
        return players;
    }

    /** @return number of players*/
    public int getNumPlayers() {
        return numPlayers;
    }


    /**@return list of player's nicknames*/
    public List<String> getNicknames() {
        List<String> nicknames = new ArrayList<>();
        for (Player player : players) {
            nicknames.add(player.getNickname());
        }
        return nicknames;
    }

    /**
     * Sets the list of players participating in the game.
     *
     * @param players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets the number of players in the game.
     *
     * @param numPlayers
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**@return the list of component cards currently face up.*/
    public List<CardComponent> getCards_faced_up() {
        return cards_faced_up;
    }


    /**
     * Sets the list of players who are currently active in the game.
     *
     * @param active_players
     */
    public void setActive_players(List<Player> active_players) {
        this.active_players = active_players;
    }

    /**
     * Sets the board for game.
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**@return the game board*/
    public Board getBoard() {
        return board;
    }

    /**
     * this method is called by the leader to draw the next card adventure
     *
     * @return it returns the first card adventure of the deck, if we already solved all the adventures it returns null
     */
    public abstract CardAdventure getRandomCardAdventure() ;


    public List<Player> getBuildPhasePlayers() {
        return build_phase_players;
    }

}
