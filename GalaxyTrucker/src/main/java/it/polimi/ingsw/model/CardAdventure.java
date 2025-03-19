package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public abstract class CardAdventure {
    private int level;
    private int cost_of_days; //it can be 0
    private CardAdventureType type;
    protected Board board;

   //costruttore da vedere se metterlo qui o nelle sottoclassi(classe astratta) si va fattto

    //da capire come gestire il fatto di chiedere al player se vuole accettare o rifiutare
    //sono abbastanza sicuro che debba fatto nel controller, ma il controller dovra chiamare qualche metodo
    // da fare nel player o qui????

    public CardAdventure(int level, int cost_of_days ,CardAdventureType type, Board board) {

        this.level = level;
        this.cost_of_days = cost_of_days;
        this.board = board;
        this.type = type;

    }

    public abstract void executeAdventureEffects(List<Player> players, Map<Player, Map<CardComponent, Boolean>> batteryUsageMap);

    public void startAdventure(List <Player> players) {


    }
    public void nextplayer(){

    }
    public void endadventure(){

    }

    public CardAdventureType getType() {
        return type;
    }


    public Board getBoard() {
        return board;
    }


}

