package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>cannons_strenght: the cannon power of card
 *     <li>astronaut_loss: the number of crewmates lost by the player who is defeated</li>
 *     <li>credits: the reward granted to the player who defeats the pirates</li>
 * </ul>
 */
public class Slavers extends CardAdventure implements Serializable {
    private int cannons_strenght;
    private int astronaut_loss;
    private int credits;

    /**
     * @param level
     * @param cost_of_days
     * @param type
     * @param cannons_strenght
     * @param astronaut_loss
     * @param credits the reward in credits for defeating the pirates
     */
    public Slavers(int level, int cost_of_days, CardAdventureType type, int cannons_strenght, int astronaut_loss, int credits,String path) {
        super(level, cost_of_days, type,path);
        this.cannons_strenght = cannons_strenght;
        this.astronaut_loss = astronaut_loss;
        this.credits=credits;
    }


    /** @return the cannon strength*/
    public int getCannons_strenght() {
        return cannons_strenght;
    }

    /** @return the number of lost crew members*/
    public int getAstronaut_loss() {
        return astronaut_loss;
    }

    /** @return the credit reward*/
    public int getCredits() {
        return credits;
    }
}
