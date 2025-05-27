package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CrewmateSelectionController {

    @FXML
    private ImageView cardImageView;

    @FXML
    private Label cardInfoLabel;

    @FXML
    private Button astronautButton;

    @FXML
    private Button pinkAlienButton;

    @FXML
    private Button brownAlienButton;

    private Stage stage;
    private CompletableFuture<Integer> crewmateSelection = new CompletableFuture<>();

    public void initialize() {
        // Setup button actions
        astronautButton.setOnAction(e -> selectCrewmate(1)); // Astronaut
        pinkAlienButton.setOnAction(e -> selectCrewmate(2)); // PinkAlien
        brownAlienButton.setOnAction(e -> selectCrewmate(3)); // BrownAlien
    }

    private void selectCrewmate(int crewmateType) {
        if (!crewmateSelection.isDone()) {
            crewmateSelection.complete(crewmateType);
            if (stage != null) {
                stage.close();
            }
        }
    }

    public CompletableFuture<Integer> getCrewmate() {
        if (crewmateSelection == null) {
            crewmateSelection = new CompletableFuture<>();
        }
        return crewmateSelection;
    }

    // Metodo alternativo per passare direttamente i crewmate supportati
    public void setCardWithSupportedCrewmates(Pair<Integer, Integer> coords, List<CrewmateType> supportedCrewmates) {

        // Set card info text
        cardInfoLabel.setText("Seleziona il crewmate per questa Living Unit alle coordinate (" + coords.getKey() + ", " + coords.getValue() + ") :");

        // Configure buttons directly with provided list
        astronautButton.setVisible(supportedCrewmates.contains(CrewmateType.Astronaut));
        astronautButton.setManaged(supportedCrewmates.contains(CrewmateType.Astronaut));

        pinkAlienButton.setVisible(supportedCrewmates.contains(CrewmateType.PinkAlien));
        pinkAlienButton.setManaged(supportedCrewmates.contains(CrewmateType.PinkAlien));

        brownAlienButton.setVisible(supportedCrewmates.contains(CrewmateType.BrownAlien));
        brownAlienButton.setManaged(supportedCrewmates.contains(CrewmateType.BrownAlien));
    }

    public CompletableFuture<Integer> getCrewmateSelection() {
        return crewmateSelection;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void resetSelection() {
        crewmateSelection = new CompletableFuture<>();
    }
}
