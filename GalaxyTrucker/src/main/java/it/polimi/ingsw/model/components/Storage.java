package it.polimi.ingsw.model.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

/**
 * This class is a subclass of {@code CardComponent} that represents a storage component on the ship.
 * A storage component can carry a limited number of cargo units during the flight.
 * <ul>
 *     <li>size: the maximum number of cargo units this storage can hold</li>
 *     <li>carried_cargos: the list of cargo currently stored in this component</li>
 * </ul>
 */
public class Storage extends CardComponent  implements Serializable {
    private int size;
    private List<Cargo> carried_cargos = new ArrayList<>();

    /**
     * The storage is initialized with all slots marked as {@code Cargo.Empty}.
     *
     * @param component_type the type of the component
     * @param connectors a map of connector configuration for each direction
     * @param size the maximum number of cargo units this storage can hold
     */
    @JsonCreator
    public Storage(
            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors,
            @JsonProperty("size") int size,
            String imagePath
           ) {
        super(component_type, connectors, imagePath);
        this.size = size;
        this.carried_cargos = new ArrayList<>(Collections.nCopies(size, Cargo.Empty)); // Riempie la lista con EMPTY

    }

    /**
     * This method adds cargo units to specific positions in the storage.
     *
     * @param cargoMap a map of cargo units and their corresponding storage slot indexes
     * @throws IllegalArgumentException if the number of cargo units exceeds the storage capacity
     */
    public void addCargo(Map<Cargo, Integer> cargoMap){

        if(cargoMap.size()>this.size){
            throw new IllegalArgumentException("Trying to add more cargo than the size of the storage");
        }

        for (Map.Entry<Cargo, Integer> entry : cargoMap.entrySet()) {
            Cargo cargo = entry.getKey();
            int index = entry.getValue();

            carried_cargos.set(index, cargo);

        }
    }


    public boolean removeCargo(Cargo cargo){

        for (int i = 0; i < carried_cargos.size(); i++) {
            if (carried_cargos.get(i).equals(cargo)) {
                carried_cargos.set(i, Cargo.Empty);
                return true;
            }
        }
        return false;

    }

    public void addCargo(Cargo cargo, int index){
        carried_cargos.set(index, cargo);
    }

    /** @return a full list of cargo currently stored in this component*/
    public List<Cargo> getCarried_cargos() {return carried_cargos;}

    /**
     * @param index the index of the cargo slot
     * @return the cargo item at that index
     */
    public Cargo getCargo(int index){
        return carried_cargos.get(index);
    }

    public int getSize() {return size;}

    /**
     * This method sets a new cargo list.
     *
     * @param carried_cargos the new list of cargo to store
     */
    public void setCarried_cargos(List<Cargo> carried_cargos) {
        this.carried_cargos = carried_cargos;
    }

    /** @return the number of cargo slots currently occupied*/
    public int getCargoCount(){
        int cargo_count = 0;
        for(int i = 0; i < carried_cargos.size(); i++){
            if(!carried_cargos.get(i).equals(Cargo.Empty)) cargo_count++;
        }
        return cargo_count;

    }

    public boolean containsCargo(Cargo cargo){

        return carried_cargos.contains(cargo);
    }

    /**
     * This method removes specific cargo items from the storage by setting their positions to {@code Cargo.Empty}.
     *
     * @param cargoMap a map where each entry associates a cargo unit to the index from which it should be removed
     * @throws IllegalArgumentException if the number of cargo items exceeds the storage capacity
     */
    public void removeCargo(Map<Cargo, Integer> cargoMap){
        if(cargoMap.size()>this.size){
            throw new IllegalArgumentException("Trying to remove more cargo than the size of the storage");
        }
    for (Map.Entry<Cargo, Integer> entry : cargoMap.entrySet()) {
            int index = entry.getValue();

            carried_cargos.set(index, Cargo.Empty);

        }
    }

    /**
     * This method creates a copy of this storage component, including its stored cargo and unique ID.
     *
     * @return a new {@code Storage} identical to this one
     */
    public CardComponent copy() {
    Storage copy = new Storage(getComponentType(),getConnectors(),size, getImagePath());
    copy.setCard_uuid(getCard_uuid());
    copy.setCarried_cargos(carried_cargos);
        copy.setRotationAngle(this.getRotationAngle());

        return copy;
    }

}
