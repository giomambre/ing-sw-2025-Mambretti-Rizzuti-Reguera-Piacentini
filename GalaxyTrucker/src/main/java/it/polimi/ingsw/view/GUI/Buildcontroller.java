package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
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

import static it.polimi.ingsw.model.enumerates.ConnectorType.Empty_Connector;
import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.West;

public class Buildcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    private CompletableFuture<Integer> crewmate = new CompletableFuture<>();
    private List<Pair<Integer,Integer>> pickedcoords = new ArrayList<>();
    private CardComponent[][] currentShipBoard;
    private List<Pair<Integer, Integer>> invalidConnectors;
    private CompletableFuture<Ship> shipUpdateFuture;
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
                cardImage.setFitWidth(62);
                cardImage.setFitHeight(62);
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
        cardImage.setFitWidth(62);
        cardImage.setFitHeight(61);
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
            view.setFitWidth(62);
            view.setFitHeight(62);
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
                                gui.getRandomcardcontroller().getStage().close();
                            }
                        });
                    }

                }
                if (i == 2 && j == 3) {
                    Color color = gui.getClient().getPlayer_local().getColor();
                    String imagePath = null;


                    Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Empty_Connector);
                    connectors.put(South, Empty_Connector);
                    connectors.put(East, Empty_Connector);
                    connectors.put(West, Empty_Connector);


                    switch (color) {
                        case BLUE:
                            imagePath = "/images/cardComponent/GT-mainUnitBlue.jpg";
                            shipboard[i][j] = new CardComponent(ComponentType.MainUnitBlue,connectors,imagePath);

                            break;
                        case RED:
                            imagePath = "/images/cardComponent/GT-mainUnitRed.jpg";
                            shipboard[i][j] = new CardComponent(ComponentType.MainUnitRed,connectors,imagePath);

                            break;
                        case GREEN:
                            imagePath = "/images/cardComponent/GT-mainUnitGreen.jpg";
                            shipboard[i][j] = new CardComponent(ComponentType.MainUnitGreen,connectors,imagePath);

                            break;
                        case YELLOW:
                            imagePath = "/images/cardComponent/GT-mainUnitYellow.jpg";
                            shipboard[i][j] = new CardComponent(ComponentType.MainUnitYellow,connectors,imagePath);

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


    public int placeCardOnShip(CardComponent card, Pair<Integer, Integer> coords) {
        int y = coords.getKey(); // RIGA
        int x = coords.getValue(); // COLONNA
        if((y==0&x==0)||(y==1&x==0)||(y==0&x==1)||(y==0&x==3)||(y==1&x==6)||(y==0&x==5)||(y==0&x==6)){
            gui.showMessage("Posizione non valida!");
            return 0 ;
        }
        if(pickedcoords.contains(coords)){
            gui.showMessage("Posizione già presa!");
            return 0 ;
        }
        pickedcoords.add(coords);
        String imagePath = card.getImagePath();

        if (imagePath == null) return 0;

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
        return 1;
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




    public void printShip(Ship ship, List<Pair<Integer, Integer>> connectors) {
        this.invalidConnectors = connectors;
        while(!connectors.isEmpty()) {
            Platform.runLater(() -> {
                shipGrid.getChildren().clear();

                for (int i = 0; i < ship.getShip_board().length; i++) {
                    for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                        StackPane cell = new StackPane();
                        cell.setPrefSize(40, 40);

                        CardComponent component = ship.getShip_board()[i][j];
                        final int row = i;
                        final int col = j;
                        Pair<Integer, Integer> currentCoords = new Pair<>(row, col);

                        if (component == null || component.getComponentType() == ComponentType.NotAccessible || component.getComponentType() == ComponentType.Empty) {
                            if (component == null) {
                                System.out.println("DEBUG: shipBoard[" + i + "][" + j + "] is null");
                            }
                            cell.setStyle("-fx-background-color: lightgray;");
                        } else {
                            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(component.getImagePath())));
                            ImageView iv = new ImageView(img);
                            iv.setFitWidth(62);
                            iv.setFitHeight(62);
                            iv.setPreserveRatio(true);
                            iv.setRotate(component.getRotationAngle());
                            cell.getChildren().add(iv);
                        }

                        if (connectors.contains(currentCoords)) {
                            highlightCell(currentCoords);
                            cell.setOnMouseClicked(e -> {
                                ship.removeComponent(row, col);
                                removeImage(row, col);
                                connectors.remove(currentCoords);
                                if (invalidConnectors.isEmpty()) {
                                    gui.showMessage("Tutti i connettori invalidi sono stati rimossi!");
                                    if (shipUpdateFuture != null && !shipUpdateFuture.isDone()) {
                                        shipUpdateFuture.complete(ship);
                                    }
                                }
                            });
                            // Cambia il cursore per indicare che è cliccabile
                            cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                        }

                        shipGrid.add(cell, j, i);
                    }
                }

                // Mostra messaggio all'utente se ci sono connettori invalidi
                if (!connectors.isEmpty()) {
                    gui.showMessage("Clicca sulle carte evidenziate in rosso per rimuoverle (connettori invalidi)");
                }
            });
        }
    }

    public void removeImage(int i, int j) {
        Platform.runLater(() -> {
            updateCellVisually(i, j);
        });
    }

    // Metodo helper per aggiornare visivamente una singola cella
    private void updateCellVisually(int row, int col) {
        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == col && rowIndex == row && node instanceof StackPane cell) {
                // Pulisci la cella
                cell.getChildren().clear();
                cell.setOnMouseClicked(null);
                cell.setStyle("-fx-background-color: lightgray; -fx-cursor: default;");
                break;
            }
        }
    }

    // Metodo per ottenere la nave aggiornata in modo asincrono
    public CompletableFuture<Ship> getUpdatedShip() {
        if (shipUpdateFuture == null || shipUpdateFuture.isDone()) {
            shipUpdateFuture = new CompletableFuture<>();
        }
        return shipUpdateFuture;
    }

    // Metodo per ottenere la nave aggiornata attuale (sincrono)
    public CardComponent[][] getCurrentUpdatedShip() {
        return currentShipBoard;
    }


}
