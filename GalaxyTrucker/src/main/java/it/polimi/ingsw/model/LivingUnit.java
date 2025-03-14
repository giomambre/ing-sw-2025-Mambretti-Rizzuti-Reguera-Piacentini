package it.polimi.ingsw.model;

import java.util.Map;

public class LivingUnit extends CardComponent{

    private int num_astronaut;
    private Crewmate_type crewmateType;

    public LivingUnit(ComponentType component_type, Map<Direction, ConnectorType> connectors, int num_astronaut) {
        super(component_type, connectors);
        this.num_astronaut = 0;
    }

    public void addCrewmate(Crewmate_type crewmateType) {
    switch (crewmateType) {
        case astronaut:
            this.num_astronaut=2;
            this.crewmateType=crewmateType;
            break;
        case Pink_alien:
            this.num_astronaut=1;
            this.crewmateType=crewmateType;
            break;

        case Brown_alien:
            this.num_astronaut=1;
            this.crewmateType=crewmateType;
            break;
    }
    }

    public void RemoveCrewmate(int num_to_remove){
            if(num_to_remove>2){
                System.out.println("non so se errore o exception");
                return;
            }
            //da continuare

    }

}
