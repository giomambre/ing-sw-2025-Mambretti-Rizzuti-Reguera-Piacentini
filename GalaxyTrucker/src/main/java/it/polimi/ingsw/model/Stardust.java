package it.polimi.ingsw.model;

import com.sun.jdi.connect.Connector;

import java.util.List;
import java.util.Set;

public class Stardust extends CardAdventure{

    public Stardust(int level, int cost_of_days, CardAdventureType type, Board board) {
        super(level, cost_of_days, type, board);
    }

    public void execute(Player player, Ship ship) {
        board.movePlayer(player, -ship.calculateExposedConnectors());
    }
}
