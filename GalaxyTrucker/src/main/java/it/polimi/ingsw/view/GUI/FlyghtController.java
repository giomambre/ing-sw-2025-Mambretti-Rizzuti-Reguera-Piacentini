package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.model.enumerates.ComponentType.Battery;


public class FlyghtController {
    private GUI gui;
    private CompletableFuture<Integer> adventureCardAction = new CompletableFuture<>();
    private List<Player> players = new ArrayList<>();
    List<Pair<Integer,Integer>> batteries = new ArrayList<>();
    private Map<String, ImageView> playerPawns = new HashMap<>();
    private Map<Integer, Player> playerPositions = new HashMap<>(); // posizione -> Player
    private Map<Integer, Player> playerLaps = new HashMap<>(); // posizione -> Player (per i giri)
    private CompletableFuture<Boolean> useDoubleCannon;
    private CompletableFuture<Boolean> acceptAdventure;
    private CompletableFuture<Pair<Integer, Integer>> coordsBattery;

    // FXML Components
    @FXML
    private GridPane boardGrid; // Board centrale 5x5

    @FXML
    private GridPane playerShipGrid; // Nave del player (basso a destra)

    @FXML
    private VBox adventureCardArea; // Area carte avventura (alto a destra)

    @FXML
    private HBox playersInfoBox; // Info giocatori

    @FXML
    private Label currentPlayerLabel;

    @FXML
    private Button endTurnButton;

    @FXML
    private Button drawAdventureCardButton;

    @FXML
    private Label dclabel;

    @FXML
    private Button yesdc;

    @FXML
    private Button nodc;

    @FXML
    private Button dontusebattery;

    // Dimensioni della board
    private static final int BOARD_SIZE = 5;
    private static final int CELL_SIZE = 80;
    private static final int SHIP_CELL_SIZE = 40;

    /**
     * Inizializza il controller
     */
    public void initialize() {
        setupBoardGrid();
        setupPlayerShipGrid();
        setupAdventureCardArea();
    }

    public List<Pair<Integer,Integer>> getBatteries() {
        return batteries;
    }

    public CompletableFuture<Pair<Integer,Integer>> getcoordsBattery() {
        if (coordsBattery == null) {
            coordsBattery = new CompletableFuture<>();
        }
        return coordsBattery;
    }

    public void resetcoordsBattery(){
        coordsBattery = new CompletableFuture<>();
    }

    public void showBatteries(Ship ship) {
        batteries.clear();
        if (this.coordsBattery == null || this.coordsBattery.isDone()) {
            this.coordsBattery = new CompletableFuture<>();
        }
        final CompletableFuture<Pair<Integer,Integer>> currentCoordsBatteryFuture = this.coordsBattery; // Capture it for the lambda

        Platform.runLater(() -> {

            dontusebattery.setVisible(true);
            dontusebattery.setOnAction((ActionEvent event) -> {
                currentCoordsBatteryFuture.complete(new Pair<>(-1,-1));
                clearShipListeners(ship);
            });
            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);
                    if (cell != null) {
                        cell.setStyle("");
                        cell.setOnMouseClicked(null);
                        CardComponent component = ship.getShip_board()[i][j];
                        if (component == null || component.getComponentType() == ComponentType.NotAccessible || component.getComponentType() == ComponentType.Empty
                                ||  component.getComponentType() == ComponentType.MainUnitRed || component.getComponentType() == ComponentType.MainUnitGreen
                                || component.getComponentType() == ComponentType.MainUnitBlue || component.getComponentType() == ComponentType.MainUnitYellow) {
                            cell.setStyle("-fx-background-color: lightgray;");
                        }
                        if (component != null && component.getComponentType() == Battery) {
                            if (((Battery) component).getStored() > 0) {
                                batteries.add(new Pair<>(i, j));
                            }
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

            // Fase 2: Evidenzia e imposta i listener solo per i connettori invalidi correnti
            for (Pair<Integer, Integer> currentCoords : batteries) {
                int row = currentCoords.getKey();
                int col = currentCoords.getValue();

                StackPane cell = (StackPane) playerShipGrid.getChildren().get(row * ship.getShip_board()[0].length + col);

                if (cell != null) {
                    highlightCell(row, col);
                    cell.setOnMouseClicked(e -> {
                        ((Battery) ship.getComponent(row, col)).removeBattery();
                        if (currentCoordsBatteryFuture != null && !currentCoordsBatteryFuture.isDone()) {
                            currentCoordsBatteryFuture.complete(new Pair<>(row, col));
                        }
                        clearShipListeners(ship);
                        cell.setOnMouseClicked(null);
                    });
                    cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                }
            }

        });
    }



    public void clearShipListeners(Ship ship) {
        Platform.runLater(() -> {
            dontusebattery.setVisible(false);
            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);
                    if (cell != null) {
                        cell.setOnMouseClicked(null);
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                        resetHighlights(i,j);
                    }
                }
            }
        });
    }


    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }


    public CompletableFuture<Boolean> getUseDoubleCannon() {
        if (useDoubleCannon == null) {
            useDoubleCannon = new CompletableFuture<>();
        }
        return useDoubleCannon;
    }

    public void resetUseDC(){
        useDoubleCannon = new CompletableFuture<>();
    }

    public void showdc(int x, int y) {
        Platform.runLater(() -> {
        dclabel.setText("Decidere se usare o no il cannone alle coordinate (" + x + ", " + y + ")");
        dclabel.setVisible(true);
        yesdc.setVisible(true);
        yesdc.setOnAction((ActionEvent event) -> {useDoubleCannon.complete(true);
        hidedc();});
        nodc.setVisible(true);
        nodc.setOnAction((ActionEvent event) -> {useDoubleCannon.complete(false);
        hidedc();});
        });
    }



    public void hidedc() {

        dclabel.setVisible(false);
        yesdc.setVisible(false);
        nodc.setVisible(false);
    }

    public Boolean acceptAdv(String prompt) {

        acceptAdventure = new CompletableFuture<>();

        Platform.runLater(() -> {
            dclabel.setText(prompt);
            dclabel.setVisible(true);
            yesdc.setVisible(true);
            yesdc.setOnAction((ActionEvent event) -> {
                acceptAdventure.complete(true);
                hidedc();
            });
            nodc.setVisible(true);
            nodc.setOnAction((ActionEvent event) -> {
                acceptAdventure.complete(false);
                hidedc();
            });
        });

        try {
            return acceptAdventure.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Imposta la GUI di riferimento
     */
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * Configura la griglia della board centrale (5x5)
     */
    private void setupBoardGrid() {
        boardGrid.getChildren().clear();
        boardGrid.setPrefSize(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
        boardGrid.setMinSize(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);
        boardGrid.setMaxSize(BOARD_SIZE * CELL_SIZE, BOARD_SIZE * CELL_SIZE);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                StackPane cell = createBoardCell(i, j);
                boardGrid.add(cell, j, i);
            }
        }
    }

    /**
     * Crea una singola cella della board (non cliccabile, solo visualizzazione)
     */
    private StackPane createBoardCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);

        // Stile della cella (non cliccabile)
        cell.setStyle("-fx-border-color: black; -fx-border-width: 1px; " +
                "-fx-background-color: lightblue;");

        return cell;
    }

    /**
     * Configura la griglia della nave del player (basso a destra)
     */
    private void setupPlayerShipGrid() {
        playerShipGrid.getChildren().clear();
        playerShipGrid.setPrefSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);
        playerShipGrid.setMinSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);
        playerShipGrid.setMaxSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);

        // Inizializza la griglia della nave (7x5 come nel Buildcontroller)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane cell = createShipCell(i, j);
                playerShipGrid.add(cell, j, i);
            }
        }
    }

    /**
     * Crea una singola cella della nave
     */
    private StackPane createShipCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);
        cell.setMinSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);
        cell.setMaxSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);

        // Posizioni non accessibili (come nel Buildcontroller)
        if ((row == 0 && col == 0) || (row == 1 && col == 0) ||
                (row == 0 && col == 1) || (row == 0 && col == 3) ||
                (row == 1 && col == 6) || (row == 0 && col == 5) ||
                (row == 0 && col == 6)) {
            cell.setStyle("-fx-background-color: transparent;");
        } else {
            cell.setStyle("-fx-border-color: gray; -fx-border-width: 1px; " +
                    "-fx-background-color: lightyellow;");
        }

        return cell;
    }

    /**
     * Configura l'area delle carte avventura
     */
    private void setupAdventureCardArea() {
        adventureCardArea.getChildren().clear();
        adventureCardArea.setPrefWidth(200);
        adventureCardArea.setSpacing(10);

        Label titleLabel = new Label("Carte Avventura");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        adventureCardArea.getChildren().add(titleLabel);
    }

    /**
     * Aggiorna la visualizzazione della nave del player
     */
    public void updatePlayerShip() {
        if (gui == null || gui.getClient() == null) return;

        Platform.runLater(() -> {
            Player localPlayer = gui.getClient().getPlayer_local();
            if (localPlayer == null || localPlayer.getShip() == null) return;

            CardComponent[][] shipBoard = localPlayer.getShip().getShipBoard();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cell = getShipCellAt(i, j);
                    if (cell == null) continue;

                    cell.getChildren().clear();

                    CardComponent component = shipBoard[i][j];
                    if (component != null &&
                            component.getComponentType() != ComponentType.Empty &&
                            component.getComponentType() != ComponentType.NotAccessible) {

                        String imagePath = component.getImagePath();
                        if (imagePath != null) {
                            try {
                                Image image = new Image(Objects.requireNonNull(
                                        getClass().getResourceAsStream(imagePath)));
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(SHIP_CELL_SIZE - 2);
                                imageView.setFitHeight(SHIP_CELL_SIZE - 2);
                                imageView.setPreserveRatio(false);
                                imageView.setRotate(component.getRotationAngle());
                                cell.getChildren().add(imageView);
                            } catch (Exception e) {
                                System.err.println("Errore nel caricamento immagine: " + imagePath);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Ottiene la cella della nave alle coordinate specificate
     */
    private StackPane getShipCellAt(int row, int col) {
        for (Node node : playerShipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == col && rowIndex == row && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null;
    }

    /**
     * Aggiorna le posizioni dei giocatori sulla board usando le Map posizione -> Player
     */
    public void updatePlayerPositions(Map<Integer, Player> positions, Map<Integer, Player> laps) {
        this.playerPositions = positions;
        this.playerLaps = laps;

        Platform.runLater(() -> {
            // Rimuovi tutte le pedine esistenti
            clearPlayerPawns();

            // Aggiungi le pedine per ogni posizione nella Map
            for (Map.Entry<Integer, Player> entry : positions.entrySet()) {
                int position = entry.getKey();
                Player player = entry.getValue();

                if (player != null) {
                    // Ottieni il numero di giri per questo giocatore (se presente)
                    Player lapPlayer = laps.get(position);
                    int lapCount = 0;

                    if (lapPlayer != null && lapPlayer.getNickname().equals(player.getNickname())) {
                        lapCount = lapPlayer.getNum_laps();
                    }

                    addPlayerPawnAtPosition(player, position, lapCount);
                }
            }
        });
    }

    /**
     * Rimuove tutte le pedine dalla board
     */
    private void clearPlayerPawns() {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.getChildren().removeIf(child ->
                        child instanceof VBox && // Cambiato da ImageView a VBox per contenere pedina + label
                                playerPawns.containsValue(((VBox) child).getChildren().get(0)));
            }
        }
        playerPawns.clear();
    }

    /**
     * Aggiunge la pedina di un giocatore sulla board alla posizione specificata con il numero di giri
     */
    private void addPlayerPawnAtPosition(Player player, int position, int lapCount) {
        // Converte la posizione lineare (0-24) in coordinate di griglia (row, col)
        int playerRow = position / BOARD_SIZE;
        int playerCol = position % BOARD_SIZE;

        StackPane cell = getBoardCellAt(playerRow, playerCol);
        if (cell == null) return;

        // Crea un contenitore verticale per pedina + label giri
        VBox playerContainer = new VBox();
        playerContainer.setSpacing(2);
        playerContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Crea l'immagine della pedina con il colore del player
        ImageView pawn = createPlayerPawn(player);
        playerPawns.put(player.getNickname(), pawn);

        // Crea il label con nickname e giri
        Label playerInfo = new Label(player.getNickname() + " (" + lapCount + ")");
        playerInfo.setStyle("-fx-font-size: 8px; -fx-text-fill: black; " +
                "-fx-background-color: white; -fx-background-radius: 3px; " +
                "-fx-padding: 1px 3px;");

        playerContainer.getChildren().addAll(pawn, playerInfo);
        cell.getChildren().add(playerContainer);
    }

    /**
     * Crea la pedina visuale per un giocatore con il suo colore
     */
    private ImageView createPlayerPawn(Player player) {
        ImageView pawn = new ImageView();
        pawn.setFitWidth(25);
        pawn.setFitHeight(25);
        pawn.setPreserveRatio(true);

        // Prima prova a caricare un'immagine specifica per il colore
        String pawnImagePath = getPawnImagePath(player.getColor());

        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(pawnImagePath)));
            pawn.setImage(image);
        } catch (Exception e) {
            // Fallback: crea una pedina colorata semplice come cerchio
            pawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px; -fx-border-color: black; " +
                    "-fx-border-width: 2px; -fx-border-radius: 12px;");

            // Se non c'è immagine, crea un piccolo cerchio colorato
            StackPane coloredPawn = new StackPane();
            coloredPawn.setPrefSize(25, 25);
            coloredPawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px; -fx-border-color: black; " +
                    "-fx-border-width: 2px; -fx-border-radius: 12px;");

            // Restituisce l'ImageView ma con uno stile colorato
            pawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px;");
        }

        return pawn;
    }

    /**
     * Ottiene il percorso dell'immagine della pedina basato sul colore
     */
    private String getPawnImagePath(Color color) {
        switch (color) {
            case BLUE:
                return "/images/pawns/pawn_blue.png";
            case RED:
                return "/images/pawns/pawn_red.png";
            case GREEN:
                return "/images/pawns/pawn_green.png";
            case YELLOW:
                return "/images/pawns/pawn_yellow.png";
            default:
                return "/images/pawns/pawn_default.png";
        }
    }

    /**
     * Ottiene il codice esadecimale del colore
     */
    private String getColorHex(Color color) {
        switch (color) {
            case BLUE:
                return "#0066CC";
            case RED:
                return "#CC0000";
            case GREEN:
                return "#00CC00";
            case YELLOW:
                return "#CCCC00";
            default:
                return "#888888";
        }
    }

    /**
     * Ottiene la cella della board alle coordinate specificate
     */
    private StackPane getBoardCellAt(int row, int col) {
        for (Node node : boardGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == col && rowIndex == row && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null;
    }

    /**
     * Muove la pedina di un giocatore da una posizione all'altra
     */
    public void movePlayerPawn(String nickname, int oldPosition, int newPosition) {
        Platform.runLater(() -> {
            // Rimuovi la pedina dalla posizione precedente
            if (oldPosition >= 0 && oldPosition < 25) {
                int oldRow = oldPosition / BOARD_SIZE;
                int oldCol = oldPosition % BOARD_SIZE;
                StackPane oldCell = getBoardCellAt(oldRow, oldCol);
                if (oldCell != null) {
                    ImageView pawn = playerPawns.get(nickname);
                    if (pawn != null) {
                        // Rimuovi il VBox contenitore della pedina
                        oldCell.getChildren().removeIf(child ->
                                child instanceof VBox &&
                                        ((VBox) child).getChildren().contains(pawn));
                    }
                }
            }

            // Aggiungi la pedina alla nuova posizione
            if (newPosition >= 0 && newPosition < 25) {
                int newRow = newPosition / BOARD_SIZE;
                int newCol = newPosition % BOARD_SIZE;
                StackPane newCell = getBoardCellAt(newRow, newCol);
                if (newCell != null) {
                    // Trova il player e i suoi giri per la nuova posizione
                    Player player = playerPositions.get(newPosition);
                    Player lapPlayer = playerLaps.get(newPosition);
                    int lapCount = 0;

                    if (player != null && player.getNickname().equals(nickname)) {
                        if (lapPlayer != null && lapPlayer.getNickname().equals(nickname)) {
                            // lapCount = lapPlayer.getLaps(); // Da implementare
                        }
                        addPlayerPawnAtPosition(player, newPosition, lapCount);
                    }
                }
            }

            // Aggiorna la Map delle posizioni
            Player player = playerPositions.get(oldPosition);
            if (player != null && player.getNickname().equals(nickname)) {
                playerPositions.remove(oldPosition);
                playerPositions.put(newPosition, player);
            }
        });
    }

    /**
     * Aggiunge una carta avventura all'area delle carte
     */
    public void addAdventureCard(CardAdventure card) {
        Platform.runLater(() -> {
            try {
                Image image = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(card.getImagePath())));
                ImageView cardView = new ImageView(image);
                cardView.setFitWidth(80);
                cardView.setFitHeight(120);
                cardView.setPreserveRatio(true);

                cardView.setOnMouseClicked(e -> {
                    if (!adventureCardAction.isDone()) {
                        adventureCardAction.complete(1);
                    }
                });

                adventureCardArea.getChildren().add(cardView);
            } catch (Exception e) {
                System.err.println("Errore nel caricamento carta avventura: " +
                        card.getImagePath());
            }
        });
    }

    /**
     * Pulisce l'area delle carte avventura
     */
    public void clearAdventureCards() {
        Platform.runLater(() -> {
            adventureCardArea.getChildren().removeIf(node -> node instanceof ImageView);
        });
    }

    // Getter per i CompletableFuture

    public CompletableFuture<Integer> getAdventureCardAction() {
        if (adventureCardAction == null || adventureCardAction.isDone()) {
            adventureCardAction = new CompletableFuture<>();
        }
        return adventureCardAction;
    }

    // Reset methods

    public void resetAdventureCardAction() {
        adventureCardAction = new CompletableFuture<>();
    }

    // Getter per le posizioni dei player

    public Map<Integer, Player> getPlayerPositions() {
        return new HashMap<>(playerPositions);
    }

    // Utility methods

    /**
     * Ottiene la posizione di un giocatore sulla board
     */
    public int getPlayerPosition(String nickname) {
        return playerPositions.entrySet().stream()
                .filter(entry -> entry.getValue().getNickname().equals(nickname))
                .mapToInt(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Aggiorna il label del giocatore corrente
     */
    public void updateCurrentPlayer(String playerName) {
        Platform.runLater(() -> {
            if (currentPlayerLabel != null) {
                currentPlayerLabel.setText("Turno di: " + playerName);
            }
        });
    }

    public void highlightCell(int y,int x) {

        for (Node node : playerShipGrid.getChildren()) {
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

    public void resetHighlights(int y,int x) {

        for (Node node : playerShipGrid.getChildren()) {
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


    /*public CompletableFuture<Pair<Integer, Integer>> getBatterySelectionFuture() {
        if (batterySelectionFuture == null) {
            batterySelectionFuture = new CompletableFuture<>();
        }
        return batterySelectionFuture;
    }

    public void resetBatterySelectionFuture() {
        batterySelectionFuture = null; // Resetta per il prossimo utilizzo
        isWaitingForBatterySelection.set(false);
    }

    /**
     * Mostra un alert per chiedere all'utente se vuole usare la batteria per il motore doppio.
     * Restituisce un CompletableFuture<Boolean> che si completa con la scelta.
     * True se vuole usare la batteria, False altrimenti.
     */
    /*public CompletableFuture<Boolean> askUseBatteryConfirmation(int engineRow, int engineCol) {
        CompletableFuture<Boolean> futureConfirmation = new CompletableFuture<>();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Motore Doppio");
            alert.setHeaderText("Motore Doppio trovato a RIGA: " + engineRow + " COLONNA: " + engineCol);
            alert.setContentText("Vuoi usare una batteria per attivare questo motore?");

            ButtonType buttonUseBattery = new ButtonType("Usa Batteria");
            ButtonType buttonDoNotUseBattery = new ButtonType("Non usare Batteria");

            alert.getButtonTypes().setAll(buttonUseBattery, buttonDoNotUseBattery);

            Optional<ButtonType> result = alert.showAndWait();

            futureConfirmation.complete(result.isPresent() && result.get() == buttonUseBattery);
        });
        return futureConfirmation;
    }

    /**
     * Evidenzia le celle delle batterie disponibili e imposta il listener per la selezione.
     *
     * @param batteryPositions Lista delle posizioni delle batterie disponibili.
     */
    /*public void highlightAndEnableBatterySelection(List<Pair<Integer, Integer>> batteryPositions) {
        Platform.runLater(() -> {
            isWaitingForBatterySelection.set(true); // Imposta lo stato di attesa


            // Aggiungi un listener per i click sulla griglia per catturare la selezione della batteria
            playerShipGrid.getChildren().forEach(node -> {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);

                if (colIndex == null) colIndex = 0;
                if (rowIndex == null) rowIndex = 0;

                Pair<Integer, Integer> currentCellCoords = new Pair<>(rowIndex, colIndex);

                if (node instanceof StackPane cell && batteryPositions.contains(currentCellCoords)) {
                    // Aggiungi il listener solo alle celle che contengono batterie valide
                    cell.setOnMouseClicked(event -> {
                        if (isWaitingForBatterySelection.get()) { // Controlla lo stato
                            // Completa il future con la posizione della batteria selezionata
                            getBatterySelectionFuture().complete(currentCellCoords);
                            // Rimuovi l'highlight e i listener dopo la selezione
                            cell.setOnMouseClicked(null); // Rimuovi il listener da questa cella
                            isWaitingForBatterySelection.set(false); // Resetta lo stato
                        }
                    });
                }
            });
        });
    }*/

    /*public void askBattery(int engineRow, int engineCol) {
        CompletableFuture<Integer> choice = new CompletableFuture<>();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Motore Doppio");
            alert.setHeaderText("Motore Doppio trovato a RIGA: " + engineRow + " COLONNA: " + engineCol);
            alert.setContentText("Vuoi usare una batteria per attivare questo motore?");

            ButtonType buttonUseBattery = new ButtonType("Usa Batteria");
            ButtonType buttonDoNotUseBattery = new ButtonType("Non usare Batteria");

            alert.getButtonTypes().setAll(buttonUseBattery, buttonDoNotUseBattery);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == buttonUseBattery) {
                choice.complete(1);
                setBatteryChoice(1);
            } else {
                choice.complete(2);
                 setBatteryChoice(1);
            }
        });
    }*/


    /*public void setBatteryChoice(int choice) {
        if (batterychoice != null) {
            if(choice==1){
                batterychoice.complete(gui.useBattery(gui.getClient().getPlayer_local().getShip()));
            }else{
                batterychoice.complete(new Pair<>(-1,-1));
            }
        }
    }

    public CompletableFuture<Pair<Integer,Integer>> getBatteryChoice() {
        if (batterychoice == null) {
            batterychoice = new CompletableFuture<>();
        }
        return batterychoice;
    }

    public void resetChoice() {
        batterychoice = new CompletableFuture<>();
    }*/

}
