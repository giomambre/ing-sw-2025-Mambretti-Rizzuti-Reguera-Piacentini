package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static it.polimi.ingsw.model.enumerates.ConnectorType.Empty_Connector;
import static it.polimi.ingsw.model.enumerates.ConnectorType.Universal;
import static it.polimi.ingsw.model.enumerates.Direction.*;

public class Buildcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    private CompletableFuture<Integer> crewmate = new CompletableFuture<>();
    private List<Pair<Integer,Integer>> pickedcoords = new ArrayList<>();
    private CardComponent[][] currentShipBoard;
    private List<Pair<Integer, Integer>> invalidConnectors;
    private CompletableFuture<Ship> shipUpdateFuture;
    private Timeline blinkTimeline;
    private Map<Direction, List<CardAdventure>> local_adventure_deck;

    public void setLocal_adventure_deck(Map<Direction, List<CardAdventure>> local_adventure_deck) {
        this.local_adventure_deck = local_adventure_deck;
    }

    @FXML
    private Label timerLabel;

    private Timeline timeline;
    private int timeLeft;
    private final int TIME_LIMIT_SECONDS = 180;
    @FXML
    private VBox playersButtonBox;
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
    @FXML private Label timer;

    private final List<CardComponent> reservedCards = new ArrayList<>();


    private CompletableFuture<Pair<Integer,Integer>> coords = new CompletableFuture<>();


    private CompletableFuture<Integer> reservedCardIndex = new CompletableFuture<>();
    private CompletableFuture<Integer> faceupCardIndex = new CompletableFuture<>();

    private Stage playerStage;

    @FXML
    private void showSouthDeckCard() {
        String imagePath=local_adventure_deck.get(South).getFirst().getImagePath();
        openCardDisplayScreen("South Deck",imagePath);
    }

    @FXML
    private void showEastDeckCard() {
        String imagePath=local_adventure_deck.get(East).getFirst().getImagePath();
        openCardDisplayScreen("East Deck",imagePath);
    }

    @FXML
    private void showWestDeckCard() {
        String imagePath=local_adventure_deck.get(West).getFirst().getImagePath();
        openCardDisplayScreen("West Deck",imagePath);
    }

    /**
     * Metodo di supporto per aprire una nuova schermata per mostrare la carta in cima al mazzo.
     * @param deckName Il nome del mazzo (es. "South Deck")
     * @param cardImagePath Il percorso relativo dell'immagine della carta all'interno del progetto.
     * Ad esempio: "/it/polimi/ingsw/view/GUI/images/cards/my_card.png"
     */
    private void openCardDisplayScreen(String deckName, String cardImagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardDisplay.fxml"));
            Parent root = loader.load();

            CardDisplayController cardDisplayController = loader.getController();
            cardDisplayController.setDeckName(deckName);
            cardDisplayController.setCard(cardImagePath); // Passa il percorso dell'immagine e la descrizione

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Prima Carta del " + deckName);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            // Gestisci l'errore (es. mostra un alert all'utente)
        }
    }


    private void startBlinking() {
        blinkTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    String currentStyle = timerLabel.getStyle();
                    if (currentStyle.contains("transparent")) {
                        timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 25;  -fx-font-family: 'Verdana'");
                    } else {
                        timerLabel.setStyle("-fx-text-fill: transparent; -fx-font-size: 25;  -fx-font-family: 'Verdana'");
                    }
                })
        );
        blinkTimeline.setCycleCount(Animation.INDEFINITE);
        blinkTimeline.play();
    }

    private void stopBlinking() {
        if (blinkTimeline != null) {
            blinkTimeline.stop();
            blinkTimeline = null;
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 20; -fx-font-family: 'Verdana'");
        }
    }

    @FXML
    public void initialize() {
        timeLeft = 270;
        timerLabel.setText(formatTime(timeLeft));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (timeLeft > 0) {
                timeLeft--;
                timerLabel.setText(formatTime(timeLeft));

                if (timeLeft == 30) {
                    startBlinking();
                }
            } else {
                timeline.stop();
                stopBlinking();
                timerLabel.setText("Tempo Scaduto!");
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }


    public void starttimer(int seconds) {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
            timeLeft = seconds; // Reset del tempo
        Platform.runLater(() -> {
            timerLabel.setText(formatTime(timeLeft));
            timeline.playFromStart();
        });

    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // Metodo aggiornato per sincronizzare con facedUp_deck_local del Client
    public void updateFaceUpCardsDisplay() {
        Platform.runLater(() -> {
            faceupCardPreview.getChildren().clear();

            List<CardComponent> faceUpCards = gui.getClient().getFacedUp_deck_local();

            for (int i = 0; i < faceUpCards.size(); i++) {
                CardComponent card = faceUpCards.get(i);

                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(card.getImagePath())));
                ImageView cardImage = new ImageView(image);
                cardImage.setFitWidth(77.5);
                cardImage.setFitHeight(77.5);
                cardImage.setPreserveRatio(true);

                final int index = i;
                cardImage.setCursor(Cursor.HAND);
                cardImage.setOnMouseClicked(e -> {
                    System.out.println("carta scartata per testare ciao");
                    if (!faceupCardIndex.isDone()) {
                        faceupCardIndex.complete(index);
                    }
                    action.complete(2);
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
        cardImage.setFitWidth(77.5);
        cardImage.setFitHeight(76.25);
        cardImage.setPreserveRatio(true);

        int index = reservedCards.size() - 1;

        cardImage.setOnMouseClicked(e -> {
            if (!reservedCardIndex.isDone()) {
                reservedCardPreview.getChildren().remove(cardImage);
                reservedCardIndex.complete(reservedCards.indexOf(card));
                reservedCards.remove(card);
            }
            if (!action.isDone()) {
                System.out.println("action done");
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
            view.setFitWidth(77.5);
            view.setFitHeight(77.5);
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
        shipGrid.setPrefSize(540, 386.25);
        shipGrid.setMinSize(540, 386.25);
        shipGrid.setMaxSize(540, 386.25);

        CardComponent[][] shipboard=gui.getClient().getPlayer_local().getShip().getShipBoard();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {

                StackPane cell = new StackPane();

                cell.setPrefSize(77.5, 77.5);
                cell.setMinSize(77.5, 77.5);
                cell.setMaxSize(77.5, 77.5);

                if (shipboard[i][j].getComponentType()== ComponentType.NotAccessible) {
                    cell.setStyle("-fx-background-color: transparent;");
                } else {
                    if(shipboard[i][j].getComponentType()==ComponentType.Empty) {
                        cell.setPrefSize(77.5, 77.5);
                        cell.setStyle("-fx-background-color: transparent;");

                        final Effect originalEffect = cell.getEffect();

                        cell.setOnMouseEntered(e -> {
                            javafx.scene.paint.Color overlayColor = new javafx.scene.paint.Color(0, 0, 0, 0.2);
                            cell.setEffect(new ColorInput(0, 0, cell.getWidth(), cell.getHeight(), overlayColor));
                        });

                        cell.setOnMouseExited(e -> {
                            cell.setEffect(originalEffect);
                        });


                        final int a = i;
                        final int b = j;
                        if (i != 2 || j != 3) {
                            cell.setOnMouseClicked(e -> {
                                coords.complete(new Pair<>(a, b));

                                if (gui.getRandomcardcontroller().getStage() != null) {
                                    gui.getRandomcardcontroller().getStage().close();
                                }
                            });
                        }else
                        {
                            cell.setOnMouseEntered(null);
                            cell.setOnMouseExited(null);
                            cell.setEffect(null);
                            cell.setOnMouseClicked(null);
                        }
                    }

                }
                if (i == 2 && j == 3) {
                    Color color = gui.getClient().getPlayer_local().getColor();
                    String imagePath = null;
                    ;


                    Map<Direction, ConnectorType> connectors = new EnumMap<>(Direction.class);
                    connectors.put(North, Universal);
                    connectors.put(South, Universal);
                    connectors.put(East, Universal);
                    connectors.put(West, Universal);


                    switch (color) {
                        case BLUE:
                            imagePath = "/images/cardComponent/GT-mainUnitBlue.jpg";
                            shipboard[i][j] = new LivingUnit(ComponentType.MainUnitBlue,connectors,imagePath);

                            break;
                        case RED:
                            imagePath = "/images/cardComponent/GT-mainUnitRed.jpg";
                            shipboard[i][j] = new LivingUnit(ComponentType.MainUnitRed,connectors,imagePath);

                            break;
                        case GREEN:
                            imagePath = "/images/cardComponent/GT-mainUnitGreen.jpg";
                            shipboard[i][j] = new LivingUnit(ComponentType.MainUnitGreen,connectors,imagePath);

                            break;
                        case YELLOW:
                            imagePath = "/images/cardComponent/GT-mainUnitYellow.jpg";
                            shipboard[i][j] = new LivingUnit(ComponentType.MainUnitYellow,connectors,imagePath);

                            break;
                    }

                    if (imagePath != null) {
                        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(77.5);
                        imageView.setFitHeight(77.5);
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
                playerButton.getStyleClass().add("player-button");
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
        System.out.println("Attempting to highlight cell at: " + x + "," + y);

        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                System.out.println("Found cell at " + x + "," + y + ", applying highlight");

                String style = "-fx-background-color: rgba(255, 0, 0, 0.3);" +
                        "-fx-border-color: red;" +
                        "-fx-border-width: 3px;" +
                        "-fx-border-style: solid;";
                cell.setStyle(style);

                for (Node child : cell.getChildren()) {
                    if (child instanceof ImageView imageView) {
                        DropShadow ds = new DropShadow();
                        ds.setColor(javafx.scene.paint.Color.RED);
                        ds.setRadius(10);
                        ds.setSpread(0.7);
                        imageView.setEffect(ds);
                    }
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
                cell.setStyle("");
                for (Node child : cell.getChildren()) {
                    if (child instanceof ImageView iv) {
                        iv.setEffect(null);
                    }
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
        imageView.setFitWidth(77.5);
        imageView.setFitHeight(77.5);
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

                cell.setOnMouseEntered(null);
                cell.setOnMouseExited(null);
                cell.setEffect(null);

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


            controller.setPlayerShip(player.getNickname(), player.getShip().getShipBoard());
            controller.showCloseButton();

            Stage stage = new Stage();
            stage.setTitle("Nave di " + player.getNickname());
            stage.setScene(new Scene(root));
            setPlayerStage(stage);
            playerStage.setX(0);
            playerStage.setY(100);
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
            gui.showMessage("Errore nel caricamento della schermata nave.");
        }
    }



    public void printInvalidsConnector(Ship ship, List<Pair<Integer, Integer>> connectors) {
        this.invalidConnectors = connectors;

        Platform.runLater(() -> {
            // Fase 1: Reset di tutte le celle esistenti
            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) shipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);

                    if (cell != null) {
                        cell.setStyle("");
                        cell.setOnMouseClicked(null);
                        CardComponent component = ship.getShip_board()[i][j];
                        if (component == null || component.getComponentType() == ComponentType.NotAccessible || component.getComponentType() == ComponentType.Empty
                                ||  component.getComponentType() == ComponentType.MainUnitRed || component.getComponentType() == ComponentType.MainUnitGreen
                                || component.getComponentType() == ComponentType.MainUnitBlue || component.getComponentType() == ComponentType.MainUnitYellow) {
                            cell.setStyle("-fx-background-color: transparent;");
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

            // Fase 2: Evidenzia e imposta i listener solo per i connettori invalidi correnti
            for (Pair<Integer, Integer> currentCoords : connectors) {
                int row = currentCoords.getKey();
                int col = currentCoords.getValue();

                StackPane cell = (StackPane) shipGrid.getChildren().get(row * ship.getShip_board()[0].length + col);

                if (cell != null) {
                    highlightCell(currentCoords);
                    cell.setOnMouseClicked(e -> {
                        ship.removeComponent(row, col);
                        removeImage(row, col);



                        List<Pair<Integer, Integer>> updatedInvalids = ship.checkShipConnections();

                        if (updatedInvalids.isEmpty()) {
                            gui.showMessage("Tutti i connettori invalidi sono stati rimossi!");

                            if (shipUpdateFuture != null && !shipUpdateFuture.isDone()) {
                                shipUpdateFuture.complete(ship);
                            }
                        } else {
                            printInvalidsConnector(ship, updatedInvalids);
                        }
                    });
                    cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                }
            }

            if (!connectors.isEmpty()) {
                gui.showMessage("Clicca sulle carte evidenziate in rosso per rimuoverle (connettori invalidi)");
            } else {
                // Se la lista passata è già vuota all'inizio
                // Assicurati che il messaggio di successo sia comunque mostrato.
                // Questa parte potrebbe essere ridondante se la logica di 'if (updatedInvalids.isEmpty())' è sempre raggiunta.
                // Potrebbe essere utile se la funzione viene chiamata con una lista vuota dall'esterno.
            }
        });
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
                cell.setStyle("-fx-background-color: transparent; -fx-cursor: default;");
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


    public void addObject(int x, int y, String type) {
        Node cardNode = getCardPosition(x, y); // Il nodo recuperato

        if (cardNode == null) {
            System.out.println("CARTA NON TROVATA (cella Grid non trovata alle coordinate specificate)");
            return;
        }

        StackPane cell = (StackPane) cardNode; // Ora il cast è sicuro e corretto
        Node overlay = createoverlayfortype(type);
        if (overlay != null) { // Aggiungi l'overlay solo se è stato creato con successo
            cell.getChildren().add(overlay);
        } else {
            System.out.println("ERROR: Impossibile creare l'overlay per il tipo: " + type);
        }
    }

    public void removeObject(int x, int y, String type) {
        Node cardNode = getCardPosition(x, y);

        if (cardNode == null) {
            System.out.println("CARTA NON TROVATA (cella Grid non trovata alle coordinate specificate)");
            return;
        }

        StackPane cell = (StackPane) cardNode;

        // Trova e rimuovi l'overlay del tipo specificato
        Node overlayToRemove = null;
        for (Node child : cell.getChildren()) {
            if (isOverlayOfType(child, type)) {
                overlayToRemove = child;
                break;
            }
        }

        if (overlayToRemove != null) {
            cell.getChildren().remove(overlayToRemove);
            System.out.println("Overlay rimosso per il tipo: " + type);
        } else {
            System.out.println("ERROR: Nessun overlay trovato per il tipo: " + type);
        }
    }

    public void addBattery(int x, int y, String type, int count) {
        Node cardNode = getCardPosition(x, y);

        if (cardNode == null) {
            System.out.println("CARTA NON TROVATA (cella Grid non trovata alle coordinate specificate)");
            return;
        }
        System.out.println("CARTA TROVATA");

        StackPane cell = (StackPane) cardNode;
        Node overlay = createoverlayfortype(type, count);
        System.out.println("overlay trovato");
        if (overlay != null) {
            cell.getChildren().add(overlay);
        } else {
            System.out.println("ERROR: Impossibile creare l'overlay per il tipo: " + type);
        }
    }

    public void removeBattery(int x, int y, String type) {
        Node cardNode = getCardPosition(x, y);

        if (cardNode == null) {
            System.out.println("CARTA NON TROVATA (cella Grid non trovata alle coordinate specificate)");
            return;
        }
        System.out.println("CARTA TROVATA");

        StackPane cell = (StackPane) cardNode;

        // Trova e rimuovi l'overlay del tipo specificato
        Node overlayToRemove = null;
        for (Node child : cell.getChildren()) {
            if (isOverlayOfType(child, type)) {
                overlayToRemove = child;
                break;
            }
        }

        if (overlayToRemove != null) {
            cell.getChildren().remove(overlayToRemove);
            System.out.println("Overlay rimosso per il tipo: " + type);
        } else {
            System.out.println("ERROR: Nessun overlay trovato per il tipo: " + type);
        }
    }


    public Node getCardPosition(int x, int y) {
        for (Node node : shipGrid.getChildren()) {
            // *** MODIFICA QUI: Gestione dei valori null per gli indici ***
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Se gli indici non sono esplicitamente impostati, GridPane li considera 0
            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            // Ora si usa && per trovare la cella esatta
            if (colIndex == x && rowIndex == y) {
                return node; // Questo dovrebbe essere lo StackPane corretto
            }
        }
        return null; // Nessuna cella trovata alle coordinate specificate
    }

    public Node createoverlayfortype(String type) {
        return createoverlayfortype(type, -1); // -1 = valore di default ignorato
    }

    public Node createoverlayfortype(String type, int count) {
        String path;

        switch(type){
            case "Astronaut": {
                path = "/images/icons/astronautPawn.png";
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                for (int i = 0; i < 2; i++) {
                    ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    img.setFitWidth(50);
                    img.setFitHeight(50);
                    img.setPreserveRatio(true);
                    img.setSmooth(true);
                    img.setMouseTransparent(true);
                    container.getChildren().add(img);
                }
                return container;
            }

            case "PinkAlien": {
                path = "/images/icons/pinkAlien.png";
                ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                img.setFitWidth(50);
                img.setFitHeight(50);
                img.setMouseTransparent(true);
                img.setId("overlay-" + type);
                return img;
            }

            case "BrownAlien": {
                path = "/images/icons/brownAlien.png";
                ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                img.setFitWidth(50);
                img.setFitHeight(50);
                img.setMouseTransparent(true);
                img.setId("overlay-" + type);
                return img;
            }

            case "Battery": {
                path = "/images/icons/battery.png";
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);


                for (int i = 0; i < count; i++) {
                    ImageView batteryImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    batteryImg.setFitWidth(30);
                    batteryImg.setFitHeight(30);
                    batteryImg.setPreserveRatio(true);
                    batteryImg.setSmooth(true);
                    batteryImg.setMouseTransparent(true);
                    container.getChildren().add(batteryImg);
                }
                return container;
            }

            default:
                return null;
        }
    }

    private boolean isOverlayOfType(Node node, String type) {
        String nodeId = node.getId();
        return nodeId != null && nodeId.equals("overlay-" + type);
    }

    public void printShipImage( CardComponent[][] shipBoard) {
        Platform.runLater(() -> {

            for (Node node : shipGrid.getChildren()) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);

                CardComponent card = shipBoard[rowIndex][colIndex];

                String imagePath = card.getImagePath();

                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(77.5);
                imageView.setFitHeight(77.5);
                imageView.setPreserveRatio(false);

                imageView.setRotate(card.getRotationAngle());



                if (colIndex == null) colIndex = 0;
                if (rowIndex == null) rowIndex = 0;

                if (card.getComponentType()!= ComponentType.Empty && card.getComponentType()!= ComponentType.NotAccessible &&  node instanceof StackPane cell) {


                    cell.getChildren().clear();
                    cell.getChildren().add(imageView);

                    cell.setOnMouseEntered(null);
                    cell.setOnMouseExited(null);
                    cell.setEffect(null);

                    shipGrid.requestLayout();
                    shipGrid.layout();

                }
            }

            CardComponent[][] shipboard = gui.getClient().getPlayer_local().getShip().getShipBoard();

        });
    }

    public void disableShipGridCells() {
        for (javafx.scene.Node node : shipGrid.getChildren()) {

            StackPane cell = (StackPane) node;

            cell.setOnMouseClicked(null);
            cell.setOnMouseEntered(null);
            cell.setOnMouseExited(null);

            //cell.setOpacity(0.7);
            //cell.setStyle("-fx-background-color: #A9A9A9;");
        }
    }






}
