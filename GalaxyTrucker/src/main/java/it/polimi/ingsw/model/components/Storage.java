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

public class Storage extends CardComponent  implements Serializable {
    private int size;
    private List<Cargo> carried_cargos = new ArrayList<>();
@JsonCreator
    public Storage(
            @JsonProperty("component_type") ComponentType component_type,
            @JsonProperty("connectors") Map<Direction, ConnectorType> connectors,
            @JsonProperty("size") int size
           ) {
        super(component_type, connectors);
        this.size = size;
        this.carried_cargos = new ArrayList<>(Collections.nCopies(size, Cargo.Empty)); // Riempie la lista con EMPTY

    }

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

    public List<Cargo> getCarried_cargos() {return carried_cargos;}

    public Cargo getCargo(int index){
        return carried_cargos.get(index);
    }

    public int getCargoCount(){
        int cargo_count = 0;
        for(int i = 0; i < carried_cargos.size(); i++){
            if(!carried_cargos.get(i).equals(Cargo.Empty)) cargo_count++;
        }
        return cargo_count;

    }

    public void removeCargo(Map<Cargo, Integer> cargoMap){
        if(cargoMap.size()>this.size){
            throw new IllegalArgumentException("Trying to remove more cargo than the size of the storage");
        }
    for (Map.Entry<Cargo, Integer> entry : cargoMap.entrySet()) {
            int index = entry.getValue();

            carried_cargos.set(index, Cargo.Empty);

        }
    }

}
