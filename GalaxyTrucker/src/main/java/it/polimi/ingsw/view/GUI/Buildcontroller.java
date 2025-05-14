package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javafx.scene.image.Image;

public class Buildcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    @FXML
    private HBox playersButtonBox;
    @FXML
    private Button endbutton;
    @FXML
    private GridPane shipGrid;
    @FXML
    private Button randomCard;
    @FXML private Button reservedCardButton;
    @FXML private HBox reservedCardPreview;


    @FXML
    public void showReservedCardPreview() {
        reservedCardPreview.getChildren().clear();
        List<CardComponent> reserved = gui.getClient().getPlayer_local().getShip().getExtra_components();

        for (CardComponent card : reserved) {
            ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath()))));
            view.setFitWidth(60);
            view.setPreserveRatio(true);
            reservedCardPreview.getChildren().add(view);
        }
        reservedCardPreview.setVisible(true);
        reservedCardPreview.setManaged(true);
    }

    @FXML
    public void hideReservedCardPreview() {
        reservedCardPreview.setVisible(false);
        reservedCardPreview.setManaged(false);
    }

    public GridPane getShipGrid() {
        return shipGrid;
    }

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
                if (i == 2 && j == 3) {
                    Color color = gui.getClient().getPlayer_local().getColor();
                    String imagePath = null;

                    switch (color) {
                        case BLUE:
                            imagePath = "/images/cardComponent/GT-mainUnitBlue.jpg";
                            break;
                        case RED:
                            imagePath = "/images/cardComponent/GT-mainUnitRed.jpg";
                            break;
                        case GREEN:
                            imagePath = "/images/cardComponent/GT-mainUnitGreen.jpg";
                            break;
                        case YELLOW:
                            imagePath = "/images/cardComponent/GT-mainUnitYellow.jpg";
                            break;
                    }

                    if (imagePath != null) {
                        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(48);
                        imageView.setFitHeight(48);
                        imageView.setPreserveRatio(true);
                        cell.getChildren().add(imageView);
                    }
                }


                shipGrid.add(cell, j, i);
            }
        }
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }


    public void setupPlayerButtons(List<Player> otherPlayers) {
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

    /*public void placeCardOnShip(CardComponent card, Pair<Integer, Integer> coords) {
        int y = coords.getKey(); // RIGA
        int x = coords.getValue(); // COLONNA
        String imagePath = card.getImagePath();

        if (imagePath == null) return;

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);

        // Cerca la cella corretta nella GridPane
        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // fallback nel caso siano null (succede se non impostati nel FXML)
            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

        }

        // Aggiorna il modello se necessario
        CardComponent[][] shipboard = gui.getClient().getPlayer_local().getShip().getShipBoard();
        shipboard[y][x] = card;
    }*/
    public void placeCardOnShip(CardComponent card, Pair<Integer, Integer> coords) {
        int y = coords.getKey(); // RIGA
        int x = coords.getValue(); // COLONNA
        String imagePath = card.getImagePath();

        if (imagePath == null) return;

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setPreserveRatio(true);

        // Cerca la cella corretta nella GridPane
        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // fallback nel caso siano null (succede se non impostati nel FXML)
            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            // Quando trovi la cella corretta (x, y), aggiungi l'ImageView
            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                cell.getChildren().clear(); // Rimuovi eventuali carte precedenti
                cell.getChildren().add(imageView); // Aggiungi la nuova carta
                break; // Esci dal ciclo appena trovata la cella
            }
        }

        // Aggiorna il modello della nave con la carta piazzata (opzionale)
        CardComponent[][] shipboard = gui.getClient().getPlayer_local().getShip().getShipBoard();
        shipboard[y][x] = card;
    }

}
