package it.polimi.ingsw.model;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;

import java.util.*;

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
public abstract class BaseGame {
    protected List<Player> players = new ArrayList<>();
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

    public Gametype getType() {
        return type;
    }

    public abstract void createDeckAdventure();

    /**
     * Gives the final ranking of the players based on the criteria of SetRewards method.
     * @param players
     */
    public void setRanking(List<Player> players) {
        players.sort(Comparator.comparingInt(Player::getCredits).reversed());
        this.ranking=players;
    }

    public void setDeck_adventure(List<CardAdventure> deck_adventure) {
        this.deck_adventure = deck_adventure;
    }

    /**
     * @return list of adventure cards
     */
    public List<CardAdventure> getDeck_adventure() {
        return deck_adventure;
    }

    /**
     * This method is called at the end of the game to give to each player its rewards.Based on:
     * <li>Order of arrival(bonus)</li>
     * <li>delivered cargos(bonus)</li>
     * <li>best ship(bonus)</li>
     * <li>extra_components(malus)</li>
     */
    public void setRewards() {
        //gives credits based on arrival order and carried cargos (only for players that ended the game)
        for (Player p : active_players) {
            if (p.equals(board.getRanking().get(0))) {
                p.receiveCredits(4);
            }
            if (p.equals(board.getRanking().get(1))) {
                p.receiveCredits(3);
            }
            if (p.equals(board.getRanking().get(2))) {
                p.receiveCredits(2);
            }
            if (p.equals(board.getRanking().get(3))) {
                p.receiveCredits(1);
            }

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 7; col++) {
                    if (p.getShip().getComponent(row, col).getComponentType().equals(BlueStorage)||p.getShip().getComponent(row, col).getComponentType().equals(RedStorage)){
                        Storage storage=(Storage) p.getShip().getComponent(row, col);
                        List<Cargo> cargos= storage.getCarried_cargos();
                        for (Cargo c : cargos){
                            switch (c){
                                case Red:

                                    p.receiveCredits(4);
                                    break;

                                case Yellow:

                                    p.receiveCredits(3);
                                    break;

                                case Green:
                                    p.receiveCredits(2);
                                    break;

                                case Blue:
                                    p.receiveCredits(1);
                                    break;

                                default:
                                    break;

                            }
                        }
                    }
                }

            }
        }
        //gives credits to the players who have the best ship (only for players that ended the game)
        List<Player> best_ships = new ArrayList<>();
        best_ships.add(active_players.get(0));
        for (Player p : active_players) {
            if (p.getExposed_connectors() < best_ships.get(0).getExposed_connectors()) {
                best_ships.clear();
                best_ships.add(p);
            }
            if (p.getExposed_connectors() == best_ships.get(0).getExposed_connectors()) {
                best_ships.add(p);
            }
        }
        for (Player p : best_ships) {
            p.receiveCredits(2);
        }

        //remove credits due to extra components on the plance (secured but not used and/or eliminated)
        for (Player p : players) {
            p.LostCredits(p.getShip().getExtra_components().size());
        }


        //gives the reward for the cargo delivered by the players who gave up
        for (Player p : players) {
            if(!active_players.contains(p)){
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
                p.receiveCredits((reward_cargo+1)/2);
            }
        }
    }

    /**
     *this method is used to initialise the deck of CardComponents before building the ship
     * @param deck_components the entire list of CardComponent used to build the ships
     */

    public void setDeck_components(List<CardComponent> deck_components) {
        this.deck_components = deck_components;
    }

    /**
     * @return a list containing the deck of CardCardComponents
     */
    public List<CardComponent> getDeck_components() {
        return deck_components;
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
     * @param players the list of participants in the game
     */
    public void setActivePlayers(List<Player> players) {
        this.active_players = players;
    }

    /**
     * this method is called when a player join the game
     * @param player the participant
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * @return return a random card component from the deck that is faced up
     * @see CardComponent as 'random card component' we mean a card component whose face_down==true
     */
    public CardComponent getRandomCardComponent() {
        CardComponent card_drawn= deck_components.removeFirst(); //picks the first card of the list (shuffled )
        return card_drawn;
    }

    /**
     * This method is called when the player wants to pick a card from the faced up cards
     * @return  the card of Card selected
     */
    public CardComponent getFacedUpCard(int index) {
        return cards_faced_up.remove(index);
    }

    /**
     * this method is called when a player loose or choose to give up
     * @param player
     */
    public void removePlayer(Player player) {
        active_players.remove(player);
    }

    /**
     * This method is used to find a player
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

    /**@return list of active players, that is, those who have not left the game*/
    public List<Player> getActive_players() {
        return active_players;
    }

    /**@return list of player's nicknames*/
    public List<String> getNicknames() {
        List<String> nicknames = new ArrayList<>();
        for (Player player : players) {
            nicknames.add(player.getNickname());
        }
        return nicknames;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public List<CardComponent> getCards_faced_up() {
        return cards_faced_up;
    }

    public void setCards_faced_up(List<CardComponent> cards_faced_up) {
        this.cards_faced_up = cards_faced_up;
    }
    public void setActive_players(List<Player> active_players) {
        this.active_players = active_players;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

}
