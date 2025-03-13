package it.polimi.ingsw.model;
import static  it.polimi.ingsw.model.Color.*;

public class Player {
    private String nickname;
    private Color color;
    private Ship ship = new Ship();
    private int exposed_connectors = 0;


    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;


    }
    public String getNickname() {
        return nickname;
    }
}
