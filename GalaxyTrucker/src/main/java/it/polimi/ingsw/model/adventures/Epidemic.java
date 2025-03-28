package it.polimi.ingsw.model.adventures;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.CardAdventureType;
import it.polimi.ingsw.model.enumerates.ComponentType;

public class Epidemic extends CardAdventure {
    int ROWS=5 , COLS=7;

    public Epidemic(int level, int cost_of_days, CardAdventureType type, Board board) {
        super(level, cost_of_days, type, board);
    }

    public void execute(Player player) {
        Ship ship = player.getShip();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {


                if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                        && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0)  {

                    if (ship.getComponent(row+1, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row+1, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).RemoveCrewmates(1);
                        ((LivingUnit)ship.getComponent(row+1, col)).RemoveCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row, col+1)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).RemoveCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col+1)).RemoveCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row, col-1)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row, col)).RemoveCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col-1)).RemoveCrewmates(1);
                    }

                    if (ship.getComponent(row, col).getComponentType() == ComponentType.LivingUnit
                            && ((LivingUnit)ship.getComponent(row-1, col)).getNum_crewmates() != 0
                            && ((LivingUnit)ship.getComponent(row, col)).getNum_crewmates() != 0){
                        ((LivingUnit)ship.getComponent(row-1, col)).RemoveCrewmates(1);
                        ((LivingUnit)ship.getComponent(row, col)).RemoveCrewmates(1);
                    }


                }

            }
        }

    }

}
