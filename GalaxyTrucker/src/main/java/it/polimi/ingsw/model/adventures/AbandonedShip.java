package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.util.Map;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>given_credits: how many credits the player receives, as specified on the card</li>
 *     <li>crewmates_loss: how many crewmates (human and/or alien) the player loses when choosing to use the card</li>
 * </ul>
 */
public class AbandonedShip extends CardAdventure {
    private int given_credits;
    private int crewmates_loss;

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     * @param given_credits
     * @param crewmates_loss
     */
    public AbandonedShip(int level, int cost_of_days, CardAdventureType type, Board board, int given_credits, int crewmates_loss) {
        super(level, cost_of_days,type ,board);
        this.given_credits = given_credits;
        this.crewmates_loss = crewmates_loss;
        this.board = board;
    }


    /**
     * This method is called when a player has enought crewmates to use this card and decides to use it.
     * It allows the player to receive given_credits credits, calling the 'given_credits' function of player.
     * Then it moves the player back by cost_of_days positions through the 'movePlayer' function of board.
     * At the end this method removes crewmates_loss crewmates from the ship.
     *
     *
     * @param player
     * @param astronaut_losses
     */


    public void execute(Player player, Map<CardComponent,Integer> astronaut_losses) {

        Ship ship_player = player.getShip();
        player.receiveCredits(given_credits);
        board.movePlayer(player,- getCost_of_days());

        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {
                CardComponent component = ship_player.getComponent(i, j);

                    for (CardComponent unit : astronaut_losses.keySet()) {
                        if (component.equals(unit)){

                            ((LivingUnit) component).removeCrewmates(astronaut_losses.get(unit)); // occhio al cast Exception

                        }
                    }
                }
            }
}



}
