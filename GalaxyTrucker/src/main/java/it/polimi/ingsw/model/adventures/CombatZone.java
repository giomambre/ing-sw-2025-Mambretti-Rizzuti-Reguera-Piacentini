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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.enumerates.Direction.North;

public class CombatZone extends CardAdventure{
    private int crewmates_loss;
    private int cargo_loss;
    private List<Pair<MeteorType, Direction>> meteors;


    public CombatZone(int level, int cost_of_days, CardAdventureType type, Board board, int crewmates_loss, int cargo_loss, List<Pair<MeteorType, Direction>> meteors) {
        super(level, cost_of_days, type, board);
        this.crewmates_loss=crewmates_loss;
        this.cargo_loss=cargo_loss;
        this.meteors = meteors;
    }

    public void executeLessCrewmates1(Player player) {
        board.movePlayer(player, -getCost_of_days());
    }

    public void executeLessEnginePower1(Player player, Map<CardComponent,Integer> astronaut_losses) {

        Ship ship_player = player.getShip();
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {
                CardComponent component = ship_player.getComponent(i, j);

                for (CardComponent unit : astronaut_losses.keySet()) {
                    if (component.equals(unit)){

                        ((LivingUnit) component).removeCrewmates(astronaut_losses.get(unit)); // occhio al cast Exception

                    }
                }
            }
        }
    }

    // LessCrewmates2 e LessCannonPower1 vengono gestiti con la execute di meteorswarm

    public void executeLessEnginePower2(Player player, Map<CardComponent, Map<Cargo, Integer>> cargo_position) {

        Ship ship_player = player.getShip();
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {

                CardComponent card = ship_player.getComponent(i, j);
                for (CardComponent storage : cargo_position.keySet()) {

                    if (card.equals(storage)) {
                        ((Storage) storage).removeCargo(cargo_position.get(storage));
                    }


                }
            }
        }
    }

    public void executeLessCannonPower2(Player player) {
        board.movePlayer(player, -getCost_of_days());
    }
}
