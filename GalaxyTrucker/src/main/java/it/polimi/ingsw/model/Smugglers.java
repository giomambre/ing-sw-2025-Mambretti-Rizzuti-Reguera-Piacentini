package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public class Smugglers extends CardAdventure {
    private int cannons_strenght;
    private List<Storage> storage_rewards;
    private int storage_loss;

    /**
     * @param level        must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     */
    public Smugglers(int level, int cost_of_days, CardAdventureType type, Board board, int cannons_strenght, List<Storage> storage_rewards, int storage_loss) {
        super(level, cost_of_days, type, board);
        this.cannons_strenght = cannons_strenght;
        this.storage_rewards = storage_rewards;
        this.storage_loss = storage_loss;
    }

    public void executeWin(Player player, Map<CardComponent, Map<Cargo, Integer>> new_cargo_position, Boolean choice) {
        if (choice) {
            Ship ship_player = player.getShip();
            board.movePlayer(player, -getCost_of_days());
            for (int i = 0; i < ship_player.getROWS(); i++) {
                for (int j = 0; j < ship_player.getCOLS(); j++) {

                    CardComponent card = ship_player.getComponent(i, j);
                    for (CardComponent storage : new_cargo_position.keySet()) {

                        if (card.equals(storage)) {
                            ((Storage) storage).addCargo(new_cargo_position.get(storage));
                        }


                    }
                }

            }
        }

    }

    public void executeLoss(Player player, Map<CardComponent, Map<Cargo, Integer>> new_cargo_position) {
        Ship ship_player = player.getShip();
        board.movePlayer(player, -getCost_of_days());
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {

                CardComponent card = ship_player.getComponent(i, j);
                for (CardComponent storage : new_cargo_position.keySet()) {

                    if (card.equals(storage)) {
                        ((Storage) storage).removeCargo(new_cargo_position.get(storage));
                    }


                }
            }


        }
    }
}

