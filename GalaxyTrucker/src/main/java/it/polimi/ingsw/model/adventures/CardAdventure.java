package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.util.List;

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
    protected boolean face_down;



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
        this.face_down = true;

    }


    public void startAdventure(List <Player> players) {


    }


    public CardAdventureType getType() {
        return type;
    }

    public int getCost_of_days() {
        return cost_of_days;
    }

    public Board getBoard() {
        return board;
    }

    //da gestire la logica che le carte avventura si girano una a una quando si risolvono, è una cazzzata ma va fatto
    public void changeFace(){this.face_down = !this.face_down;}


}



