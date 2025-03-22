package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public class AbandonedShip extends CardAdventure {
    private int given_credits;
    private int crewmates_loss;

    public AbandonedShip(int level, int cost_of_days, CardAdventureType type, Board board, int given_credits, int crewmates_loss) {
        super(level, cost_of_days,type ,board);
        this.given_credits = given_credits;
        this.crewmates_loss = crewmates_loss;
        this.board = board;
    }





    public void execute(Player player, Map<CardComponent,Integer> astronaut_losses) {

        Ship ship_player = player.getShip();
        player.reciveCredits(given_credits);
        board.MovePlayer(player,- getCost_of_days());

        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {
                CardComponent component = ship_player.getComponent(i, j);

                    for (CardComponent unit : astronaut_losses.keySet()) {
                        if (component.equals(unit)){

                            ((LivingUnit) component).RemoveCrewmates(astronaut_losses.get(unit)); // occhio al cast Exception

                        }
                    }
                }
            }
}



}
