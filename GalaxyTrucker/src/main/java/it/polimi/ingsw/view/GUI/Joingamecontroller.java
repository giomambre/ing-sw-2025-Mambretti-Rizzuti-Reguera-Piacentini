package it.polimi.ingsw.view.GUI;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for the screen where the player chooses how to join the game.
 * It allows the user to select between different joining options: joining an existing lobby or creating a new one.
 */
public class Joingamecontroller {

    @FXML
    private Button lobbyButton;

    @FXML
    private Button joinButton;

    private CompletableFuture<Integer> choiceFuture = new CompletableFuture<>();

    private GUI gui;

    /**
     * Initializes and displays the "Join Game" screen.
     * @param stage
     * @throws Exception if the FXML file fails to load
     */
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/joinGame.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        Scene scene = new Scene(root);
        stage.setTitle("Join the Game");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.setMaximized(true);

        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }
    @FXML
    public void setOne(ActionEvent event) {
        choiceFuture.complete(1);
    }
    @FXML
    public void setTwo(ActionEvent event) {
        choiceFuture.complete(2);
    }

    public void setGui(GUI gui) {
        this.gui=gui;
    }

    public CompletableFuture<Integer> getChoiceFuture() {
        return choiceFuture;
    }

}