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

    @Override
    public void execute() {
        System.out.println("metodo cuscinetto");


    }
//eventuale controllo se nessuno accetta la carte, da fare nel controller, tutto rimane invariato nel model
    public void execute(Player player,Map<Storage , Map<Integer,Cargo>> new_cargo_positions) {

    }



    public List<Cargo> getCargo() {
        return cargo_reward;
    }
// 0 : Red , 2: Blue
    //non finita da fare l'aggiunta dei cargo e la rimozione degli astronauti







}
