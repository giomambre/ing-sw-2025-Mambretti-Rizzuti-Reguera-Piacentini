package it.polimi.ingsw.model;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class MeteorSwarm extends CardAdventure {

    List<Pair<MeteorType,Direction>> meteors;
    public MeteorSwarm(int level, int cost_of_days, CardAdventureType type, Board board, List<Pair<MeteorType,Direction>> meteors) {
        super(level, cost_of_days, type, board);
        this.meteors = meteors;

    }

    public void execute(Player player, Direction direction, MeteorType meteor_type,Boolean shield_usage,CardComponent battery,int position) {

        if ((direction == Direction.North || direction == Direction.South) && (position < 4 || position > 10)) return;
        if ((direction == Direction.East || direction == Direction.West) && (position < 5 || position > 9)) return;




     /*   switch (meteor_type) {
            case SmallMeteor  :
                if (shield_usage) {
                    ((Battery) battery).removeBattery();
                    break;
                }else if(){




                }

        }


    }
*/
    }
}
