package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.util.Map;

public class Slavers extends CardAdventure {
    private int cannons_strenght;
    private int astronaut_loss;
    private int credits;
    
    public Slavers(int level, int cost_of_days, CardAdventureType type, int cannons_strenght, int astronaut_loss, int credits) {
        super(level, cost_of_days, type);
        this.cannons_strenght = cannons_strenght;
        this.astronaut_loss = astronaut_loss;
        this.credits=credits;
    }
    
    public void executeWin(Player player) {
        board.movePlayer(player, -getCost_of_days());
        player.receiveCredits(credits);
    }
    
    public void executeLoss(Player player, Map<CardComponent,Integer> astronaut_losses) {

        Ship ship_player = player.getShip();
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {
                CardComponent component = ship_player.getComponent(i, j);

                for (CardComponent unit : astronaut_losses.keySet()) {
                    if (component.equals(unit)) {

                        ((LivingUnit) component).removeCrewmates(astronaut_losses.get(unit)); // occhio al cast Exception

                    }
                }
            }
        }
    }

    public int getCannons_strenght() {
        return cannons_strenght;
    }

    public int getAstronaut_loss() {
        return astronaut_loss;
    }

    public int getCredits() {
        return credits;
    }
}
