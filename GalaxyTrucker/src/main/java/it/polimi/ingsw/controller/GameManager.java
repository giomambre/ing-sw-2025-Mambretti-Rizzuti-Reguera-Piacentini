package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

public class GameManager {

    List<Lobby> all_lobbies = new ArrayList<>();



    public GameManager() {
    }


    public int createLobby(String nickname, int limit) {

        for (Lobby lobby : all_lobbies) {
            if (lobby.isPlayerInLobby(nickname)) {
                throw new IllegalArgumentException("Player already in a lobby");
            }
        }

        Lobby l = new Lobby(nickname, limit);
        all_lobbies.add(l);
        return l.getLobbyId();

    }


    public void joinLobby(String nickname, int id) {

        for (Lobby lobby : all_lobbies) {
            if (lobby.isPlayerInLobby(nickname)) {
                throw new IllegalArgumentException("Player already in a lobby");
            }
            if (lobby.getLobbyId() == id) {
                lobby.join(nickname);
                return;
            }

        }
    }


    public List<Integer> getAvaibleLobbies() {
        List<Integer> avaibleLobbies = new ArrayList<>();
        for (Lobby lobby : all_lobbies)

            if (!lobby.isLobbyFull()) {
                avaibleLobbies.add(lobby.getLobbyId());
            }
        return avaibleLobbies;
    }


    public void startGame(int id_lobby) {


    }

    public void disconnectPlayer(String nickname) {
        for (Lobby lobby : all_lobbies) {
            if (lobby.isPlayerInLobby(nickname)) {
                lobby.removePlayer(nickname);
            }
        }
    }

public List<Lobby> getAllLobbies() {
        return all_lobbies;
}

    public Lobby getLobby(int id) {
        for (Lobby lobby : all_lobbies) {
            if (lobby.getLobbyId() == id) {
                return lobby;
            }
        }
        return null;
    }

}
