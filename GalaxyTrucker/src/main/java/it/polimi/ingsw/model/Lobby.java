package it.polimi.ingsw.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a multiplayer game lobby with a fixed player limit.
 * The first player (creator) is automatically added upon creation.
 *
 */
public class Lobby {
    private static int count = 0;
    private final int lobbyId;
    private final int limit;
    private Set<String> players = new HashSet<>();

    /**
     * Creates a new lobby with the specified creator and player limit.
     *
     * @param creator the username of the player who creates the lobby
     * @param limit   the maximum number of players allowed in the lobby
     */
    public Lobby(String creator, int limit){
        players.add(creator);
        count++;
        this.lobbyId = count;
        this.limit = limit;
    }

    /**
     * Adds a player to the lobby if there is space and the player is not already present.
     *
     * @param joiner the username of the player who wants to join
     * @throws IllegalStateException    if the lobby is full
     * @throws IllegalArgumentException if the player is already in the lobby
     */
    public void join(String joiner){
        if(isLobbyFull()) {
            throw new IllegalStateException("La lobby " + lobbyId + " è piena!");
        } else if (players.contains(joiner)) {
            throw new IllegalArgumentException("Il giocatore " + joiner + " è già dentro!");
        } else {
            players.add(joiner);
        }
    }

    /**
     * Checks if the lobby has reached its player limit.
     *
     * @return true if the lobby is full, false otherwise
     */
    public boolean isLobbyFull(){
        return players.size() == limit;
    }

    /**
     * Removes a player from the lobby.
     *
     * @param player the username of the player to remove
     */
    public void removePlayer(String player){
        players.remove(player);
    }

    /**
     * Checks if a player is currently in the lobby.
     *
     * @param p the username of the player
     * @return true if the player is in the lobby, false otherwise
     */
    public boolean isPlayerInLobby(String p){
        return players.contains(p);
    }

    /**@return the lobby ID*/
    public int getLobbyId() {
        return lobbyId;
    }

    /**@return the player limit*/
    public int getLimit() {
        return limit;
    }

    /**@return the set of players in the lobby*/
    public Set<String> getPlayers() {
        return players;
    }

    /**
     * Sets the global lobby counter.
     *
     * @param count the new value of the static lobby counter
     */
}
