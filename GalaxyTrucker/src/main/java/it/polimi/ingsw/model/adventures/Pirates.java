package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.MeteorType;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>cannons_strenght: the cannon power of card
 *     <li>credits: the reward granted to the player who defeats the pirates</li>
 *     <li>meteors: a list of meteor types and directions representing cannon shots on defeat</li>
 * </ul>
 */
public class Pirates extends CardAdventure implements Serializable {
    private int cannons_strenght;
    private int credits;
    List<Pair<MeteorType, Direction>> meteors;

    /**
     * @param level
     * @param cost_of_days
     * @param type
     * @param cannons_strenght
     * @param credits the reward in credits for defeating the pirates
     * @param meteors
     */
    public Pirates(int level, int cost_of_days, CardAdventureType type, int cannons_strenght, int credits, List<Pair<MeteorType, Direction>> meteors, String path ) {
        super(level, cost_of_days, type,path);
        this.cannons_strenght = cannons_strenght;
        this.credits=credits;
        this.meteors = meteors;
    }



    /** @return a list of meteor type and direction pairs*/

    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }

    /** @return the cannon strength*/
    public int getCannons_strenght() {
        return cannons_strenght;
    }

    /** @return the credit reward*/
    public int getCredits() {
        return credits;
    }
}
