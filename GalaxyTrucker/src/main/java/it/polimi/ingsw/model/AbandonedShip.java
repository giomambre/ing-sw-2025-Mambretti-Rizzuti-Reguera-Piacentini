package it.polimi.ingsw.model;

public class AbandonedShip extends CardAdventure{
    int given_credits;
    int crewmates_loss;

    public AbandonedShip(int level, int cost_of_days,int given_credits, int crewmates_loss) {
        super(level, cost_of_days);
        this.given_credits = given_credits;
        this.crewmates_loss = crewmates_loss;
    }
}
