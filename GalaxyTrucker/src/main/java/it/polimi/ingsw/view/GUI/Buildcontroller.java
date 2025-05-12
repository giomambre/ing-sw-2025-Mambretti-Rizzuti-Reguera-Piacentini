package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Buildcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    @FXML
    private HBox playersButtonBox;
    @FXML
    private Button endbutton;
    @FXML
    private GridPane shipGrid;

    public void initializeShipBoard() {
        shipGrid.getChildren().clear(); // shipGrid è un GridPane definito in FXML
        CardComponent[][] shipboard=gui.getClient().getPlayer_local().getShip().getShipBoard();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(50, 50);

                if (shipboard[i][j].getComponentType()== ComponentType.NotAccessible) {
                    cell.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
                } else {
                    cell.setStyle("-fx-background-color: lightyellow;");
                }

                shipGrid.add(cell, j, i);
            }
        }
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }


    public void setupPlayerButtons(List<Player> otherPlayers) {
        System.out.println("ALTRI GIOCATORI: " + otherPlayers);
        Platform.runLater(() -> {
            playersButtonBox.getChildren().clear(); // pulisci prima
            for (Player p : otherPlayers) {
                Button playerButton = new Button(p.getNickname());
                //playerButton.setOnAction(e -> showShipForPlayer(p.getId()));
                //playerButton.getStyleClass().add("player-button"); // se vuoi uno stile CSS
                playersButtonBox.getChildren().add(playerButton);
            }
        });
    }



    @FXML
    public void setFour(ActionEvent event) {
        action.complete(4);
    }
    @FXML
    public void setThree(ActionEvent event) {
        action.complete(3);
    }
    @FXML
    public void setTwo(ActionEvent event) {
        action.complete(2);
    }
    @FXML
    public void setOne(ActionEvent event) {
        action.complete(1);
    }

    public CompletableFuture<Integer> getAction() {
        if (action == null) {
            action = new CompletableFuture<>();
        }
        return action;
    }
}
