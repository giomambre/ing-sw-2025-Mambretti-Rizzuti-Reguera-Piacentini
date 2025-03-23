package it.polimi.ingsw.model;

import java.util.Map;

public class LivingUnit extends CardComponent{

    private int num_astronaut;
    private CrewmateType crewate_type;

    public LivingUnit(ComponentType component_type, Map<Direction, ConnectorType> connectors) {
        super(component_type, connectors);
        this.num_astronaut = 0;
    }

    public void addCrewmate(CrewmateType crewate_type) {
    switch (crewate_type) {
        case Astronaut:
            this.num_astronaut=2;
            this.crewate_type=crewate_type;
            break;
        case PinkAlien:
            this.num_astronaut=1;
            this.crewate_type=crewate_type;
            break;

        case BrownAlien:
            this.num_astronaut=1;
            this.crewate_type=crewate_type;
            break;
    }
    }

    public int getNum_astronaut() {
        return num_astronaut;
    }

    public CrewmateType getCrewate_type() {
        return crewate_type;
    }

    public void RemoveCrewmates(int num_to_remove){

        num_astronaut -= num_to_remove;

        if(num_astronaut == 0) crewate_type=CrewmateType.None;


    }

}
