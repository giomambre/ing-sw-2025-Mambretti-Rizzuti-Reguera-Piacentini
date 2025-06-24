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

public class Numplayercontroller {
    @FXML
    private Spinner<Integer> playerNumberSpinner;
    @FXML
    private Button confirmButton;
    @FXML
    private Button goBack;

    private CompletableFuture<Integer> playerNumber = new CompletableFuture<>();
    private GUI gui;

    @FXML
    public void initializespinner() {
        // Imposta i valori min, max e il valore iniziale
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4); // da 2 a 4 giocatori, valore iniziale 2
        playerNumberSpinner.setValueFactory(valueFactory);
    }

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

    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public CompletableFuture<Integer> getPlayerNumber() {
        return playerNumber;
    }

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
