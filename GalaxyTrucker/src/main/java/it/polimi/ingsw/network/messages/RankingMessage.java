package it.polimi.ingsw.network.messages;

import java.util.Map;

public class RankingMessage extends Message {

    Map<String,Double> ranks;
    public RankingMessage(MessageType type, String content, Map<String,Double> ranks) {
        super(type, content);
        this.ranks = ranks;
    }
    public Map<String,Double> getRanks(){

        return ranks;
    }

    public String getWeakerPlayer(){

        Double less = 100.0;
        String player = "";
        for(Map.Entry<String,Double> entry : ranks.entrySet()){
            if(entry.getValue() < less){
                less = entry.getValue();
                player = entry.getKey();
            }
        }
        return player;


    }

}
