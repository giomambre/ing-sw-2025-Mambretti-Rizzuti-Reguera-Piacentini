package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Epidemic;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

import javax.smartcardio.Card;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;


public class FlyghtController {
    private GUI gui;
    private CompletableFuture<Integer> adventureCardAction = new CompletableFuture<>();
    private List<Player> players = new ArrayList<>();
    List<Pair<Integer,Integer>> batteries = new ArrayList<>();
    List<Pair<Integer,Integer>> livingUnits = new ArrayList<>();
    private Map<String, ImageView> playerPawns = new HashMap<>();
    private Map<Integer, Player> playerPositions = new HashMap<>(); // posizione -> Player
    private Map<Integer, Player> playerLaps = new HashMap<>(); // posizione -> Player (per i giri)
    private CompletableFuture<Boolean> useDoubleCannon;
    private CompletableFuture<Boolean> acceptAdventure;
    private CompletableFuture<Pair<Integer, Integer>> coordsBattery;
    private CompletableFuture<Boolean> useCard;
    private CompletableFuture<Pair<Integer, Integer>> astronautToRemove;
    private Stage playerStage;

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

    @FXML
    private Button accept;

    @FXML
    private Button reject;

    @FXML
    private Label choiceLabel;
    @FXML
    private Label playerCreditsLabel;

    @FXML
    private Label epidemicLabel;

    @FXML
    private Button continueButton;

    @FXML
    private VBox playersButtonBox;

    @FXML
    private Label infoLabel;

    // Dimensioni della board
    private static final int BOARD_SIZE = 5;
    private static final int CELL_WIDTH = 80;
    private static final int CELL_HEIGHT = 50;
    private static final int SHIP_CELL_SIZE = 75;

    public void showMessageLabel(String message) {
        Platform.runLater(() -> {
                infoLabel.setVisible(true);
                infoLabel.setText(message);
        });
    }
    public void hideInfoLabel() {
        Platform.runLater(() -> infoLabel.setVisible(false));
    }

    public void setPlayerStage(Stage playerStage) {
        this.playerStage = playerStage;
    }
    public Stage getPlayerStage() {
        return playerStage;
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
            controller.setFlyghtcontroller(this);

            System.out.println("debug,nave di:"+player.getNickname());
            System.out.println("la shipboard invece è"+player.getShip().getShipBoard());
            controller.setPlayerShip(player.getNickname(), player.getShip().getShipBoard());
            controller.showCloseButtonFlyght();

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

    public void updateCreditLabel(int credits) {
        Platform.runLater(() -> playerCreditsLabel.setText("Crediti: " + credits));
    }

    public CompletableFuture<Pair<Integer,Integer>> getAstronautToRemove() {
        if (astronautToRemove == null) {
            astronautToRemove = new CompletableFuture<>();
        }
        return astronautToRemove;
    }

    public void resetastronautToRemove(){
        astronautToRemove = new CompletableFuture<>();
    }

    public void showCrewmates(Ship ship) {
        livingUnits.clear();
        if (this.astronautToRemove == null || this.astronautToRemove.isDone()) {
            this.astronautToRemove = new CompletableFuture<>();
        }
        final CompletableFuture<Pair<Integer,Integer>> currentCoordsAstronautFuture = this.astronautToRemove;

        Platform.runLater(() -> {

            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);
                    if (cell != null) {
                        cell.setStyle("");
                        cell.setOnMouseClicked(null);
                        CardComponent component = ship.getShip_board()[i][j];
                        if (component == null || component.getComponentType() == ComponentType.NotAccessible || component.getComponentType() == ComponentType.Empty) {
                            cell.setStyle("-fx-background-color: transparent;");
                        }
                        if (component != null && component.getComponentType() == ComponentType.LivingUnit ||  component.getComponentType() == MainUnitRed
                                || component.getComponentType() == MainUnitYellow || component.getComponentType() == MainUnitGreen || component.getComponentType() == MainUnitBlue) {
                            LivingUnit unit = (LivingUnit) component;
                            if (unit.getNum_crewmates() > 0) {
                                livingUnits.add(new Pair<>(i, j));
                                //addOverlay(i, j, "Astronaut");
                            }
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

            // Fase 2: Evidenzia e imposta i listener solo per i connettori invalidi correnti
            for (Pair<Integer, Integer> currentCoords : livingUnits) {
                int row = currentCoords.getKey();
                int col = currentCoords.getValue();

                StackPane cell = (StackPane) playerShipGrid.getChildren().get(row * ship.getShip_board()[0].length + col);

                if (cell != null) {
                    highlightCell(row, col);
                    cell.setOnMouseClicked(e -> {
                        ((LivingUnit) ship.getComponent(row, col)).removeCrewmates( 1);
                        if (astronautToRemove != null && !astronautToRemove.isDone()) {
                            astronautToRemove.complete(new Pair<>(row, col));
                        }
                        updateCrewmateOverlayAt(row, col, ship);
                        clearCrewmates(ship);
                        updatePlayerShip();
                        cell.setOnMouseClicked(null);
                    });
                    cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                }
            }

        });
    }


    public void clearCrewmates(Ship ship) {
        Platform.runLater(() -> {
            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);
                    if (cell != null) {
                        cell.setOnMouseClicked(null);
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                        resetHighlights(i,j);
                        removeOverlay(i, j, "Astronaut");

                    }
                }
            }
        });
    }


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
                updatePlayerShip();
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
                            cell.setStyle(null);
                        }
                        if (component != null && component.getComponentType() == Battery) {
                            if (((Battery) component).getStored() > 0) {
                                batteries.add(new Pair<>(i, j));
                                addOverlay(i, j, "Battery", ((Battery) component).getStored());
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

                        if (currentCoordsBatteryFuture != null && !currentCoordsBatteryFuture.isDone()) {
                            currentCoordsBatteryFuture.complete(new Pair<>(row, col));
                        }
                        clearShipListeners(ship);
                        updatePlayerShip();
                        cell.setOnMouseClicked(null);
                    });
                    cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                }
            }

        });
    }

    public void showStorage(Ship ship, Cargo cargo) {
        batteries.clear();
        if (this.coordsBattery == null || this.coordsBattery.isDone()) {
            this.coordsBattery = new CompletableFuture<>();
        }
        final CompletableFuture<Pair<Integer,Integer>> currentCoordsBatteryFuture = this.coordsBattery;

        Platform.runLater(() -> {
            for (int i = 0; i < ship.getShip_board().length; i++) {
                for (int j = 0; j < ship.getShip_board()[0].length; j++) {
                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(i * ship.getShip_board()[0].length + j);
                    if (cell != null) {
                        cell.setStyle("");
                        cell.setOnMouseClicked(null);
                        CardComponent component = ship.getShip_board()[i][j];

                        // Celle non accessibili o vuote
                        if (component == null || component.getComponentType() == ComponentType.NotAccessible ||
                                component.getComponentType() == ComponentType.Empty ||
                                component.getComponentType() == ComponentType.MainUnitRed ||
                                component.getComponentType() == ComponentType.MainUnitGreen ||
                                component.getComponentType() == ComponentType.MainUnitBlue ||
                                component.getComponentType() == ComponentType.MainUnitYellow) {
                            cell.setStyle("-fx-background-color: transparent;");
                        }

                        // Controlla i componenti di storage
                        if (cargo == Cargo.Red) {
                            // Solo storage rossi per cargo rosso
                            if (component != null && component.getComponentType() == RedStorage) {
                                Storage storage = (Storage) component; // Cast corretto a Storage
                                if (storage.getCarried_cargos().size() > 0) { // Controlla se ha cargo
                                    batteries.add(new Pair<>(i, j));
                                }
                            }
                        } else {
                            // Storage rossi e blu per altri tipi di cargo
                            if (component != null && (component.getComponentType() == RedStorage ||
                                    component.getComponentType() == BlueStorage)) {
                                Storage storage = (Storage) component; // Cast corretto a Storage
                                if (storage.getCarried_cargos().size() > 0) { // Controlla se ha cargo
                                    batteries.add(new Pair<>(i, j));
                                }
                            }
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

            // Controlla se ci sono storage disponibili
            if (batteries.isEmpty()) {
                // Nessuno storage disponibile per questo tipo di cargo
                String cargoType = cargo == Cargo.Red ? "rosso" : "blu/altro";
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Storage non disponibile");
                alert.setHeaderText("Nessuno storage disponibile");
                alert.setContentText("Non hai storage disponibili per il cargo di tipo " + cargoType );
                alert.showAndWait();

                // Completa il future con coordinate negative per indicare che non c'è selezione
                if (currentCoordsBatteryFuture != null && !currentCoordsBatteryFuture.isDone()) {
                    currentCoordsBatteryFuture.complete(new Pair<>(-1, -1));
                }
            } else {
                // Evidenzia e imposta i listener per gli storage validi
                for (Pair<Integer, Integer> currentCoords : batteries) {
                    int row = currentCoords.getKey();
                    int col = currentCoords.getValue();

                    StackPane cell = (StackPane) playerShipGrid.getChildren().get(row * ship.getShip_board()[0].length + col);

                    if (cell != null) {
                        highlightCell(row, col);
                        cell.setOnMouseClicked(e -> {
                            // Non rimuovere nulla qui - lascia che sia il metodo chiamante a gestire la logica
                            if (currentCoordsBatteryFuture != null && !currentCoordsBatteryFuture.isDone()) {
                                currentCoordsBatteryFuture.complete(new Pair<>(row, col));
                            }
                            clearShipListeners(ship);
                            updatePlayerShip();
                            cell.setOnMouseClicked(null);
                        });
                        cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");
                    }
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
        dclabel.setText("Decidere se attivare la carta alle coordinate (" + x + ", " + y + ")");
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

    public void hideChoice() {

        choiceLabel.setVisible(false);
        accept.setVisible(false);
        reject.setVisible(false);
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
        boardGrid.setPrefSize(BOARD_SIZE * CELL_WIDTH, BOARD_SIZE * CELL_HEIGHT);
        boardGrid.setMinSize(BOARD_SIZE * CELL_WIDTH, BOARD_SIZE * CELL_HEIGHT);
        boardGrid.setMaxSize(BOARD_SIZE * CELL_WIDTH, BOARD_SIZE * CELL_HEIGHT);

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
        cell.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
        cell.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        cell.setMaxSize(CELL_WIDTH, CELL_HEIGHT);

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
                    "-fx-background-color: transparent;");
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
                    cell.setStyle("-fx-background-color: transparent;");

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
            restoreOverlays(localPlayer.getShip());
        });
    }

    public void restoreOverlays(Ship ship) {
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component == null) continue;

                ComponentType type = component.getComponentType();

                if (type == LivingUnit || type == MainUnitRed || type == MainUnitBlue || type == MainUnitGreen || type == MainUnitYellow) {
                    LivingUnit unit = (LivingUnit) component;
                    if (unit.getNum_crewmates() > 0) {
                        switch (unit.getCrewmate_type()) {
                            case Astronaut -> addOverlay(i, j, "Astronaut", unit.getNum_crewmates());
                            case PinkAlien -> addOverlay(i, j, "PinkAlien", unit.getNum_crewmates());
                            case BrownAlien -> addOverlay(i, j, "BrownAlien", unit.getNum_crewmates());
                        }
                    }
                }


                if (type == ComponentType.Battery) {
                    Battery battery = (Battery) component;
                    if (battery.getStored() > 0) {
                        addOverlay(i, j, "Battery", battery.getStored());
                    }
                }

                if (type == ComponentType.RedStorage || type == ComponentType.BlueStorage) {
                    Storage storage = (Storage) component;
                    if (!storage.getCarried_cargos().isEmpty()) {
                        updateCargoOverlayAt(i, j, ship);
                    }
                }
            }
        }
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
            // Rimuovi fortutte le pedine esistenti
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
//    public void movePlayerPawn(String nickname, int oldPosition, int newPosition) {
//        Platform.runLater(() -> {
//            // Rimuovi la pedina dalla posizione precedente
//            if (oldPosition >= 0 && oldPosition < 25) {
//                int oldRow = oldPosition / BOARD_SIZE;
//                int oldCol = oldPosition % BOARD_SIZE;
//                StackPane oldCell = getBoardCellAt(oldRow, oldCol);
//                if (oldCell != null) {
//                    ImageView pawn = playerPawns.get(nickname);
//                    if (pawn != null) {
//                        // Rimuovi il VBox contenitore della pedina
//                        oldCell.getChildren().removeIf(child ->
//                                child instanceof VBox &&
//                                        ((VBox) child).getChildren().contains(pawn));
//                    }
//                }
//            }
//
//            // Aggiungi la pedina alla nuova posizione
//            if (newPosition >= 0 && newPosition < 25) {
//                int newRow = newPosition / BOARD_SIZE;
//                int newCol = newPosition % BOARD_SIZE;
//                StackPane newCell = getBoardCellAt(newRow, newCol);
//                if (newCell != null) {
//                    // Trova il player e i suoi giri per la nuova posizione
//                    Player player = playerPositions.get(newPosition);
//                    Player lapPlayer = playerLaps.get(newPosition);
//                    int lapCount = 0;
//
//                    if (player != null && player.getNickname().equals(nickname)) {
//                        if (lapPlayer != null && lapPlayer.getNickname().equals(nickname)) {
//                            // lapCount = lapPlayer.getLaps(); // Da implementare
//                        }
//                        addPlayerPawnAtPosition(player, newPosition, lapCount);
//                    }
//                }
//            }
//
//            // Aggiorna la Map delle posizioni
//            Player player = playerPositions.get(oldPosition);
//            if (player != null && player.getNickname().equals(nickname)) {
//                playerPositions.remove(oldPosition);
//                playerPositions.put(newPosition, player);
//            }
//        });
//    }

    /**
     * Aggiunge una carta avventura all'area delle carte
     */
    public void addAdventureCard(CardAdventure card) {
        Platform.runLater(() -> {
            try {
                adventureCardArea.getChildren().removeIf(node -> node instanceof ImageView);

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
                if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
                    imageView.setStyle("-fx-effect: dropshadow(gaussian, gold, 5, 0.8, 0, 0); -fx-border-color: gold; -fx-border-width: 3px;");
                } else {
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
                    cell.setStyle("-fx-background-color: gray;");
                }
                return;
            }
        }


    }

    private Node createOverlayForType(String type) {
        return createOverlayForType(type, -1);
    }

    private Node createOverlayForType(String type, int count) {
        String path;

        switch(type){
            case "Astronaut": {
                path = "/images/icons/astronautPawn.png";
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                int num = count > 0 ? count : 1;
                for (int i = 0; i < num; i++) {
                    ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    img.setFitWidth(37.5);
                    img.setFitHeight(37.5);
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
                img.setFitWidth(37.5);
                img.setFitHeight(37.5);
                img.setMouseTransparent(true);
                img.setId("overlay-" + type);
                return img;
            }

            case "BrownAlien": {
                path = "/images/icons/brownAlien.png";
                ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                img.setFitWidth(37.5);
                img.setFitHeight(37.5);
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
                    batteryImg.setFitWidth(27);
                    batteryImg.setFitHeight(27);
                    batteryImg.setPreserveRatio(true);
                    batteryImg.setSmooth(true);
                    batteryImg.setMouseTransparent(true);
                    container.getChildren().add(batteryImg);
                }
                return container;
            }

            case "RedCargo": {
                path = "/images/icons/redCargo.png"; // Sostituisci con il percorso corretto della tua icona
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                for (int i = 0; i < count; i++) {
                    ImageView cargoImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    cargoImg.setFitWidth(25);
                    cargoImg.setFitHeight(25);
                    cargoImg.setPreserveRatio(true);
                    cargoImg.setSmooth(true);
                    cargoImg.setMouseTransparent(true);
                    container.getChildren().add(cargoImg);
                }
                return container;
            }

            case "BlueCargo": {
                path = "/images/icons/blueCargo.png"; // Sostituisci con il percorso corretto della tua icona
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                for (int i = 0; i < count; i++) {
                    ImageView cargoImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    cargoImg.setFitWidth(25);
                    cargoImg.setFitHeight(25);
                    cargoImg.setPreserveRatio(true);
                    cargoImg.setSmooth(true);
                    cargoImg.setMouseTransparent(true);
                    container.getChildren().add(cargoImg);
                }
                return container;
            }

            case "YellowCargo": {
                path = "/images/icons/yellowCargo.png"; // Sostituisci con il percorso corretto della tua icona
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                for (int i = 0; i < count; i++) {
                    ImageView cargoImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    cargoImg.setFitWidth(25);
                    cargoImg.setFitHeight(25);
                    cargoImg.setPreserveRatio(true);
                    cargoImg.setSmooth(true);
                    cargoImg.setMouseTransparent(true);
                    container.getChildren().add(cargoImg);
                }
                return container;
            }

            case "GreenCargo": {
                path = "/images/icons/greenCargo.png"; // Sostituisci con il percorso corretto della tua icona
                HBox container = new HBox(0);
                container.setAlignment(Pos.CENTER);
                container.setMouseTransparent(true);
                container.setId("overlay-" + type);
                for (int i = 0; i < count; i++) {
                    ImageView cargoImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    cargoImg.setFitWidth(25);
                    cargoImg.setFitHeight(25);
                    cargoImg.setPreserveRatio(true);
                    cargoImg.setSmooth(true);
                    cargoImg.setMouseTransparent(true);
                    container.getChildren().add(cargoImg);
                }
                return container;
            }


            default:
                return null;
        }
    }
    public void updateCargoOverlayAt(int row, int col, Ship ship) {
        CardComponent component = ship.getComponent(row, col);

        if (component == null ||
                (component.getComponentType() != ComponentType.RedStorage &&
                        component.getComponentType() != ComponentType.BlueStorage)) {
            return;
        }

        Storage storage = (Storage) component;

        // Rimuovi tutti i cargo esistenti
        removeOverlay(row, col, "RedCargo");
        removeOverlay(row, col, "BlueCargo");
        removeOverlay(row, col, "YellowCargo");
        removeOverlay(row, col, "GreenCargo");

        // Conta i cargo per tipo
        Map<Cargo, Integer> cargoCount = new HashMap<>();
        for (Cargo cargo : storage.getCarried_cargos()) {
            cargoCount.put(cargo, cargoCount.getOrDefault(cargo, 0) + 1);
        }

        // Aggiungi gli overlay per ogni tipo di cargo
        for (Map.Entry<Cargo, Integer> entry : cargoCount.entrySet()) {
            Cargo cargoType = entry.getKey();
            int count = entry.getValue();

            switch (cargoType) {
                case Red -> addOverlay(row, col, "RedCargo", count);
                case Blue -> addOverlay(row, col, "BlueCargo", count);
                case Yellow -> addOverlay(row, col, "YellowCargo", count);
                case Green -> addOverlay(row, col, "GreenCargo", count);
            }
        }
    }

    // Aggiorna il metodo restoreOverlays per includere i cargo
        public void refreshAllCargoOverlays(Ship ship) {
        Platform.runLater(() -> {
            for (int i = 0; i < ship.getROWS(); i++) {
                for (int j = 0; j < ship.getCOLS(); j++) {
                    CardComponent component = ship.getComponent(i, j);
                    if (component != null &&
                            (component.getComponentType() == ComponentType.RedStorage ||
                                    component.getComponentType() == ComponentType.BlueStorage)) {
                        updateCargoOverlayAt(i, j, ship);
                    }
                }
            }
        });
    }

    private boolean isOverlayOfType(Node node, String type) {
        if (node == null) return false;
        String nodeId = node.getId();
        return nodeId != null && nodeId.equals("overlay-" + type);
    }

    public void addOverlay(int x, int y, String type) {
            StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;

            Node overlay = createOverlayForType(type);
            if (overlay != null) {
                cell.getChildren().add(overlay);
            }
    }

    public void addOverlay(int x, int y, String type, int count) {
        Platform.runLater(() -> {StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;

            Node overlay = createOverlayForType(type, count);
            if (overlay != null) {
                cell.getChildren().add(overlay);
            }});

    }


    public void removeOverlay(int x, int y, String type) {
        Platform.runLater(() -> {
            StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;

            Node toRemove = null;
            for (Node child : cell.getChildren()) {
                if (isOverlayOfType(child, type)) {
                    toRemove = child;
                    break;
                }
            }

            if (toRemove != null) {
                cell.getChildren().remove(toRemove);
            }
        });
    }


    public void updateCrewmateOverlayAt(int row, int col, Ship ship) {
            CardComponent component = ship.getComponent(row, col);

            if (component == null || component.getComponentType() != LivingUnit) return;

            LivingUnit unit = (LivingUnit) component;

            removeOverlay(row, col, "Astronaut");
            removeOverlay(row, col, "PinkAlien");
            removeOverlay(row, col, "BrownAlien");

            if (unit.getNum_crewmates() > 0) {
                CrewmateType type = unit.getCrewmate_type();
                switch (type) {
                    case Astronaut -> addOverlay(row, col, "Astronaut", unit.getNum_crewmates());
                    case PinkAlien -> addOverlay(row, col, "PinkAlien", unit.getNum_crewmates());
                    case BrownAlien -> addOverlay(row, col, "BrownAlien", unit.getNum_crewmates());
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
