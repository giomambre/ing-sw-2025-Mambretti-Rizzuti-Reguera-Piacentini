package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class Lobby {
    private static int count = 0;
    private final int lobbyId;
    private final int limit;
    public static List<String> players = new ArrayList<>();

    /** Creates a new lobby given a player "creator" and a limit of people that can eventually join. */
    public Lobby(String Creator, int limit){
        players.add(Creator);

        count++;
        this.lobbyId = count;
        this.limit = limit;
    }


    public void join(String joiner){
        if(isLobbyFull()) throw new InputMismatchException("The lobby " + lobbyId + " is full!");
        else if (this.players.contains(joiner)){
            throw new InputMismatchException("The player " + joiner + " is already in!");
        } else players.add(joiner);
    }


    public boolean isLobbyFull(){
        return players.size() == limit;
    }


    public void removePlayer(String player){
        players.remove(player);
    }

    public boolean isPlayerInLobby(String p){
        for (String player: this.players) {
            if(p.equals(player)){
                return true;
            }
        }
        return false;
    }

    public static int getPlayerSize() {
        return players.size();
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getPlayers() {
        return players;
    }

    public static void setCount(int count) {
        Lobby.count = count;
    }
}