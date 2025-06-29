package it.polimi.ingsw.view.GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;
/**
 * Controller for the screen where the host selects the number of players for the game.
 */
public class Numplayercontroller {
    @FXML
    private Spinner<Integer> playerNumberSpinner;
    @FXML
    private Button confirmButton;
    @FXML
    private Button goBack;

    private CompletableFuture<Integer> playerNumber = new CompletableFuture<>();
    private GUI gui;

    /**
     * Initializes the spinner that allows the host to select the number of players.
     * The spinner range is set from 2 to 4, which are the allowed player counts.
     */

    @FXML
    public void initializespinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4);
        playerNumberSpinner.setValueFactory(valueFactory);
    }

    /**
     * Sets up the stage, scene, and spinner component for player selection.
     * @param stage
     * @throws Exception if the FXML file cannot be loaded
     */
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NumPlayers.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        initializespinner();
        Scene scene = new Scene(root);
        stage.setTitle("Join the Game");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }

    /**
     * Sets the GUI reference for this controller.
     * @param gui the GUI instance to associate with this controller
     */

    public void setGui(GUI gui) {
        this.gui=gui;
    }

    /**
     * @return a CompletableFuture containing the selected player count
     */
    public CompletableFuture<Integer> getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Handles the confirmation of the selected number of players.
     * @param event the action event triggered by clicking the confirm button
     */
    @FXML
    public void setPlayerNumber(ActionEvent event) {
        gui.showMessage("In attesa di player nella lobby");
        this.playerNumber.complete(playerNumberSpinner.getValue());
        goBack.setDisable(true);
        confirmButton.setDisable(true);

    }
    @FXML
    public void setOne(ActionEvent event) {
        this.playerNumber.complete(-1);
    }
}
