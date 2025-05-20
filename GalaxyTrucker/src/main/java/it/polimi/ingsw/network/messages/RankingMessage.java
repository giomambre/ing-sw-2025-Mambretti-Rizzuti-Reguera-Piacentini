package it.polimi.ingsw.network.messages;

import java.util.Map;

public class RankingMessage extends Message {

    Map<String,Integer> ranks;
    public RankingMessage(MessageType type, String content, Map<String,Integer> ranks) {
        super(type, content);
        this.ranks = ranks;
    }
    public Map<String,Integer> getRanks(){

        return ranks;
    }

    public String getWeakerPlayer(){

        int less = 100;
        String player = "";
        for(Map.Entry<String,Integer> entry : ranks.entrySet()){
            if(entry.getValue() < less){
                less = entry.getValue();
                player = entry.getKey();
            }
        }
        return player;


    }

}
