package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class Lobby {
    private static int count = 0;
    private final int lobbyId;
    //io metterei direttamente 4 come limit fissato (isa), se approvi il cambiamento faccio le modifiche
    private final int limit;
    public List<String> Players = new ArrayList<>();

    /** Creates a new lobby given a player "creator" and a limit of people that can eventually join. */
    public Lobby(String Creator, int limit){
        Players.add(Creator);

        count++;
        this.lobbyId = count;
        this.limit = limit;
    }


    public void join(String Joiner){
        if(isLobbyFull()) throw new InputMismatchException("The lobby " + lobbyId + " is full!");
        else if (this.Players.contains(Joiner)){
            throw new InputMismatchException("The player " + Joiner + " is already in!");
        } else Players.add(Joiner);
    }


    public boolean isLobbyFull(){
        return Players.size() == limit;
    }


    public void removePlayer(String player){
        Players.remove(player);
    }

    public boolean isPlayerInLobby(String p){
        for (String player: this.Players) {
            if(p.equals(player)){
                return true;
            }
        }
        return false;
    }

    public static int getCount() {
        return count;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getPlayers() {
        return Players;
    }

    public static void setCount(int count) {
        Lobby.count = count;
    }
}