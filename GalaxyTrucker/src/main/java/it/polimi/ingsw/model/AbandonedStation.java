package it.polimi.ingsw.model;

import java.util.List;
import java.util.Map;

public class AbandonedStation extends CardAdventure{

    int num_of_crewmates ;
    List<Cargo> cargo_reward; //da capire come implementare quali l utente accetta e quali rifiuta
    Player player;
    Map<Integer,Cargo> new_cargos;
    public AbandonedStation(int level, int cost_of_days,int num_of_crewmates, List<Cargo> cargo_reward,Player player,Map<Integer,Cargo> new_cargos  ) {

        super(level, cost_of_days);
        this.num_of_crewmates = num_of_crewmates;
        this.cargo_reward = cargo_reward;
        this.player = player;
        this.new_cargos = new_cargos;
    }


// 0 : Red , 2: Blue
    //non finita da fare l'aggiunta dei cargo e la rimozione degli astronauti







}
