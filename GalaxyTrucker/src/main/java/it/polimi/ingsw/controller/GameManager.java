package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Lobby;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    List<Lobby> all_lobbies = new ArrayList<>();

    public GameManager() {}

    /**
     * Creates the instance and the correspondent id
     * The id is incremental
     * @param nickname
     * @param limit
     * @return
     */
    public int createLobby(String nickname, int limit) {
        for (Lobby lobby : all_lobbies) {
            if (lobby.isPlayerInLobby(nickname)) {
                throw new IllegalArgumentException("Il giocatore è già in una lobby");
            }
        }

        Lobby l = new Lobby(nickname, limit);
        all_lobbies.add(l);
        return l.getLobbyId();
    }

    /**
     * A player is added to the Lobby, one player cannot be in more than 1 lobby
     * @param nickname
     * @param id
     */
    public void joinLobby(String nickname, int id) {
        for (Lobby lobby : all_lobbies) {
            if (lobby.isPlayerInLobby(nickname)) {
                throw new IllegalArgumentException("Il giocatore è già in una lobby");
            }
            if (lobby.getLobbyId() == id) {
                lobby.join(nickname);
                return;
            }
        }
    }

    /**
     * It returns all the lobby Ids where the Limit is not reached
     * @return
     */
    public List<Integer> getAvaibleLobbies() {
        List<Integer> avaibleLobbies = new ArrayList<>();
        for (Lobby lobby : all_lobbies)
            if (!lobby.isLobbyFull()) {
                avaibleLobbies.add(lobby.getLobbyId());
            }
        return avaibleLobbies;
    }


    public List<Lobby> getAllLobbies() {
        return all_lobbies;
    }


    /**
     * From the lobby id to the Lobby object
     * @param id
     * @return
     */
    public Lobby getLobby(int id) {
        for (Lobby lobby : all_lobbies) {
            if (lobby.getLobbyId() == id) {
                return lobby;
            }
        }
        return null;
    }
}
