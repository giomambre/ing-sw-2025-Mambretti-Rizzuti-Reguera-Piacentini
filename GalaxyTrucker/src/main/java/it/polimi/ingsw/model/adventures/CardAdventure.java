package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.enumerates.CardAdventureType;

import java.io.Serializable;
import java.util.List;

/**
 * This class contains some common methods between the 8 different CardAdventure
 * It Represents a generic adventure card in the game.
 * Cards are meant to be revealed (flipped face up) one at a time when resolved.
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
 * </ul>
 */
public abstract class CardAdventure implements Serializable {
    private int level;
    private int cost_of_days; //it can be 0
    private CardAdventureType type;
    protected Board board;
    protected boolean face_down;

    /**
     *
     * @param level must be level 1 or 2
     * @param cost_of_days indicates how many position in the board the player will lose if he uses the card. Can be = 0
     * @param type
     */
    public CardAdventure(int level, int cost_of_days ,CardAdventureType type) {

        this.level = level;
        this.cost_of_days = cost_of_days;
        this.type = type;
        this.face_down = true;

    }

    /**
     * Sets the game board associated with this adventure card.
     *
     * @param board the board to be set
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Starts the resolution of this adventure.
     *
     * @param players the list of players involved in the adventure
     */
    public void startAdventure(List <Player> players) {
        if (face_down) {
            changeFace();
        }
            // logica specifica nelle sottoclassi
    }

    //da gestire la logica che le carte avventura si girano una a una quando si risolvono

    /**
     * This method flips the card to reveal it (if face down).
     * Called when the adventure is about to be resolved.
     */
    public void changeFace(){this.face_down = !this.face_down;}

    /**
     * Checks if the card is currently face down.
     * @return true if the card is face down, false if face up
     */
    public boolean isFaceDown() {
        return face_down;
    }

    /**@return type of adventure card*/
    public CardAdventureType getType() {
        return type;
    }

    /**@return the number of positions the player will lose.*/
    public int getCost_of_days() {
        return cost_of_days;
    }

    /**@return the player's board.*/
    public Board getBoard() {
        return board;
    }

    /**@return the card's level.*/
    public int getLevel() {return level;}
}



