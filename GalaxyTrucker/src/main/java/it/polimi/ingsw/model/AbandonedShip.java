package it.polimi.ingsw.model;

import java.util.List;

public class AbandonedShip extends CardAdventure {
    int given_credits;
    int crewmates_loss;

    public AbandonedShip(int level, int cost_of_days, CardAdventureType type, Board board, int given_credits, int crewmates_loss) {
        super(level, cost_of_days,type ,board);
        this.given_credits = given_credits;
        this.crewmates_loss = crewmates_loss;
    }


    public void removeCrewmates(Player player, List<CardComponent[][]> living_unit_positions) {


    }

    @Override
    public void execute() {

    }
}
