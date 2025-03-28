package it.polimi.ingsw.model.components;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.Map;

/**
 * This class represent the CardComponent LivingUnit that can host 1 or 2 astronauts or an alien (alien support required)
 * <ul>
 *     <li>num_crewmates: how many crewmates are carried by this living unit</li>
 *     <li>crewmate_type: Astronaut, BrownAlien, PinkAlien</li>
 *     <li>alien_support: point to the alien support of the living unit if it's present, otherwise null</li>
 * </ul>
 */
public class LivingUnit extends CardComponent{
 //rivedere la cosa che l'attributo supporto alieno è null se non c'è
    //secondo me va rivista questa logica perchè se ci sono due moduli per il supporto alieno collegati alla living unit e di quel colore, l'alieno può viverci anche se uno dei due moduli viene eliminato
    private int num_crewmates;
    private CrewmateType crewmate_type;


    @JsonCreator
    public LivingUnit(
            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors) {
        super(component_type, connectors);
        this.num_crewmates = 0;


    }

    /**
     * This method allows to add 2 astronauts to this living unit
     */
    public void addAstronauts() {


        this.crewmate_type = CrewmateType.Astronaut;
        this.num_crewmates=2;

    }



    public void addAlien(CrewmateType crewmate_type) {

      //controllare che ci sia una cabina
        this.crewmate_type = crewmate_type;
        this.num_crewmates=1;

    }





    public int getNum_crewmates() {
        return num_crewmates;
    }

    public CrewmateType getCrewmateType() {
        return crewmate_type;
    }

    /**
     * This method is used to remove one or more crewmates to this living unit
     * @param num_to_remove how many crewmates must be removed
     */
    public void removeCrewmates(int num_to_remove){

        if(num_to_remove>this.num_crewmates){
            throw new IllegalArgumentException("Trying to remove a Crewmate that does not exist");
        }

        num_crewmates -= num_to_remove;

        if(num_crewmates == 0) crewmate_type=CrewmateType.None;


    }

}
