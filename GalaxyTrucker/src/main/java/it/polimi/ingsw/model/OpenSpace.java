package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class OpenSpace extends  CardAdventure{
    public OpenSpace(int level, int cost_of_days) {
        super(level, cost_of_days);
    }

    public List<Integer> calculatePlayersPower(List<Player> players) { //returns a list with the player and thei relative enigne power, in
        // the game class has to travel it in inverse order
        List<Integer> powers = new ArrayList<>();
        for (Player player : players) {
            powers.add(player.getShip().calculateEnginePower()); //manca da gestire la roba delle batteria
        }

    }

}
