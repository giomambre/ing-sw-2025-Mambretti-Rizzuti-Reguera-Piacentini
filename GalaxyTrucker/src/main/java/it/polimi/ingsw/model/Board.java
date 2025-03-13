package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final int BOARD_SIZE = 24;
    private  Map<Integer, Player> player_position = new HashMap<>();

    public void MovePlayer(Player p,int pos){ //pos è il numero di pos in aggiunta

        //List<Integer> keys = new ArrayList<>(player_position.keySet());
        //keys.sort(Integer::compareTo);
        int startingPosition = 0;
        for(var entry : player_position.entrySet()){

            if(entry.getValue().equals(p)){
                startingPosition = entry.getKey();
            }

        }
        int not_occupied_spaces = 0;
        //i represent the effective position on the board, assumptions: every cell corresponds to a number(1...24)
        int i=startingPosition+1;
        //assunzione dell'if dentro al while: in ogni momento del gioco, in player_position sono memorizzate SOLO le posizioni correnti dei giocatori, quelle vecchie vengono eliminate appena sposto la pedina (come sotto)
        while(not_occupied_spaces<pos){
            if(player_position.containsKey(i)){
                i++;
            }
            else{
                not_occupied_spaces++;
                i++;
            }
        }
        i--;
        if(i<BOARD_SIZE) {
            player_position.put(i, p);
            player_position.remove(startingPosition);
            return;
        }
        else {
            //la sottrazione è da rivedere perchè non so se sei partito a contare le caselle da 0 o da 1, se le hai numerate da 1 dovrebbe essere corretto se sei partito da 0 aggiungi un -1
            i=i-BOARD_SIZE;
            player_position.put(i, p);
            player_position.remove(startingPosition);
            return;
        }
    }


    public Map<Integer,Player> GetBoard(){
        return player_position;
    }
    public List<Player> GetRanking(){ //restituisce la lista ordinata


        List<Integer> keys = new ArrayList<>( player_position.keySet() );
        keys.sort(Integer::compareTo);
        List<Player> ranking = new ArrayList<>();
        for(Integer i : keys){
            ranking.add(player_position.get(i));

        }
    return ranking;

    }

}

