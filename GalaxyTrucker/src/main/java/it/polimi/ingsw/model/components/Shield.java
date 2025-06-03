package it.polimi.ingsw.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.smartcardio.Card;

/**
 * This class is a subclass of {@code CardComponent} that represents a shield component on the ship.
 * A shield protects the ship from small meteors or cannon shots.
 * <ul>
 *     <li>covered_sides: a map indicating which directions (North, East, South, West) are protected by the shield</li>
 * </ul>
 */
public class Shield extends CardComponent implements Serializable {
    private Map<Direction, Boolean> covered_sides = new EnumMap<>(Direction.class);

    /**
     * By default, the shield covers the North and East sides.
     *
     * @param component_type
     * @param connectors the connectors for this component in each direction
     */
    @JsonCreator
    public Shield( @JsonProperty("component_type") ComponentType component_type,
                  @JsonProperty("connectors")Map<Direction, ConnectorType> connectors, String imagePath) {

            super(component_type, connectors, imagePath);
            covered_sides.put(North,Boolean.TRUE);
            covered_sides.put(East,Boolean.TRUE);
            covered_sides.put(South,Boolean.FALSE);
            covered_sides.put(West,Boolean.FALSE);

    }


    /**
     * This method rotates the shield 90 degrees clockwise.
     * This affects both the connector orientation and the sides protected by the shield.
     */
    @Override
    public void rotate(){ //clockwise rotation
        super.rotate();
        Map<Direction, Boolean> rotated = new EnumMap<>(Direction.class);

        rotated.put(East, covered_sides.get(North));
        rotated.put(South, covered_sides.get(East));
        rotated.put(West, covered_sides.get(South));
        rotated.put(North, covered_sides.get(West));

        covered_sides = rotated;

    }

    /** @return a map from direction (sides of the shield) to boolean (true if covered, false otherwise) */
    public Map<Direction, Boolean> getCoveredSides() {
        return covered_sides;
    }

    /**
     * This method sets the map of directions that are covered by the shield.
     *
     * @param covered_sides a map indicating which directions are protected
     */
    public void setCovered_sides(Map<Direction, Boolean> covered_sides) {
        this.covered_sides = covered_sides;
    }

    /**
     * This methopd creates a deep copy of this shield component, preserving its covered sides, connectors, and UUID.
     *
     * @return a new identical {@code Shield} instance
     */
    public CardComponent copy() {

        Shield copy = new Shield(getComponentType(),getConnectors(), getImagePath());
        copy.setCovered_sides(covered_sides);
        copy.setCard_uuid(getCard_uuid());
        copy.setRotationAngle(this.getRotationAngle());

        return copy;

    }

}
