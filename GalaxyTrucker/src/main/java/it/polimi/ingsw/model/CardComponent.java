package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;


public class CardComponent {
    private final ComponentType component_type;
    private Map<Direction,ConnectorType> connectors = new EnumMap<>(Direction.class);
    private boolean face_down;

    //costruttore? ha senso che face down sia false all'inizio quando istanziata ?

    public CardComponent(ComponentType component_type, Map<Direction,ConnectorType> connectors){

        this.component_type = component_type;
        this.connectors =new EnumMap<>(connectors);
        this.face_down = true;
    }

    public void changefaceshowed(){
        face_down = !face_down;
        return;
    }

    public ComponentType getComponent_type() {
        return component_type;
    }

    public ConnectorType getConnector_type(Direction direction) {
        return connectors.get(direction);
    }
    public List<ConnectorType> getvalidsconnectors(ConnectorType connector){
        List<ConnectorType> valids = new ArrayList<ConnectorType>();
        return  valids;
        //da fare
    }
    @Override
    public String toString() {
        return "CardComponent{" +
                "component_type=" + component_type +
                ", connectors=" + connectors +
                ", face_down=" + face_down +
                '}';
    }

}


