package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Planets extends CardAdventure {
    private List<List<Cargo>> cargo_reward;

    public Planets(int level, int cost_of_days, CardAdventureType type, Board board, List<List<Cargo>> cargo_reward) {
        super(level, cost_of_days, type, board);
        this.cargo_reward = cargo_reward;
    }

    public void execute(Map<Player, Map<CardComponent, Map<Cargo, Integer>>> players) {
        List<Cargo> totalRewards = new ArrayList<>();

        for (Player player : players.keySet()) {

            board.movePlayer(player,-getCost_of_days());// fa perdere i giorni
            Ship ship_player = player.getShip();


            for (int i = 0; i < ship_player.getROWS(); i++) {
                for (int j = 0; j < ship_player.getCOLS(); j++) {

                    CardComponent card = ship_player.getComponent(i, j);

                    if (players.get(player).containsKey(card)) {
                        Map<Cargo, Integer> rewards = players.get(player).get(card);

                        ((Storage) card).addCargo(rewards);
                    }
                }
            }
        }

    }


    public List<Cargo> getCargos(int index) {
        return cargo_reward.get(index);
    }

}
