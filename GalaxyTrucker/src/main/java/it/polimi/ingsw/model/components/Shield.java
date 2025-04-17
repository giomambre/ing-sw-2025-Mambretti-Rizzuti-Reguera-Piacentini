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

public class Shield extends CardComponent   implements Serializable {
    private Map<Direction, Boolean> covered_sides = new EnumMap<>(Direction.class);


    @JsonCreator
    public Shield( @JsonProperty("component_type") ComponentType component_type,
                  @JsonProperty("connectors")Map<Direction, ConnectorType> connectors) {


            super(component_type, connectors);
            covered_sides.put(North,Boolean.TRUE);
            covered_sides.put(East,Boolean.TRUE);
            covered_sides.put(South,Boolean.FALSE);
            covered_sides.put(West,Boolean.FALSE);

        }

        public Map<Direction, Boolean> getCoveredSides() {
        return covered_sides;
        }
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

        public void setCovered_sides(Map<Direction, Boolean> covered_sides) {

        this.covered_sides = covered_sides;
        }


        public CardComponent copy() {

        Shield copy = new Shield(getComponentType(),getConnectors());
        copy.setCovered_sides(covered_sides);
        copy.setCard_uuid(getCard_uuid());
        return copy;

        }

}
