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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.smartcardio.Card;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;

/**
 * Controller responsible for managing the GUI elements during the flight phase of the game.
 */
public class FlyghtController {
    private GUI gui;
    private CompletableFuture<Integer> adventureCardAction = new CompletableFuture<>();
    private List<Player> players = new ArrayList<>();
    List<Pair<Integer,Integer>> batteries = new ArrayList<>();
    List<Pair<Integer,Integer>> livingUnits = new ArrayList<>();
    private Map<String, ImageView> playerPawns = new HashMap<>();
    private Map<Integer, Player> playerPositions = new HashMap<>();
    private Map<Integer, Player> playerLaps = new HashMap<>();
    private CompletableFuture<Boolean> useDoubleCannon;
    private CompletableFuture<Boolean> acceptAdventure;
    private CompletableFuture<Pair<Integer, Integer>> coordsBattery;
    private CompletableFuture<Boolean> useCard;
    private CompletableFuture<Pair<Integer, Integer>> astronautToRemove;
    private Stage playerStage;


    @FXML
    private GridPane boardGrid;

    @FXML
    private GridPane playerShipGrid;

    @FXML
    private VBox adventureCardArea;

    @FXML
    private HBox playersInfoBox;

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

    @FXML
    private Label extraComponentLabel;
    @FXML
    private VBox eventLogVBox;
    @FXML
    private ScrollPane eventLogScrollPane;


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

    /**
     * Adds a log message to the event log with a specific message type that determines its style.
     * @param message the message to display in the log
     * @param messageType the type of the message, which determines the color and font style
     */
    public void addLogMessage(String message, String messageType) {
        Platform.runLater(() -> {

            Label logEntry = new Label(message);
            logEntry.setWrapText(true);
            logEntry.setMaxWidth(eventLogVBox.getWidth());

            logEntry.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 12px;");

            switch (messageType) {
                case "HIGHLIGHT":
                    logEntry.setTextFill(javafx.scene.paint.Color.web("#DAA520"));
                    logEntry.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                    break;
                case "ERROR":
                    logEntry.setTextFill(javafx.scene.paint.Color.web("#FF0000"));
                    logEntry.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                    break;
                case "NOTIFICATION":
                    logEntry.setTextFill(javafx.scene.paint.Color.web("#90EE90"));
                    logEntry.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                    break;
                case "NORMAL":
                default:
                    logEntry.setTextFill(javafx.scene.paint.Color.web("#D8B7DD"));
                    logEntry.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                    break;
            }

            if (eventLogVBox != null) {
                eventLogVBox.getChildren().add(logEntry);
            }
        });
    }


    /**
     * Sets the current player's ship viewing stage (used when opening another player's ship view).
     * @param playerStage the stage to associate with the player's ship view
     */
    public void setPlayerStage(Stage playerStage) {
        this.playerStage = playerStage;
    }
    public Stage getPlayerStage() {
        return playerStage;
    }

    /**
     * Creates and adds buttons for each other player, allowing the user to view their ship.
     * @param otherPlayers the list of other players to display
     */
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

    /**
     * Displays the ship of a given player in a new window.
     * @param nickname the nickname of the player whose ship will be shown
     */
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

    /**
     * Updates the credit label shown on the UI with the current player's credit count.
     * @param credits the number of credits to display
     */
    public void updateCreditLabel(int credits) {
        Platform.runLater(() -> playerCreditsLabel.setText("Crediti: " + credits));
    }

    /**
     * @return a CompletableFuture with the coordinates of the astronaut to remove
     */
    public CompletableFuture<Pair<Integer,Integer>> getAstronautToRemove() {
        if (astronautToRemove == null) {
            astronautToRemove = new CompletableFuture<>();
        }
        return astronautToRemove;
    }


    public void updateExtraComponentsLabel(int num) {
        Platform.runLater(() ->extraComponentLabel .setText("Componenti Persi : " + num));
    }

    /**
     * Resets the CompletableFuture used for selecting a crewmate to remove,
     * allowing for a new selection to be made.
     */
    public void resetastronautToRemove(){
        astronautToRemove = new CompletableFuture<>();
    }

    /**
     * Highlights the crew members (Living Units) present on the given ship
     * and sets up mouse listeners for selecting one to remove.
     * @param ship
     */
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
                            }
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

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

    /**
     * Clears the UI overlays and click listeners used for selecting crewmates from the ship.
     * @param ship
     */
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
     * Initializes the flight screen
     */
    public void initialize() {
        setupBoardGrid();
        setupPlayerShipGrid();
        setupAdventureCardArea();
        addLogMessage("Benvenuto In Galaxy Trucker! Questa à la Fase di Volo","NOTIFICATION");
        eventLogVBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            eventLogScrollPane.setVvalue(1.0);
        });
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

    /**
     * Displays all batteries available on the ship that still have charge.
     * @param ship the ship whose batteries will be displayed
     */
    public void showBatteries(Ship ship) {
        batteries.clear();
        if (this.coordsBattery == null || this.coordsBattery.isDone()) {
            this.coordsBattery = new CompletableFuture<>();
        }
        final CompletableFuture<Pair<Integer,Integer>> currentCoordsBatteryFuture = this.coordsBattery;

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

    /**
     * Highlights storages containing the specified type of cargo.
     * Allows the player to choose one for cargo removal or interaction.
     * @param ship
     * @param cargo the cargo type to search for
     */
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

                        if (component == null || component.getComponentType() == ComponentType.NotAccessible ||
                                component.getComponentType() == ComponentType.Empty ||
                                component.getComponentType() == ComponentType.MainUnitRed ||
                                component.getComponentType() == ComponentType.MainUnitGreen ||
                                component.getComponentType() == ComponentType.MainUnitBlue ||
                                component.getComponentType() == ComponentType.MainUnitYellow) {
                            cell.setStyle("-fx-background-color: transparent;");
                        }

                        if (cargo == Cargo.Red) {
                            if (component != null && component.getComponentType() == RedStorage) {
                                Storage storage = (Storage) component;
                                if (storage.getCarried_cargos().size() > 0) {
                                    batteries.add(new Pair<>(i, j));
                                }
                            }
                        } else {
                            if (component != null && (component.getComponentType() == RedStorage ||
                                    component.getComponentType() == BlueStorage)) {
                                Storage storage = (Storage) component;
                                if (storage.getCarried_cargos().size() > 0) {
                                    batteries.add(new Pair<>(i, j));
                                }
                            }
                        }
                        cell.setStyle(cell.getStyle() + " -fx-cursor: default;");
                    }
                }
            }

            if (batteries.isEmpty()) {
                String cargoType = cargo == Cargo.Red ? "rosso" : "blu/altro";
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Storage non disponibile");
                alert.setHeaderText("Nessuno storage disponibile");
                alert.setContentText("Non hai storage disponibili per il cargo di tipo " + cargoType );
                alert.showAndWait();

                if (currentCoordsBatteryFuture != null && !currentCoordsBatteryFuture.isDone()) {
                    currentCoordsBatteryFuture.complete(new Pair<>(-1, -1));
                }
            } else {
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
            }
        });
    }


    /**
     * Clears all mouse listeners and highlighting effects from the ship grid.
     * @param ship
     */
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

    /**
     * @param gridPane the GridPane to search
     * @param col the column index
     * @param row the row index
     * @return the Node at the specified position, or null if not found
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    /**
     * Returns a CompletableFuture that completes with true or false based on the player's choice
     * to activate the double cannon ability.
     * @return CompletableFuture<Boolean> indicating whether the double cannon is used
     */
    public CompletableFuture<Boolean> getUseDoubleCannon() {
        if (useDoubleCannon == null) {
            useDoubleCannon = new CompletableFuture<>();
        }
        return useDoubleCannon;
    }

    public void resetUseDC(){
        useDoubleCannon = new CompletableFuture<>();
    }

    /**
     * Displays the UI prompt asking the player whether they want to use the double cannon
     * at the specified coordinates.
     * @param x the row coordinate
     * @param y the column coordinate
     */
    public void showdc(int x, int y) {
        Platform.runLater(() -> {
            dclabel.setText("Decidere se attivare la carta alle coordinate (" + x + ", " + y + ")");
            dclabel.setVisible(true);
            yesdc.setVisible(true);
            yesdc.setOnAction((ActionEvent event ) -> {
                useDoubleCannon.complete(true);
                hidedc();
            });
            nodc.setVisible(true);
            nodc.setOnAction((ActionEvent event) -> {
                useDoubleCannon.complete(false);
                hidedc();
                resetHighlights(y, x);
            });
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

    /**
     * Displays a yes/no prompt for accepting an adventure card.
     * @param prompt the message shown to the player
     * @return true if the player accepts the adventure, false otherwise
     */
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
     * Sets the reference to the GUI instance.
     * @param gui the main GUI object
     */
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * Initializes the central board grid (5x5) with styled cells.
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
     * Creates a single board cell with consistent styling.
     * @param row the row index
     * @param col the column index
     * @return the created StackPane representing the cell
     */
    private StackPane createBoardCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
        cell.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        cell.setMaxSize(CELL_WIDTH, CELL_HEIGHT);

        cell.setStyle(
                "-fx-border-color: #3a0066; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-color: #bfa3ff; " +
                        "-fx-background-radius: 10px;"
        );

        return cell;
    }


    /**
     * Initializes the grid for the player's ship (7x5).
     */
    private void setupPlayerShipGrid() {
        playerShipGrid.getChildren().clear();
        playerShipGrid.setPrefSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);
        playerShipGrid.setMinSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);
        playerShipGrid.setMaxSize(7 * SHIP_CELL_SIZE, 5 * SHIP_CELL_SIZE);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane cell = createShipCell(i, j);
                playerShipGrid.add(cell, j, i);
            }
        }
    }

    /**
     * Creates a single ship cell.
     * @param row the row index
     * @param col the column index
     * @return the created StackPane representing the ship cell
     */
    private StackPane createShipCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);
        cell.setMinSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);
        cell.setMaxSize(SHIP_CELL_SIZE, SHIP_CELL_SIZE);

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
     * Initializes the adventure card area UI with a title label.
     */
    private void setupAdventureCardArea() {
        adventureCardArea.getChildren().clear();
        adventureCardArea.setSpacing(10);

        Label titleLabel = new Label("Carte Avventura");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-family: 'Verdana'; -fx-font-weight: bold; -fx-text-fill: #D8B7DD;");
        adventureCardArea.getChildren().add(titleLabel);
    }

    /**
     * Updates the graphical representation of the player's ship on the grid.
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

    /**
     * Restores graphical overlays for crew members, batteries, and cargo storage
     * @param ship
     */
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
     * Returns the ship cell at the specified grid coordinates.
     * @param row the row index
     * @param col the column index
     * @return the StackPane representing the cell, or null if not found
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
     * Updates the positions of all player pawns on the central board.
     * @param positions a map of board positions to players
     * @param laps a map of positions to players with lap count info
     */
    public void updatePlayerPositions(Map<Integer, Player> positions, Map<Integer, Player> laps) {
        this.playerPositions = positions;
        this.playerLaps = laps;

        Platform.runLater(() -> {
            clearPlayerPawns();

            for (Map.Entry<Integer, Player> entry : positions.entrySet()) {
                int position = entry.getKey();
                Player player = entry.getValue();

                if (player != null) {

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
     * Removes all player pawns from the central board grid.
     */
    private void clearPlayerPawns() {
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.getChildren().removeIf(child ->
                        child instanceof VBox &&
                                playerPawns.containsValue(((VBox) child).getChildren().get(0)));
            }
        }
        playerPawns.clear();
    }

    /**
     * Adds a player pawn to the central board grid at the specified position.
     * @param player the player to place
     * @param position the position on the board (0 to BOARD_SIZE * BOARD_SIZE - 1)
     * @param lapCount the number of laps completed by the player
     */
    private void addPlayerPawnAtPosition(Player player, int position, int lapCount) {
        int playerRow = position / BOARD_SIZE;
        int playerCol = position % BOARD_SIZE;

        StackPane cell = getBoardCellAt(playerRow, playerCol);
        if (cell == null) return;

        VBox playerContainer = new VBox();
        playerContainer.setSpacing(0);
        playerContainer.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView pawn = createPlayerPawn(player);
        playerPawns.put(player.getNickname(), pawn);

        Label playerInfo = new Label(player.getNickname() + " (" + lapCount + ")");
        playerInfo.setStyle("-fx-font-size: 8px; -fx-text-fill: black; " +
                "-fx-background-color: white; -fx-background-radius: 3px; " +
                "-fx-padding: 1px 3px;");

        playerContainer.getChildren().addAll(pawn, playerInfo);
        cell.getChildren().add(playerContainer);
    }

    /**
     * Creates the visual pawn for a player based on their color.
     * @param player the player whose pawn is to be created
     * @return an ImageView representing the pawn
     */
    private ImageView createPlayerPawn(Player player) {
        ImageView pawn = new ImageView();
        pawn.setFitWidth(40);
        pawn.setFitHeight(40);
        pawn.setPreserveRatio(true);

        String pawnImagePath = getPawnImagePath(player.getColor());

        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(pawnImagePath)));
            pawn.setImage(image);
        } catch (Exception e) {
            pawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px; -fx-border-color: black; " +
                    "-fx-border-width: 2px; -fx-border-radius: 12px;");

            StackPane coloredPawn = new StackPane();
            coloredPawn.setPrefSize(25, 25);
            coloredPawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px; -fx-border-color: black; " +
                    "-fx-border-width: 2px; -fx-border-radius: 12px;");

            pawn.setStyle("-fx-background-color: " + getColorHex(player.getColor()) +
                    "; -fx-background-radius: 12px;");
        }

        return pawn;
    }

    private String getPawnImagePath(Color color) {
        switch (color) {
            case BLUE:
                return "/images/icons/bluPawn.png";
            case RED:
                return "/images/icons/redPawn.png";
            case GREEN:
                return "/images/icons/greenPawn.png";
            case YELLOW:
                return "/images/icons/yellowPawn.png";
            default:
                return "/images/icons/astronautPawns.png";
        }
    }

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
     * Returns the board cell at the specified coordinates on the central board grid.
     * @param row the row index
     * @param col the column index
     * @return the StackPane representing the board cell, or null if not found
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
     * Adds an adventure card to the adventure card area.
     * @param card the adventure card to display
     */
    public void addAdventureCard(CardAdventure card) {
        Platform.runLater(() -> {
            try {
                adventureCardArea.getChildren().removeIf(node -> node instanceof ImageView);

                Image image = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream(card.getImagePath())));
                ImageView cardView = new ImageView(image);
                cardView.setFitWidth(120);
                cardView.setFitHeight(180);
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
     * Clears all adventure cards from the display area.
     */
    public void clearAdventureCards() {
        Platform.runLater(() -> {
            adventureCardArea.getChildren().removeIf(node -> node instanceof ImageView);
        });
    }


    public CompletableFuture<Integer> getAdventureCardAction() {
        if (adventureCardAction == null || adventureCardAction.isDone()) {
            adventureCardAction = new CompletableFuture<>();
        }
        return adventureCardAction;
    }


    public void resetAdventureCardAction() {
        adventureCardAction = new CompletableFuture<>();
    }


    public Map<Integer, Player> getPlayerPositions() {
        return new HashMap<>(playerPositions);
    }

    public int getPlayerPosition(String nickname) {
        return playerPositions.entrySet().stream()
                .filter(entry -> entry.getValue().getNickname().equals(nickname))
                .mapToInt(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    /**
     * Updates the label to show whose turn it is.
     * @param playerName the name of the current player
     */
    public void updateCurrentPlayer(String playerName) {
        Platform.runLater(() -> {
            if (currentPlayerLabel != null) {
                currentPlayerLabel.setText("Turno di: " + playerName);
            }
        });
    }

    /**
     * Highlights the specified cell on the ship grid.
     * @param y the row index
     * @param x the column index
     */
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

    /**
     * Resets the visual highlight of the specified cell.
     * @param y the row index
     * @param x the column index
     */
    public void resetHighlights(int y,int x) {

        for (Node node : playerShipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == x && rowIndex == y && node instanceof StackPane cell) {
                if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
                    imageView.setStyle("");
                } else {
                    cell.setStyle("-fx-background-color: gray;");
                }
                return;
            }
        }


    }

    /**
     * Creates a graphical overlay node for the specified type.
     * @param type the type of overlay to create
     * @return the overlay Node, or null if type is unknown
     */
    private Node createOverlayForType(String type) {
        return createOverlayForType(type, -1);
    }

    /**
     * Creates a graphical overlay node for the specified type and count.
     * @param type the type of overlay to create
     * @param count the number of elements to display
     * @return the overlay Node, or null if type is unknown
     */
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
                path = "/images/icons/redCargo.png";
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
                path = "/images/icons/blueCargo.png";
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
                path = "/images/icons/yellowCargo.png";
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
                path = "/images/icons/greenCargo.png";
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

    /**
     * Updates the cargo overlay on a specific cell based on the cargo stored.
     * @param row the row index
     * @param col the column index
     * @param ship the ship containing the component
     */
    public void updateCargoOverlayAt(int row, int col, Ship ship) {
        CardComponent component = ship.getComponent(row, col);

        if (component == null ||
                (component.getComponentType() != ComponentType.RedStorage &&
                        component.getComponentType() != ComponentType.BlueStorage)) {
            return;
        }

        Storage storage = (Storage) component;

        removeOverlay(row, col, "RedCargo");
        removeOverlay(row, col, "BlueCargo");
        removeOverlay(row, col, "YellowCargo");
        removeOverlay(row, col, "GreenCargo");

        Map<Cargo, Integer> cargoCount = new HashMap<>();
        for (Cargo cargo : storage.getCarried_cargos()) {
            cargoCount.put(cargo, cargoCount.getOrDefault(cargo, 0) + 1);
        }

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

    /**
     * Checks if the given Node is an overlay of the specified type.
     * @param node the node to check
     * @param type the type identifier to match
     * @return true if the node is an overlay of the given type
     */
    private boolean isOverlayOfType(Node node, String type) {
        if (node == null) return false;
        String nodeId = node.getId();
        return nodeId != null && nodeId.equals("overlay-" + type);
    }


    /**
     * Adds an overlay to a specific ship cell.
     * @param x the row index
     * @param y the column index
     * @param type the type of overlay
     * @param count number of elements to show
     */
    public void addOverlay(int x, int y, String type, int count) {
        Platform.runLater(() -> {StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;

            Node overlay = createOverlayForType(type, count);
            if (overlay != null) {
                cell.getChildren().add(overlay);
            }});

    }

    /**
     * Removes an overlay of a given type from a specific cell.
     * @param x the row index
     * @param y the column index
     * @param type the type of overlay to remove
     */
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

    /**
     * Updates the crewmate overlay for a specific cell, showing the correct type and count.
     * @param row the row index
     * @param col the column index
     * @param ship the ship containing the LivingUnit
     */
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
}
