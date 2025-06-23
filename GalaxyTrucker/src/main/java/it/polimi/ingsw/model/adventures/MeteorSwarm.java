package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>meteors: a list in which each element is a pair consisting of a value of type meteorType and one of type direction</li>
 * </ul>
 */
public class MeteorSwarm extends CardAdventure implements Serializable {

    private List<Pair<MeteorType, Direction>> meteors;

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param meteors
     */
    public MeteorSwarm(int level, int cost_of_days, CardAdventureType type, List<Pair<MeteorType, Direction>> meteors, String path) {
        super(level, cost_of_days, type, path);
        this.meteors = meteors;
    }


    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }


}
