package it.polimi.ingsw.view.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Guiselectcontroller {
    private GUI gui;
    private List<Integer> lobbies;
    private CompletableFuture<Integer> selectedLobbyFuture;

    @FXML
    private ListView<Integer> lobbyListView;

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChooseLobby.fxml"));
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
    public void setGui(GUI gui) {
        this.gui=gui;
    }


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

    @FXML
    private void handleSelect() {
        Integer selected = lobbyListView.getSelectionModel().getSelectedItem();
        System.out.println("METODO HANDLESELECT Selected lobby: " + selected);
        if (selected != null && selectedLobbyFuture != null) {
            selectedLobbyFuture.complete(selected);
        }
    }

}
