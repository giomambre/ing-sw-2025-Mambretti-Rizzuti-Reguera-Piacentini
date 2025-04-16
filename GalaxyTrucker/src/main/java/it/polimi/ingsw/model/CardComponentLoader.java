package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.model.components.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for loading card components from a JSON file.
 * Uses Jackson's {@code ObjectMapper} to deserialize a list of {@code CardComponent} objects.
 */
public class CardComponentLoader {

    private ObjectMapper objectMapper;

    /**
     * Creates a new loader with a default {@code ObjectMapper} instance.
     */
    public CardComponentLoader() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Loads a list of {@code CardComponent} objects from a JSON file.
     * @param jsonFilePath the path to the JSON file containing the card data
     * @return a list of card component parsed from the file
     * @throws IOException if the file cannot be read or parsed
     */
    public List<CardComponent> loadCardComponents(String jsonFilePath) throws IOException {
        // Carica il JSON dal file
        return objectMapper.readValue(new File(jsonFilePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardComponent.class));
    }
}