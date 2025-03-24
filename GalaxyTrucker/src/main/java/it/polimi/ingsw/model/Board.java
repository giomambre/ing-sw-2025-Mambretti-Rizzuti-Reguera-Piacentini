package it.polimi.ingsw.model;

import java.util.*;

/**
 * this class represents the game board and all the logic regarding the movement of the rockets on it
 * <ul>
 *      <li>BOARD_SIZE= 24
 *      <li>player_position: for each cell on the map return the player who's on it, null if it's empty
 *      <li>board_leader: the current player who's the first on the ranking based on the positions on the map
 * </ul>
 */

public class Board {
    private final int BOARD_SIZE = 24;
    private Map<Integer, Player> player_position = new HashMap<>();
    private Player board_leader;

    public Board(List<Player> players) { //mette i player nelle posizioni di partenza,
        //in caso di nave che non puo partite, tipo non ha motori, viene rimossa e il player dopo scala avanti???

        int[] starting_positions = {7, 4, 2, 1}; // the first player that end the build is the first in the active player list
        int i = 0;
        for (Player player : players) {
            player_position.put(starting_positions[i], player);
            i++;

        }

    }

    public Player getBoard_leader() {
        return board_leader;
    }

    /**
     * this method is called to eventually change the board leader.
     * @see Board checkleader method used at the beginning to check who's the leader
     */
    public void changeBoard_leader() {
        Player new_leader = checkLeader(player_position);
        this.board_leader = new_leader;
        return;
    }

    /**
     * this method is called to check who's the current board leader on the board that is passed as parameter
     * @param player_position The map on which we want to find out who's the leader at the moment
     * @return returns the current leader
     */
    public Player checkLeader(Map<Integer, Player> player_position) {
        List<Player> players = new ArrayList<>(player_position.values());

        //a player is the board_leader based on #laps if he is the maybe_leader and is the only possible candidate (possible_leaders.size()==1)
        Player maybe_leader = players.get(0);
        List<Player> possible_leaders = new ArrayList<>();
        possible_leaders.add(maybe_leader);

        // first of all find the player who did the most number of laps
        for (int i = 1; i < players.size(); i++) {
            if (players.get(i).getNum_laps() > maybe_leader.getNum_laps()) {
                possible_leaders.clear();
                possible_leaders.add(players.get(i));
                maybe_leader = players.get(i);
            }
            if (players.get(i).getNum_laps() == maybe_leader.getNum_laps()) {
                possible_leaders.add(players.get(i));
            }
        }
        if (possible_leaders.size() == 1) {
            return maybe_leader;

        } else {
            //the keys of the HM are the positions of the players
            List<Integer> positions = new ArrayList<>(player_position.keySet());
            while (positions.size() > 0) {
                //let's find out the max position
                int max = positions.get(0);
                for (int i = 0; i < positions.size(); i++) {
                    if (positions.get(i) > max) {
                        max = positions.get(i);
                    }
                }
                if (possible_leaders.contains(player_position.get(max))) {
                    return player_position.get(max);
                } else {
                    positions.remove(max);
                }
            }

        }
        //non dovrebbe mai succedere...
        return null;
    }


    /**
     * This method moves the player p on the board of n=pos positions.
     * <ul></ul>Then update his position in player_positions and deletes the old one.
     * @param p the player that needs to move his rocket
     * @param pos the number of position gained (if pos>0) or lost (if pos<0)
     */
    public void MovePlayer(Player p, int pos) { //pos è il numero di pos in aggiunta
        int startingPosition = 0;
        for (var entry : player_position.entrySet()) {

            if (entry.getValue().equals(p)) {
                startingPosition = entry.getKey();
            }
        }
        player_position.remove(startingPosition);
        int not_occupied_spaces = 0;
        //it represents the effective position on the board, assumptions: every cell corresponds to a number(1...24)
        if (pos > 0) {
            int i = startingPosition + 1;

            while (not_occupied_spaces < pos) {
                if (player_position.containsKey(i%BOARD_SIZE)) {
                    i++;
                } else {
                    not_occupied_spaces++;
                    i++;
                }
            }
            i--;
            if(i < BOARD_SIZE) {
                player_position.put(i, p);
                //changeBoard_leader();
                return;
            } else {
                i=i%BOARD_SIZE;
                System.out.println("int i:"+i);
                player_position.put(i, p);
                //p.addLap();
                //changeBoard_leader();
                return;
            }
        }
        if (pos < 0) {
            int i = startingPosition - 1;
            pos = pos * (-1);
            while (not_occupied_spaces < pos) {
                if (player_position.containsKey(i)){
                    i--;
                } else {
                    not_occupied_spaces++;
                    i--;
                }
            }
            i++;
            if (i>0) {
                player_position.put(i, p);
                //changeBoard_leader();
                return;
            }
            if (i < 0) {
                i = i + BOARD_SIZE+1;
                //p.subLap();
                player_position.put(i, p);
                //player_position.remove(startingPosition);
                //changeBoard_leader();
                return;

            }

        }
    }


    public Map<Integer, Player> GetBoard() {
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
    public List<Player> GetRanking() {
        List<Player> ranking = new ArrayList<>();
        HashMap<Integer, Player> tmp_player_position = new HashMap<>();
        tmp_player_position.putAll(player_position);
        Player tmp_player;
        while (tmp_player_position.size() > 0) {
            tmp_player=checkLeader(tmp_player_position);
            ranking.add(tmp_player);
            for (var entry : tmp_player_position.entrySet()) {
                if (entry.getValue().equals(tmp_player)) {
                    tmp_player_position.remove(entry.getKey());
                }
            }
        }
        return ranking;
    }



    public void printBoard() {
        for (var entry : player_position.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getNickname());
        }
    }


}
