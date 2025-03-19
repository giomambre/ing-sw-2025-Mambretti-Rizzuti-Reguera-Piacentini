package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final int BOARD_SIZE = 24;
    private Map<Integer, Player> player_position = new HashMap<>();

    public Board(List<Player> players) { //mette i player nelle posizioni di partenza,
        //in caso di nave che non puo partite, tipo non ha motori, viene rimossa e il player dopo scala avanti???

        int[] starting_positions = {7, 4, 2, 1}; // the first player that end the build is the first in the active player list
        int i = 0;
        for (Player player : players) {
            player_position.put(starting_positions[i], player);
            i++;

        }

    }

    public void MovePlayer(Player p, int pos) { //pos è il numero di pos in aggiunta

        //List<Integer> keys = new ArrayList<>(player_position.keySet());
        //keys.sort(Integer::compareTo);
        int startingPosition = 0;
        for (var entry : player_position.entrySet()) {

            if (entry.getValue().equals(p)) {
                startingPosition = entry.getKey();
            }

        }
        int not_occupied_spaces = 0;
        //it represent the effective position on the board, assumptions: every cell corresponds to a number(1...24)
        if (pos > 0) {
            int i = startingPosition + 1;
            //assunzione dell'if dentro al while: in ogni momento del gioco, in player_position sono memorizzate
            // SOLO le posizioni correnti dei giocatori, quelle vecchie vengono eliminate appena sposto la pedina (come sotto)
            while (not_occupied_spaces < pos) {
                if (player_position.containsKey(i)) {
                    i++;
                } else {
                    not_occupied_spaces++;
                    i++;
                }
            }
            i--;
            if (i < BOARD_SIZE) {
                player_position.put(i, p);
                player_position.remove(startingPosition);
                return;
            } else {
                //la sottrazione è da rivedere perchè non so se sei partito a contare le caselle da 0 o da 1, se le hai numerate da 1 dovrebbe essere corretto se sei partito da 0 aggiungi un -1
                i = i - BOARD_SIZE;
                player_position.put(i, p);
                player_position.remove(startingPosition);
                return;
            }
        }
        if (pos < 0) {
            int i = startingPosition - 1;
            pos = pos * (-1);
            while (not_occupied_spaces < pos) {
                if (player_position.containsKey(i)) {
                    i--;
                } else {
                    not_occupied_spaces++;
                    i--;
                }
                if (i<0)
                    i=i+BOARD_SIZE; // lo ripristino qua per evitare casi in cui il giocatore torna indietro e ne becca uno che sta per terminare il giro
            }
            i++;

            player_position.put(i, p);
            player_position.remove(startingPosition);
            return;

            /*else {
                i = i + BOARD_SIZE + 1;
                player_position.put(i, p);
                player_position.remove(startingPosition);
                return;
            }*/
        }

    }


    public Map<Integer, Player> GetBoard() {
        return player_position;
    }

    public List<Player> GetRanking() { //restituisce la lista ordinata


        List<Integer> keys = new ArrayList<>(player_position.keySet());
        keys.sort(Integer::compareTo);
        List<Player> ranking = new ArrayList<>();
        for (Integer i : keys) {
            ranking.add(player_position.get(i));

        }
        return ranking;

    }



    public void printBoard() {
        for (var entry : player_position.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getNickname());
        }
    }


}

