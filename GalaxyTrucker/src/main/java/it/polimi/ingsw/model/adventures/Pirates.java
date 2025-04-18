package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.MeteorType;
import javafx.util.Pair;

import java.util.List;

public class Pirates extends CardAdventure{
    private int cannons_strenght;
    private int credits;
    List<Pair<MeteorType, Direction>> meteors;

    public Pirates(int level, int cost_of_days, CardAdventureType type, int cannons_strenght, int credits, List<Pair<MeteorType, Direction>> meteors ) {
        super(level, cost_of_days, type);
        this.cannons_strenght = cannons_strenght;
        this.credits=credits;
        this.meteors = meteors;
    }

    public void executeWin(Player player) {
        board.movePlayer(player, -getCost_of_days());
        player.receiveCredits(credits);
    }

    //executeloss gestito da meteorswarm
    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }

    public int getCannons_strenght() {
        return cannons_strenght;
    }

    public int getCredits() {
        return credits;
    }
}
