package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Storage extends CardComponent {
    private final int size;
    private List<Cargo> carried_cargos = new ArrayList<>();

    public Storage(ComponentType component_type, Map<Direction, ConnectorType> connectors,int size) {
        super(component_type, connectors);
        this.size=size;
    }

    public void AddCargo(Cargo cargo){

    }


}
