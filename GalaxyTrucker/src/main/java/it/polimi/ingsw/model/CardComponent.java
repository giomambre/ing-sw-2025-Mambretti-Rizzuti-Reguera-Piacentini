package it.polimi.ingsw.model;

import javax.smartcardio.Card;
import java.util.*;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;

/**
 * This class
 */

public class CardComponent {
    private final ComponentType component_type;
    private Map<Direction,ConnectorType> connectors = new EnumMap<>(Direction.class);
    private boolean face_down;

    public CardComponent(ComponentType component_type, Map<Direction,ConnectorType> connectors){

        this.component_type = component_type;
        this.connectors = new EnumMap<>(connectors);
        this.face_down = true;
    }

    public void changeFaceShowed(){
        face_down = !face_down;
    }

    public boolean getFaceDown(){
        return face_down;
    }


    public ConnectorType getConnector_type(Direction direction) {
        return connectors.get(direction);
    }

    public List<ConnectorType> getvalidsconnectors(ConnectorType connector){
        List<ConnectorType> valids = new ArrayList<ConnectorType>();
        return  valids;
        //da fare
    }

    public List<ConnectorType> getValidsConnectors(ConnectorType connector){
        List<ConnectorType> valids= new ArrayList<>();
        switch (connector){

            case Double:
                valids.add(Double);
                valids.add(Universal);
                break;
            case Single:
                valids.add(Single);
                valids.add(Universal);
                break;
            case Universal:
                valids.add(Universal);
                valids.add(Double);
                valids.add(Single);
                break;

        }
        return valids;
    }

    public ComponentType GetComponent_type() {
        return component_type;
    }
    public ConnectorType getConnector(Direction direction) {
        return connectors.get(direction);
    }
    public boolean getFace() {return face_down;}

    public void rotate() {
        Map<Direction, ConnectorType> rotated = new EnumMap<>(Direction.class);

        rotated.put(East, connectors.get(North));
        rotated.put(South, connectors.get(East));
        rotated.put(West, connectors.get(South));
        rotated.put(North, connectors.get(West));


        connectors = rotated;
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


