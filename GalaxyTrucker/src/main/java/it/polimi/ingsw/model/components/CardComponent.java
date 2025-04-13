package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.enumerates.*;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.ConnectorType.*;
import static it.polimi.ingsw.model.enumerates.ComponentType.*;

/**
 * This class represents a Card Component. Each card has:
 * <ul>
 *     <li>component_type: indicates the type of the component card
 *     <li>connectors: Indicates for each direction (N,S,O,W) witch type of connectors it's present
 *     @see ComponentType ComponentType: to see the different types of card that compose the deck
 * </ul>
 */

public class CardComponent implements Serializable {
    private final ComponentType component_type;
    private Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
    private UUID card_uuid;

    public CardComponent(ComponentType component_type, Map<Direction,ConnectorType> connectors){
        card_uuid = UUID.randomUUID();
        this.component_type = component_type;
        this.connectors = new EnumMap<>(connectors);

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
    public List<ConnectorType> getValidConnectors(ConnectorType connector){
        List<ConnectorType> valids= new ArrayList<>();
        if(connector == Empty_Connector){
            throw new IllegalArgumentException("You cant use an empty connector");
        }

        switch (connector){

            case Double:
                valids.add(Double);
                valids.add(Universal);

                valids.add(Empty_Connector);

                break;
            case Single:
                valids.add(Single);
                valids.add(Universal);
                valids.add(Empty_Connector);

                break;
            case Universal:
                valids.add(Universal);
                valids.add(Double);
                valids.add(Single);
                valids.add(Empty_Connector);

                break;

            case Smooth:
                valids.add(Smooth);
                valids.add(Empty_Connector);
                break;


            case Engine_Connector,Cannon_Connector:
                valids.add(Empty_Connector);
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

    public UUID getCard_uuid() {
        return card_uuid;
    }

    @Override
    public String toString() {
        return "CardComponent{" +
                "component_type=" + component_type +
                ", connectors=" + connectors +

                '}';
    }


}


