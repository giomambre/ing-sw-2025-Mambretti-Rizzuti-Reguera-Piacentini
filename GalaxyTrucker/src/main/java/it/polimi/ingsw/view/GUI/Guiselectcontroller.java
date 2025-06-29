package it.polimi.ingsw.view.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the GUI screen that allows the player to select a game lobby to join.
 */
public class Guiselectcontroller {
    private GUI gui;
    private List<Integer> lobbies;
    private CompletableFuture<Integer> selectedLobbyFuture;

    @FXML
    private ListView<Integer> lobbyListView;

    /**
     * Launches the lobby selection screen and displays the available lobbies.
     * @param stage the stage on which the selection scene is displayed
     * @throws Exception if an error occurs while loading the FXML file
     */
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChooseLobby.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        setLobbies(gui.getlobbies());
        Scene scene = new Scene(root);
        stage.setTitle("Select Lobby");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }

    /**
     * Sets the reference to the main GUI.
     * @param gui the main GUI instance
     */
    public void setGui(GUI gui) {
        this.gui=gui;
    }

    /**
     * Updates the list of available lobbies shown in the interface.
     * @param lobbies a list of available lobby
     */
    public void setLobbies(List<Integer> lobbies) {
        Platform.runLater(() -> {
            lobbyListView.getItems().setAll(lobbies);
        });
    }

    public CompletableFuture<Integer> getSelectedLobbyFuture() {
        if (selectedLobbyFuture == null) {
            selectedLobbyFuture = new CompletableFuture<>();
        }
        return selectedLobbyFuture;
    }

    /**
     * Completes the {@link CompletableFuture} with the selected lobby ID.
     */
    @FXML
    private void handleSelect() {
        Integer selected = lobbyListView.getSelectionModel().getSelectedItem();
        System.out.println("METODO HANDLESELECT Selected lobby: " + selected);
        if (selected != null && selectedLobbyFuture != null) {
            selectedLobbyFuture.complete(selected);
        }
    }

}
