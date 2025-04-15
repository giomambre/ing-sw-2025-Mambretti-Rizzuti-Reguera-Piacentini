package it.polimi.ingsw.model;

import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private static int count = 0;
    private final int lobbyId;
    private final int limit;
    private Set<String> players = new HashSet<>();

    /** Crea una nuova lobby data un "creatore" e il limite di persone che possono unirsi. */
    public Lobby(String creator, int limit){
        players.add(creator);
        count++;
        this.lobbyId = count;
        this.limit = limit;
    }

    public void join(String joiner){
        if(isLobbyFull()) {
            throw new IllegalStateException("La lobby " + lobbyId + " è piena!");
        } else if (players.contains(joiner)) {
            throw new IllegalArgumentException("Il giocatore " + joiner + " è già dentro!");
        } else {
            players.add(joiner);
        }
    }

    public boolean isLobbyFull(){
        return players.size() == limit;
    }

    public void removePlayer(String player){
        players.remove(player);
    }

    public boolean isPlayerInLobby(String p){
        return players.contains(p);
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public int getLimit() {
        return limit;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public static void setCount(int count) {
        Lobby.count = count;
    }
}
