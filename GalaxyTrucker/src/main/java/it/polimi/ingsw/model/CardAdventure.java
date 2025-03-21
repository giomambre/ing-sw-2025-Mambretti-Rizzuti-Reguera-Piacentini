package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class contains some common methods between the 8 different CardAdventure
 * It's the direct father of the following:
 * <ul>
 *     <li>{@link OpenSpace}
 *     <li>{@link AbandonedStation}
 *     <li>{@link AbandonedShip}
 *     <li>{@link Planets}
 *     <li>{@link Smugglers}
 *     <li>{@link CombatZone}
 *     <li>{@link MeteorSwarm}
 *     <li>{@link Stardust}
 *     <li>{@link Epidemic}
 *
 * </ul>
 */
public abstract class CardAdventure {
    private int level;
    private int cost_of_days; //it can be 0
    private CardAdventureType type;
    protected Board board;



    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be =0
     * @param type
     * @param board
     */
    public CardAdventure(int level, int cost_of_days ,CardAdventureType type, Board board) {

        this.level = level;
        this.cost_of_days = cost_of_days;
        this.board = board;
        this.type = type;

    }

    public abstract void execute();

    public void startAdventure(List <Player> players) {


    }

    /**
     * This method is called when a card needs to be passed to another player. The order is decreasing, starting to the group leader,
     * based on the position of the rockets on the board.
     */


    public CardAdventureType getType() {
        return type;
    }

    public int getCost_of_days() {
        return cost_of_days;
    }

    public Board getBoard() {
        return board;
    }


}



