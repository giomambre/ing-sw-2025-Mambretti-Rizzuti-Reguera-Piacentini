package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.North;

/**
 * This class is a sublass of {@code CardAdventure}, from which it inherits attributes and methods
 * <ul>
 *     <li>crewmates_loss: how many crewmates the player with the weakest engines loses</li>
 *     <li>cargo_loss: how many cargo units the player with the least engine power loses</li>
 *     <li> meteors: a list of pairs, each containing a meteor type and the direction it comes from.
 *     Depending on the type of Combat Zone card, the meteors threaten either the player with the weakest cannons
 *     or the player with the fewest crew members.</li>
 * </ul>
 */
public class CombatZone extends CardAdventure implements Serializable {
    private int crewmates_loss;
    private int cargo_loss;
    private int id;
    private List<Pair<MeteorType, Direction>> meteors;

    /**
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose. Can be =0
     * @param type
     * @param crewmates_loss
     * @param cargo_loss
     * @param meteors
     */
    public CombatZone(int level, int cost_of_days, CardAdventureType type,int id, int crewmates_loss, int cargo_loss, List<Pair<MeteorType, Direction>> meteors,String path) {
        super(level, cost_of_days, type,path);
        this.crewmates_loss=crewmates_loss;
        this.cargo_loss=cargo_loss;
        this.meteors = meteors;
        this.id = id;
    }


    /**@return a list of pair of meteor type and its direction*/
    public List<Pair<MeteorType, Direction>> getMeteors() {
        return meteors;
    }

    public int getId(){
        return id;
    }

    public int getCrewmates_loss() {
        return crewmates_loss;
    }

    public int getCargo_loss() {
        return cargo_loss;
    }
}
