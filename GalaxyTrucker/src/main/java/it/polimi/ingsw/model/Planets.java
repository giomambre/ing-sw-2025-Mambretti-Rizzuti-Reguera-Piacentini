package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Planets extends CardAdventure{
    List<List<Cargo>> cargo_reward;

    public Planets(int level, int cost_of_days, CardAdventureType type, Board board, List<List<Cargo>> cargo_reward) {
        super(level, cost_of_days,type ,board);
        this.cargo_reward = cargo_reward;
    }

    public List<Cargo> givePlayerRewards(Player player, int num_planet) {
        return cargo_reward.get(num_planet);
    }

}
