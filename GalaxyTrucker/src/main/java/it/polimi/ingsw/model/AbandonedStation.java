package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public class AbandonedStation extends  CardAdventure{

    int needed_crewmates ;
    List<Cargo> cargo_reward; //da capire come implementare quali l utente accetta e quali rifiuta



    public AbandonedStation(int level, int cost_of_days, CardAdventureType type , int needed_crewmates, Board board, List<Cargo> cargo_reward) {
        super(level, cost_of_days,type ,board);
    this.needed_crewmates = needed_crewmates;
    this.cargo_reward = cargo_reward;


    }



//eventuale controllo se nessuno accetta la carte, da fare nel controller, tutto rimane invariato nel model
    public void execute(Player player,Map<CardComponent , Map<Cargo,Integer>> new_cargo_positions) {

        Ship ship_player = player.getShip();
        board.MovePlayer(player, -getCost_of_days());
        for (int i = 0; i < ship_player.getROWS(); i++) {
            for (int j = 0; j < ship_player.getCOLS(); j++) {

                CardComponent card = ship_player.getComponent(i, j);
                for (CardComponent storage : new_cargo_positions.keySet()) {

                    if (card.equals(storage)) {
                        ((Storage) storage).addCargo(new_cargo_positions.get(storage));
                    }



                }
            }

        }
    }


    public List<Cargo> getCargo() {
        return cargo_reward;
    }

    public int getNeeded_crewmates() {
        return needed_crewmates;
    }








}
