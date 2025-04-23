package it.polimi.ingsw.model.components;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is a sublass of {@code CardComponent} that represents a living unit on the ship.
 * A living unit can host 1 or 2 astronauts or an alien (alien support required)
 * <ul>
 *     <li>num_crewmates: how many crewmates are carried by this living unit</li>
 *     <li>crewmate_type: Astronaut, BrownAlien, PinkAlien</li>
 * </ul>
 */
public class LivingUnit extends CardComponent  implements Serializable {
    private int num_crewmates;
    private CrewmateType crewmate_type;

    /**
     * @param component_type the type of this component
     * @param connectors a map of connectors configuration of this component
     */
    @JsonCreator
    public LivingUnit(
            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors) {
        super(component_type, connectors);
        this.num_crewmates = 0;


    }

    /**
     * This method allows to add 2 astronauts to this living unit.
     *
     *  @throws IllegalArgumentException if the living unit is already occupied
     */
    public void addAstronauts() {

        if(num_crewmates>0){
            throw new IllegalArgumentException("Trying to remove a Crewmate, you can't add two crewmates");
        }

        this.crewmate_type = CrewmateType.Astronaut;
        this.num_crewmates=2;

    }


    /**
     * This method allows to add an alien of the specified type to this living unit.
     *
     * @param crewmate_type the type of alien to add
     */
    public void addAlien(CrewmateType crewmate_type) {

      //controllare che ci sia una cabina
        this.crewmate_type = crewmate_type;
        this.num_crewmates=1;

    }

    /**
     * This method is used to remove one or more crewmates to this living unit
     *
     * @param num_to_remove how many crewmates must be removed
     */
    public void removeCrewmates(int num_to_remove){

        if(num_to_remove>this.num_crewmates){
            throw new IllegalArgumentException("Trying to remove a Crewmate that does not exist");
        }

        num_crewmates -= num_to_remove;

        if(num_crewmates == 0) crewmate_type=CrewmateType.None;

    }

    /** @return the number of crewmates in this living unit*/
    public int getNum_crewmates() {
        return num_crewmates;
    }

    /**
     * This method sets the number of crewmates in this living unit
     * @param num_crewmates
     */
    public void setNum_crewmates(int num_crewmates) {
        this.num_crewmates = num_crewmates;
    }

    /** @return the crewmate type*/
    public CrewmateType getCrewmate_type() {
        return crewmate_type;
    }

    /**
     * This method sets the crewmate type.
     * @param crewmate_type (PinkAlien, BrownAlien, Astronaut, None)
     */
    public void setCrewmate_type(CrewmateType crewmate_type) {
        this.crewmate_type = crewmate_type;
    }

    /**
     * This method reates a copy of this living unit, including its UUID, crewmate type, and number of crewmates.
     *
     * @return a cloned {@code LivingUnit}
     */
    public CardComponent copy(){
        LivingUnit copy = new LivingUnit(getComponentType(),getConnectors());
        copy.setCard_uuid(getCard_uuid());
        copy.setCrewmate_type(getCrewmate_type());
        copy.setNum_crewmates(getNum_crewmates());
        return copy;
    }

}
