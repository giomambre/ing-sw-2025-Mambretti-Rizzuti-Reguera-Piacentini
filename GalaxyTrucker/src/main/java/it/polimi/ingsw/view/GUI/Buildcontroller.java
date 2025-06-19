package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
                // Se la lista passata è già vuota all'inizio (es. dopo l'ultimo clic)
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
        System.out.println("CARTA TROVATA");

        StackPane cell = (StackPane) cardNode; // Ora il cast è sicuro e corretto
        Node overlay = createoverlayfortype(type);
        System.out.println("overlay trovato");
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


    /*public Node getCardPosition(int x,int y) {
        Node card = null;
        for (Node node : shipGrid.getChildren()) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                card = node;
            }
        }
        return card;
    }*/
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
                    img.setFitWidth(40);
                    img.setFitHeight(40);
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
                img.setFitWidth(40);
                img.setFitHeight(40);
                img.setMouseTransparent(true);
                img.setId("overlay-" + type);
                return img;
            }

            case "BrownAlien": {
                path = "/images/icons/brownAlien.png";
                ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                img.setFitWidth(40);
                img.setFitHeight(40);
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
                    batteryImg.setFitWidth(25);
                    batteryImg.setFitHeight(25);
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



}
