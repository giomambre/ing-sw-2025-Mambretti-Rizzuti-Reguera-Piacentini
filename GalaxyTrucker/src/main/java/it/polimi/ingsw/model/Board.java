package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.*;

/**
 * this class represents the game board and all the logic regarding the movement of the rockets on it
 * <ul>
 *      <li>board_size could be 24 (Game) or 18 (QuickGame)
 *      <li>player_position: for each cell on the map return the player who's on it, null if it's empty
 *      <li>board_leader: the current player who's the first on the ranking based on the positions on the map
 * </ul>
 */

public class Board implements Serializable {
    private int board_size;
    private Map<Integer, Player> player_position = new HashMap<>();
    private Player board_leader;
    private transient BaseGame game;
    public Board(int board_size, BaseGame game) {
        this.board_size = board_size;
        this.game = game;
    }

    public void putPlayersOnBoard(List<Player> players){
        int[] starting_positions = {7, 4, 2, 1};
        int i = 0;
        for (Player player : players) {
            player_position.put(starting_positions[i], player);
            i++;
        }
    }


    /**
     * this method is called to eventually change the board leader.
     *
     * @see Board checkleader method used at the beginning to check who's the leader
     */
    public void changeBoard_leader() {
        this.board_leader = getRanking().getFirst();
    }



    /**
     * @param p player
     * @return position of player p
     */
    public int getPlayerPosition(Player p) {

        for (var entry : player_position.entrySet()) {
            if (entry.getValue().equals(p)) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * This method moves the player p on the board of n=pos positions.
     * <ul></ul>Then update his position in player_positions and deletes the old one.
     *
     * @param p the player that needs to move his rocket
     * @param pos the number of position gained (if pos>0) or lost (if pos<0)
     */
    public void movePlayer(Player p, int pos) {
        int starting_position = -1;

        if(pos == 0) return;

        for (var entry : player_position.entrySet()) {
            if (entry.getValue().equals(p)) {
                starting_position = entry.getKey();
                break;
            }
        }

        player_position.remove(starting_position);

        int newPosition;

        if (pos >= 0) {
            int i = starting_position;
            int spaces_traversed = 0;

            while (spaces_traversed < pos) {
                i = i + 1;
                if (i > board_size) {
                    i = 1; // Reset to position 1 when exceeding board_size (24)
                }
                if (!player_position.containsKey(i)) {
                    spaces_traversed++;
                }
            }

            newPosition = i;
            if (newPosition <= starting_position) {
                p.addLap();
            }

        } else {
            int i = starting_position;
            int spacesToMove = -pos;
            int spaces_traversed = 0;

            while (spaces_traversed < spacesToMove) {
                i = i - 1;
                if (i < 1) {
                    i = board_size;
                }
                if (!player_position.containsKey(i)) {
                    spaces_traversed++;
                }
            }

            newPosition = i;

            if (newPosition > starting_position) {
                p.subLap();
            }
        }

        player_position.put(newPosition, p);
        lappedPlayers();

        changeBoard_leader();
    }

    /**
     * @return a map of player positions
     */
    public Map<Integer, Player> getBoard() {
        return player_position;
    }

    /**
     * this method returns the ranking of the players based on their position on the board
     * @return it returns a sorted list based of the positions (starting with the first and going on)
     * <ul>
     *     <li>ranking.get(0)= first player on the board
     *     <li>ranking.get(1)= second player on the board
     *     <li>and so on...
     * </ul>
     */
    public List<Player> getRanking() {
        return player_position.entrySet().stream()
                .sorted((a, b) -> {
                    Player p1 = a.getValue();
                    Player p2 = b.getValue();

                    int giri_compare = Integer.compare(p2.getNum_laps(), p1.getNum_laps());
                    if (giri_compare != 0) {
                        return giri_compare;
                    }

                    return Integer.compare(b.getKey(), a.getKey());
                })
                .map(Map.Entry::getValue)
                .toList();
    }


    /**
     * This function removes the players that got lapped by the group leader.
     * They are removed to the list of active players.
     */
    public void lappedPlayers() {
        List<Player> players = new ArrayList<>(getRanking());

        Player leader = players.get(0);
        List<Player> active_players = game.getActivePlayers();

        for (int i = 1; i < players.size(); i++) {
            Player player = players.get(i);

            if(leader.getNum_laps() > player.getNum_laps() && getPlayerPosition(leader)>getPlayerPosition(player)) {
                active_players.remove(player);
            }
        }

        game.setActivePlayers(active_players);
    }


    public Map<Integer, Player> copyPlayerPositions() {
        Map<Integer, Player> copy = new HashMap<>();
        for (Map.Entry<Integer, Player> entry : this.player_position.entrySet()) {
            // Assumiamo che Player abbia un costruttore di copia
            copy.put(entry.getKey(), entry.getValue().copyPlayer());
        }
        return copy;
    }

    public Map<Integer, Player> copyLaps() {
        Map<Integer, Player> copy = new HashMap<>();
        for (Map.Entry<Integer, Player> entry : this.player_position.entrySet()) {
            Player originalPlayer = entry.getValue();
            Player copiedPlayer = entry.getValue().copyPlayer();
            copiedPlayer.setNum_laps(originalPlayer.getNum_laps());
            copy.put(entry.getKey(), copiedPlayer);
        }
        return copy;
    }

}








