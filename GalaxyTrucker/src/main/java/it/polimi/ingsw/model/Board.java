package it.polimi.ingsw.model;

import java.util.*;

public class Board {
    private final int BOARD_SIZE = 24;
    private  Map<Integer, Player> player_position = new HashMap<>();
    public void MovePlayer(Player p,int pos){ //pos è il numero di pos in aggiunta

        List<Integer> keys = new ArrayList<>(player_position.keySet());
        keys.sort(Integer::compareTo);
        int startingPosition = 0;
        for(var entry : player_position.entrySet()){

            if(entry.getValue().equals(p)){
                startingPosition = entry.getKey();
            }

        }
        for(int i = 0 ; i < pos ; i++){

            System.out.println("da fare");


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

