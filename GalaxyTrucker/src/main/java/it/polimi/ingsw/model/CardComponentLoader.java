package it.polimi.ingsw.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import it.polimi.ingsw.model.components.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CardComponentLoader {

    private ObjectMapper objectMapper;

    public CardComponentLoader() {
        objectMapper = new ObjectMapper();
    }

    public List<CardComponent> loadCardComponents(String jsonFilePath) throws IOException {
        // Carica il JSON dal file
        return objectMapper.readValue(new File(jsonFilePath),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CardComponent.class));
    }
}