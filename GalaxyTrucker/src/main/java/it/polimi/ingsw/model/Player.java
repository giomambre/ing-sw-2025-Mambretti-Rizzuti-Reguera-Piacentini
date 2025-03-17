package it.polimi.ingsw.model;

import static it.polimi.ingsw.model.Color.*;

public class Player {
    private String nickname;
    private Color color;
    private Ship ship = new Ship(this);
    private int exposed_connectors = 0;


    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;


    }

    public void EndBuild() {
        //quando un player dichiara di aver finito o finisce per il tempo
        // game lo aggiunge nella lista active_players
    }

    public String getNickname() {
        return nickname;
    }
    public Color getColor() {
        return color;
    }
}
