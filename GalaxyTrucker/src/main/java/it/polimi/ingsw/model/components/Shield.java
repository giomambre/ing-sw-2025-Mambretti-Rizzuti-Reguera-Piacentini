package it.polimi.ingsw.model.components;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.util.EnumMap;
import java.util.Map;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Shield extends CardComponent {
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

}
