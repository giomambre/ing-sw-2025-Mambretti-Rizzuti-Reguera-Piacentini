package it.polimi.ingsw.model;

import java.util.*;
import static it.polimi.ingsw.model.Direction.*;
import static it.polimi.ingsw.model.ConnectorType.*;

/**
 * This class represents a Card Component. Each card has:
 * <ul>
 *     <li>component_type: indicates the type of the component card
 *     <li>connectors: Indicates for each direction (N,S,O,W) witch type of connectors it's present
 *     <li>face_down: to indicate witch side of the card is shown. If it's face up then face_down==false ,otherwise face_down==true
 *     @see ComponentType ComponentType: to see the different types of card that compose the deck
 * </ul>
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

    /**
     * This method twist the card.
     */
    public void changeFaceShowed(){
        face_down = !face_down;
    }

    /**
     * This method tells us witch face of the card is shown
     * @return False means the card is face down. True means the card is face up
     */
    public boolean getFaceDown(){
        return face_down;
    }

    /**
     * This method tells us, giving a direction, witch type of connector is present on the card in that direction.
     * @param direction   North, East, South, West
     * @return one of the followings connector type:   Universal,Double,Single,Smooth,Cannon_Connector,Engine_Connector,EmptyConnector
     */
    public ConnectorType getConnector_type(Direction direction) {
        return connectors.get(direction);
    }

    /**
     * This method tells us, giving a connector type, witch types of connector can be connected to it.
     * @param connector
     * @return : a list containing all the possible connector that matches with the given one
     */
    public List<ConnectorType> getValidsConnectors(ConnectorType connector){
        List<ConnectorType> valids= new ArrayList<>();
        switch (connector){

            case Double:
                valids.add(Double);
                valids.add(Universal);

                valids.add(EmptyConnector);

                break;
            case Single:
                valids.add(Single);
                valids.add(Universal);
                valids.add(EmptyConnector);

                break;
            case Universal:
                valids.add(Universal);
                valids.add(Double);
                valids.add(Single);
                valids.add(EmptyConnector);

                break;

            case Smooth:
                valids.add(Smooth);
                valids.add(EmptyConnector);



            case Engine_Connector,Cannon_Connector:
                valids.add(EmptyConnector);
                break;
        }
        return valids;
    }

    /**
     * This method, called on a Card component, tells us witch type of card is.
     * @return the type of the card
     * @see ComponentType Component type: contains all the possible types that can be returned
     */
    public ComponentType getComponentType() {
        return component_type;
    }

    /**
     * This method, given a Direction, tells witch type of connector is present in that direction.
     * @param direction North, East, South, West
     * @return  one of the followings: Universal,Double,Single,Smooth,Cannon_Connector,Engine_Connector,EmptyConnector
     */
    public ConnectorType getConnector(Direction direction) {
        return connectors.get(direction);
    }

    /**
     * This method rotate the card of 90 degrees in a counterclockwise direction.
     * It's called by the controller when the player is building his ship and wants to rotate a certain card component.
     */

    //non bisogna magari eliminare il vecchio riferimento non ruotato??
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


