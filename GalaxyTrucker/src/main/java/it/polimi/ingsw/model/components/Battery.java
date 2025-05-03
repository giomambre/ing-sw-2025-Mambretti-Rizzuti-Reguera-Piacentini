package it.polimi.ingsw.model.components;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is a subclass of {@code CardComponent} that represents a battery component on the ship, which stores energy used by other systems such as shields or cannons.
 * The battery has a maximum capacity ({@code size}) and a current energy level ({@code stored}).
 * Batteries can be consumed or recharged within the allowed limits.
 * <ul>
 *     <li>size: the maximum capacity of the battery</li>
 *     <li>stored: current energy level</li>
 * </ul>
 */
public class Battery extends CardComponent  implements Serializable {
    //capacity
    private int size;
    //effectively stored
    private int stored;

    /**
     * Initially, the battery is fully charged (stored energy equals size).
     *
     * @param component_type the type of this component
     * @param connectors the connectors of this component in each direction
     * @param size the maximum capacity of the battery
     */
    @JsonCreator
    public Battery(

            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors, @JsonProperty("size") int size, String imagePath) {
        super(component_type, connectors, imagePath);
        this.size = size;
        this.stored = size;
    }

    //hp: le batterie si aggiungono solo quando si fanno i rifornimenti iniziali (mi sembra di ricordare dal regolamento)
    //se così non fosse vanno messe delle condizioni aggiuntive di verifica es: stored+num_batteries<size ...

    //altra cosa: non so se imporre direttamente che num batteries sia uguale alla size (ha più senso perchè penso tutti i giocatori riempiano al massimo lo spazio) però per attenermi all'uml ho messo così
    //se così fosse ha senso farlo direttamente nel costruttore ed eliminare questa funzione

    /**
     * This method adds a specified number of battery units to this component.
     *
     * @param num_batteries the number of batteries to add
     * @throws IllegalArgumentException if the total would exceed the battery's capacity
     */
    public void addBattery(int num_batteries) throws IllegalArgumentException{
       if(this.getStored()+num_batteries> size ){
           throw new IllegalArgumentException("More batteries than the size of this component");
       }
       stored += num_batteries;
    }

    /**
     * This method removes one unit of stored energy.
     *
     * @throws IllegalStateException if the battery is already empty
     */
     public void removeBattery(){
        if(stored==0){

            throw new IllegalStateException("Cannot remove batteries from this component, is already empty");



        }
        if(stored>0){
            stored--;
        }
     }

    /**
     * This method sets the current number of stored battery units.
     *
     * @param stored the new amount of stored energy
     */
    public void setStored(int stored) {
        this.stored = stored;
    }

    /** @return the amount of energy currently stored*/
    public int getStored(){
        return stored;
     }

    /**
     * This function creates a copy of this battery component, preserving its stored energy and unique ID.
     *
     * @return a new {@code Battery} with the same state
     */
    public CardComponent copy(){
        Battery copy = new Battery(getComponentType(),getConnectors(),size, getImagePath());
         copy.setStored(stored);
         copy.setCard_uuid(getCard_uuid());

         return copy;
     }

}
