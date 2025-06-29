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

import javafx.scene.input.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for managing the selection of crewmates within the ship grid.
 */
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

    /**
     * This method initializes the crewmate selection buttons with their corresponding actions.
     * Each button, when clicked, completes the selection with its associated crewmate type.
     */
    public void initialize() {
        astronautButton.setOnAction(e -> selectCrewmate(1));
        pinkAlienButton.setOnAction(e -> selectCrewmate(2));
        brownAlienButton.setOnAction(e -> selectCrewmate(3));
    }

    /**
     * This method completes the crewmate selection with the given type and closes the stage if open.
     * @param crewmateType the type of crewmate selected (1 = Astronaut, 2 = PinkAlien, 3 = BrownAlien)
     */
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

    /**
     * Configures the UI to show only the crewmates that can be assigned to the given card.
     * Also updates the label with the coordinates of the card.
     * @param coords the coordinates of the card
     * @param supportedCrewmates a list of allowed crewmate types for this card
     */
    public void setCardWithSupportedCrewmates(Pair<Integer, Integer> coords, List<CrewmateType> supportedCrewmates) {

        cardInfoLabel.setText("Seleziona il crewmate per questa Living Unit \n" + "alle coordinate (" + coords.getKey() + ", " + coords.getValue() + ") :");
        cardInfoLabel.setWrapText(true);

        astronautButton.setVisible(supportedCrewmates.contains(CrewmateType.Astronaut));
        astronautButton.setManaged(supportedCrewmates.contains(CrewmateType.Astronaut));

        pinkAlienButton.setVisible(supportedCrewmates.contains(CrewmateType.PinkAlien));
        pinkAlienButton.setManaged(supportedCrewmates.contains(CrewmateType.PinkAlien));

        brownAlienButton.setVisible(supportedCrewmates.contains(CrewmateType.BrownAlien));
        brownAlienButton.setManaged(supportedCrewmates.contains(CrewmateType.BrownAlien));
    }

    @FXML
    private void handleMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setTranslateY(-2);
        button.setScaleX(1.05);
        button.setScaleY(1.05);
    }

    @FXML
    private void handleMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setTranslateY(0);
        button.setScaleX(1.0);
        button.setScaleY(1.0);
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
