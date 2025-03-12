package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CardComponent {
    private ComponentType component_type;
    private Map<Direction,ConnectorType> connectors = new HashMap<Direction,ConnectorType>();
    private boolean face_down;

    //costruttore? ha senso che face down sia false all'inizio quando istanziata ?

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
}
