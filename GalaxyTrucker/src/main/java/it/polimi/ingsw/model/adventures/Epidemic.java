package it.polimi.ingsw.model.adventures;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.ComponentType;

/**
 * This class is a sublass of CardAdventure, from which it inherits attributes and methods
 * <ul>
 *     <li>ROWS: number of rows of the ship's plance</li>
 *     <li>COLS: number of rows of the ship's plance</li>
 * </ul>
 */
public class Epidemic extends CardAdventure {
    int ROWS=5 , COLS=7;

    /**
     *
     * @param level
     * @param cost_of_days
     * @param type
     * @param board
     */
    public Epidemic(int level, int cost_of_days, CardAdventureType type, Board board) {
        super(level, cost_of_days, type, board);
    }

    /**
     * This method is called to remove one crewmate from each occupied 'LivingUnit' component card
     * that is connected with another occupied cabin.
     * It checks all adjacent card.
     * @param player
     */
    public void execute(Player player) {
        Ship ship = player.getShip();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {


                if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                        && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0)  {

                    if (ship.getComponent(row+1, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row+1, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).removeCrewmates(1);
                        ((LivingUnit)ship.getComponent(row+1, col)).removeCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row, col+1)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).removeCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col+1)).removeCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row, col-1)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).removeCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col-1)).removeCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row-1, col)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row-1, col)).removeCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col)).removeCrewmates(1);
                    }


                }

            }
        }

    }

}
