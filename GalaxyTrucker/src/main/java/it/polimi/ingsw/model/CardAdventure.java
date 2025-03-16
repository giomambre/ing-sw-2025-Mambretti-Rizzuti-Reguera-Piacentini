package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public abstract class CardAdventure {
    private int level;
    private int cost_of_days; //it can be 0


   //costruttore da vedere se metterlo qui o nelle sottoclassi(classe astratta) si va fattto

    //da capire come gestire il fatto di chiedere al player se vuole accettare o rifiutare
    //sono abbastanza sicuro che debba fatto nel controller, ma il controller dovra chiamare qualche metodo
    // da fare nel player o qui????

    public CardAdventure(int level, int cost_of_days) {}

    public void startAdventure(List <Player> players) {}
    public void nextplayer(){

    }
    public void endadventure(){

    }

}

