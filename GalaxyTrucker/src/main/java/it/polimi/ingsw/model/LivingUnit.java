package it.polimi.ingsw.model;

import java.util.Map;

public class LivingUnit extends CardComponent{

    private int num_astronaut;
    private CrewmateType crewmate_type;
    private CardComponent alien_support;

    public LivingUnit(ComponentType component_type, Map<Direction, ConnectorType> connectors) {
        super(component_type, connectors);
        this.num_astronaut = 0;
        this.alien_support = null;
    }

    public void addCrewmate(CrewmateType crewmate_type, CardComponent alien_support) {
    switch (crewmate_type) {
        case Astronaut:
            this.num_astronaut=2;
            this.crewmate_type=crewmate_type;
            break;
        case PinkAlien:
            this.num_astronaut=1;
            this.crewmate_type=crewmate_type;
            this.alien_support=alien_support;
            break;

        case BrownAlien:
            this.num_astronaut=1;
            this.crewmate_type=crewmate_type;
            this.alien_support=alien_support;
            break;
    }
    }

    public void addAlienSupport(CardComponent alien_support) {this.alien_support=alien_support;}


    public int getNum_astronaut() {
        return num_astronaut;
    }

    public CrewmateType getCrewmateType() {
        return crewmate_type;
    }

    public void RemoveCrewmates(int num_to_remove){

        num_astronaut -= num_to_remove;

        if(num_astronaut == 0) crewmate_type=CrewmateType.None;


    }

    public CardComponent getAlien_support() {return alien_support;}
}
