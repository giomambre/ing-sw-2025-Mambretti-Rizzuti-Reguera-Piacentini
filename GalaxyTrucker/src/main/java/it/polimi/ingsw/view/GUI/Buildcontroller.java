package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
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
import javafx.scene.control.ScrollPane;
import javafx.util.Pair;
import javafx.scene.image.ImageView;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import javafx.scene.image.Image;

public class Buildcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    private CompletableFuture<Integer> crewmate = new CompletableFuture<>();
    private List<Pair<Integer,Integer>> pickedcoords = new ArrayList<>();

    @FXML
    private HBox playersButtonBox;
    @FXML
    private HBox crewmateButtonBox;

    @FXML
    private Button endbutton;
    @FXML
    private GridPane shipGrid;
    @FXML
    private Button randomCard;

    @FXML private ScrollPane faceUpScrollPane;

    @FXML private HBox reservedCardPreview;
    @FXML private HBox faceupCardPreview;

    private final List<CardComponent> reservedCards = new ArrayList<>();


    private CompletableFuture<Pair<Integer,Integer>> coords = new CompletableFuture<>();


    private CompletableFuture<Integer> reservedCardIndex = new CompletableFuture<>();
    private CompletableFuture<Integer> faceupCardIndex = new CompletableFuture<>();

    private Stage playerStage;

    // Metodo aggiornato per sincronizzare con facedUp_deck_local del Client
    public void updateFaceUpCardsDisplay() {
        Platform.runLater(() -> {
            faceupCardPreview.getChildren().clear();

            List<CardComponent> faceUpCards = gui.getClient().getFacedUp_deck_local();

            for (int i = 0; i < faceUpCards.size(); i++) {
                CardComponent card = faceUpCards.get(i);

                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
                ImageView cardImage = new ImageView(image);
                cardImage.setFitWidth(100);
                cardImage.setPreserveRatio(true);

                final int index = i;

                cardImage.setOnMouseClicked(e -> {
                    if (!faceupCardIndex.isDone()) {
                        faceupCardIndex.complete(index);
                    }
                    if (!action.isDone()) {
                        action.complete(2);
                    }
                });

                faceupCardPreview.getChildren().add(cardImage);
            }

            if (faceUpScrollPane != null) {
                faceUpScrollPane.setVisible(!faceUpCards.isEmpty());
                faceUpScrollPane.setManaged(!faceUpCards.isEmpty());
            }
        });
    }

    public void setPlayerStage(Stage playerStage) {
        this.playerStage = playerStage;
    }
    public Stage getPlayerStage() {
        return playerStage;
    }

    public CompletableFuture<Integer> getReservedCardIndexFuture() {
        return reservedCardIndex;
    }
    public CompletableFuture<Integer> getFaceupCardIndexFuture() {
        return faceupCardIndex;
    }

    public void resetReservedCardIndex() {
        reservedCardIndex = new CompletableFuture<>();
    }
    public void resetfaceupCardIndex() {
        faceupCardIndex = new CompletableFuture<>();
    }

    public void resetCoords() {
        reservedCardIndex = new CompletableFuture<>();
    }

    public void resetAction() {
        action = new CompletableFuture<>();
    }

    public void addReservedCard(CardComponent card) {
        if (reservedCards.size() >= 2) return;

        reservedCards.add(card);

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
        ImageView cardImage = new ImageView(image);
        cardImage.setFitWidth(100);
        cardImage.setPreserveRatio(true);

        int index = reservedCards.size() - 1;

        cardImage.setOnMouseClicked(e -> {
            if (!reservedCardIndex.isDone()) {
                reservedCardPreview.getChildren().remove(cardImage);
                reservedCardIndex.complete(index);
                reservedCards.remove(index);
            }
            if (!action.isDone()) {
                action.complete(3);
            }
        });

        reservedCardPreview.getChildren().add(cardImage);
        reservedCardPreview.setVisible(true);
        reservedCardPreview.setManaged(true);
    }


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
        shipGrid.getChildren().clear();
        shipGrid.setPrefSize(432, 309);
        shipGrid.setMinSize(432, 309);
        shipGrid.setMaxSize(432, 309);

        CardComponent[][] shipboard=gui.getClient().getPlayer_local().getShip().getShipBoard();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane cell = new StackPane();

                cell.setPrefSize(62, 62);
                cell.setMinSize(62, 62);
                cell.setMaxSize(62, 62);

                if (shipboard[i][j].getComponentType()== ComponentType.NotAccessible) {
                    cell.setStyle("-fx-background-color: transparent;");
                } else {
                    if(shipboard[i][j].getComponentType()==ComponentType.Empty) {
                        cell.setPrefSize(62,62);
                        cell.setStyle("-fx-background-color: transparent;");
                        final int a = i;
                        final int b = j;
                        cell.setOnMouseClicked(e -> {
                            coords.complete(new Pair<>(a, b));
                            if (gui.getRandomcardcontroller().getStage() != null) {
                                System.out.println("chiudo lo stage");
                                gui.getRandomcardcontroller().getStage().close();
                            }
                        });
                    }

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
                        imageView.setFitWidth(62);
                        imageView.setFitHeight(62);
                        imageView.setPreserveRatio(false);
                        cell.getChildren().add(imageView);
                    }
                }

                shipGrid.add(cell, j, i);
            }
        }
    }

    public CompletableFuture<Pair<Integer,Integer>> getCoords() {
        if (coords == null || coords.isDone()) {
            coords = new CompletableFuture<>();
        }
        return coords;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }


    public void setupPlayerButtons(List<Player> otherPlayers) {
        Platform.runLater(() -> {
            playersButtonBox.getChildren().clear();
            for (Player p : otherPlayers) {
                Button playerButton = new Button(p.getNickname());
                playerButton.setOnAction(e -> showShipForPlayer(p.getNickname()));
                //playerButton.getStyleClass().add("player-button"); // se vuoi uno stile CSS
                playersButtonBox.getChildren().add(playerButton);
            }
        });
    }

    public void setupCrewmatesButtons(List<CrewmateType> crewmateTypes) {
        Platform.runLater(() -> {
            crewmateButtonBox.getChildren().clear();
            for (CrewmateType c : crewmateTypes) {
                Button crewmateButton = new Button(c.name());
                crewmateButton.setOnAction(e -> {
                    if(c==CrewmateType.PinkAlien){
                        crewmate.complete(2);
                    }
                    if(c==CrewmateType.BrownAlien){
                        crewmate.complete(3);
                    }
                    if(c==CrewmateType.Astronaut){
                        crewmate.complete(1);
                    }
                });
                crewmateButtonBox.getChildren().add(crewmateButton);
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

    public CompletableFuture<Integer> getCrewmate() {
        if (crewmate == null) {
            crewmate = new CompletableFuture<>();
        }
        return crewmate;
    }

    public void highlightCell(Pair<Integer, Integer> coords) {
        int y = coords.getKey();
        int x = coords.getValue();

        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                // Se la cella contiene un'immagine, applica il bordo all'immagine
                if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
                    imageView.setStyle("-fx-effect: dropshadow(gaussian, gold, 5, 0.8, 0, 0); -fx-border-color: gold; -fx-border-width: 3px;");
                } else {
                    // Se la cella è vuota, applica il bordo alla StackPane
                    cell.setStyle("-fx-border-color: gold; -fx-border-width: 3px;");
                }
                return;
            }
        }
    }

    public void resetHighlights(Pair<Integer, Integer> coords) {
        int y = coords.getKey();
        int x = coords.getValue();

        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                // Se la cella contiene un'immagine, rimuovi gli effetti dall'immagine
                if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
                    imageView.setStyle(""); // Rimuovi tutti gli stili
                } else {
                    // Se la cella è vuota, rimuovi il bordo dalla StackPane
                    cell.setStyle("-fx-background-color: lightyellow;");
                }
                return;
            }
        }

    }




    public void placeCardOnShip(CardComponent card, Pair<Integer, Integer> coords) {
        int y = coords.getKey(); // RIGA
        int x = coords.getValue(); // COLONNA
        if((y==0&x==0)||(y==1&x==0)||(y==0&x==1)||(y==0&x==3)||(y==1&x==6)||(y==0&x==5)||(y==0&x==6)){
            gui.showMessage("Posizione non valida!");
            return;
        }
        if(pickedcoords.contains(coords)){
            gui.showMessage("Posizione già presa!");
            return;
        }
        pickedcoords.add(coords);
        String imagePath = card.getImagePath();

        if (imagePath == null) return;

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(62);
        imageView.setFitHeight(62);
        imageView.setPreserveRatio(false);

        imageView.setRotate(card.getRotationAngle());

        // Cerca la cella corretta nella GridPane
        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);


            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                cell.getChildren().clear();
                cell.getChildren().add(imageView);
                shipGrid.requestLayout();
                shipGrid.layout();
                break;
            }
        }

        // Aggiorna il modello della nave con la carta piazzata (opzionale)
        CardComponent[][] shipboard = gui.getClient().getPlayer_local().getShip().getShipBoard();
        shipboard[y][x] = card;
    }

    public void showShipForPlayer(String nickname) {
        Optional<Player> optionalPlayer = gui.getClient().getOther_players_local().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst();

        if (optionalPlayer.isEmpty()) {
            gui.showMessage("Giocatore non trovato!");
            return;
        }

        Player player = optionalPlayer.get();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlayerShipView.fxml"));
            Parent root = loader.load();

            PlayerShipController controller = loader.getController();
            controller.setBuildcontroller(this);

            System.out.println("debug,nave di:"+player.getNickname());
            System.out.println("la shipboard invece è"+player.getShip().getShipBoard());
            controller.setPlayerShip(player.getNickname(), player.getShip().getShipBoard());

            Stage stage = new Stage();
            stage.setTitle("Nave di " + player.getNickname());
            stage.setScene(new Scene(root));
            setPlayerStage(stage);
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
            gui.showMessage("Errore nel caricamento della schermata nave.");
        }
    }



}
