package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

public class CombatZone extends CardAdventure{
    private int num_of_crewmates;
    private int engine_power;
    public int cannon_power;


    public CombatZone(int level, int cost_of_days, CardAdventureType type, Board board, int num_of_crewmates, int engine_power, int cannon_power) {
        super(level, cost_of_days, type, board);
        this.num_of_crewmates = num_of_crewmates;
        this.engine_power = engine_power;
        this.cannon_power = cannon_power;
    }
}
