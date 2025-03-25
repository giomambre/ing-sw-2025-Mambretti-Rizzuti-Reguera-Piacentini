package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Storage extends CardComponent {
    private int size;
    private List<Cargo> carried_cargos = new ArrayList<>();

    public Storage(ComponentType component_type, Map<Direction, ConnectorType> connectors,int size) {
        super(component_type, connectors);
        this.size=size;
        this.carried_cargos = new ArrayList<>(Collections.nCopies(size, Cargo.Empty)); // Riempie la lista con EMPTY
    }

    public void addCargo(Map<Cargo, Integer> cargoMap){
        for (Map.Entry<Cargo, Integer> entry : cargoMap.entrySet()) {
            Cargo cargo = entry.getKey();
            int index = entry.getValue();

                carried_cargos.set(index, cargo);

        }
    }

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
        for (Map.Entry<Cargo, Integer> entry : cargoMap.entrySet()) {
            int index = entry.getValue();

            carried_cargos.set(index, Cargo.Empty);

        }
    }

}
