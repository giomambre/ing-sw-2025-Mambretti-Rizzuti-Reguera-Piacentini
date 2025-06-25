package it.polimi.ingsw;

import it.polimi.ingsw.model.CardComponentLoader;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardComponentLoaderTest {

    private CardComponentLoader loader;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new CardComponentLoader();
    }


    @Test
    void testLoadCardComponents_EmptyJsonArray() throws IOException {
        String jsonContent = "[]";

        File jsonFile = tempDir.resolve("empty_cards.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonContent);
        }

        List<CardComponent> components = loader.loadCardComponents(jsonFile.getAbsolutePath());

        assertNotNull(components);
        assertTrue(components.isEmpty());
    }

    @Test
    void testLoadCardComponents_NonExistentFile() {
        String nonExistentPath = tempDir.resolve("non_existent.json").toString();

        assertThrows(IOException.class, () -> {
            loader.loadCardComponents(nonExistentPath);
        });
    }

    @Test
    void testLoadCardComponents_InvalidJsonFormat() throws IOException {
        String invalidJsonContent = "{ invalid json content }";

        File jsonFile = tempDir.resolve("invalid.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(invalidJsonContent);
        }

        assertThrows(IOException.class, () -> {
            loader.loadCardComponents(jsonFile.getAbsolutePath());
        });
    }

    @Test
    void testLoadCardComponents_InvalidJsonStructure() throws IOException {
        String jsonContent = """
            [
                {
                    "wrongField": "value",
                    "anotherWrongField": 123
                }
            ]
            """;

        File jsonFile = tempDir.resolve("wrong_structure.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(jsonContent);
        }


        assertThrows(Exception.class, () -> {
            loader.loadCardComponents(jsonFile.getAbsolutePath());
        });
    }


    @Test
    void testConstructor() {
        CardComponentLoader newLoader = new CardComponentLoader();
        assertNotNull(newLoader);

        assertDoesNotThrow(() -> {

        });
    }
}