package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.Epidemic;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;

/**
 * Main graphical user interface (GUI) class for the Galaxy Trucker game.
 */
public class GUI implements View {

    Joingamecontroller joingamecontroller;
    Numplayercontroller numplayercontroller;
    Guiselectcontroller guiselectcontroller;
    Choosecolorcontroller choosecolorcontroller;
    Buildcontroller buildcontroller;
    Randomcardcontroller randomcardcontroller;
    FinalRanksScreen finalRanksScreen;
    CrewmateSelectionController crewmateSelectionController;
    Stage stage;
    private String nicknamescelto;
    private Player player_local;
    @FXML
    public TextField nicknameField;
    private GuiApplication application;
    private ClientCallBack clientCallback;
    private List<Integer> lobbies;
    private List<Color> colorsavailable;
    private boolean chooseColorScreenOpen = false;
    private Client client;
    private CardComponent actualcard;
    private Boolean isbuildscreenactive=false;

    public Buildcontroller getBuildcontroller() {
        return buildcontroller;
    }
    public Stage getStage(){
        return stage;
    }
    public void setClient(Client client){
        this.client = client;
    }
    public Client getClient(){
        return client;
    }

    private FlyghtController flyghtController;
    private boolean isFlyghtScreenActive = false;

    public void setClientCallback(ClientCallBack callback) {
        this.clientCallback = callback;
    }
    public List<Integer> getlobbies() {
        return this.lobbies;
    }
    public void setColorsavailable(List<Color> colorsavailable) {
        this.colorsavailable = colorsavailable;
    }
    public List<Color> getColorsavailable() {
        return this.colorsavailable;
    }

    public GUI() {
    }
    public Randomcardcontroller getRandomcardcontroller() {
        return randomcardcontroller;
    }

    public void setGuiApplication(GuiApplication application) {
        this.application = application;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * This method is used to create the nickname screen
     */
    public void createNicknamescreen(){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Nickname.fxml"));
                loader.setController(this);
                Parent root = loader.load();
                Stage stage = application.getStage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method send the user's nickname to the client application.
     * @param event the insertion of the name by the user
     */

    @FXML
    public void sendNickname(ActionEvent event) {
        System.out.println("sendNickname() chiamato");
        this.nicknamescelto = nicknameField.getText();
        if (clientCallback != null) {
            clientCallback.sendNicknameToServer(nicknamescelto);
        }
        System.out.println("nicknamescelto: " + nicknamescelto);
    }

    /**
     * This method send the user's nickname to the client application.
     */
    @Override
    public String askNickname()  {
        return nicknamescelto;
    }

    /**
     * This method is used to create the screen in witch the player decide to enter a lobby or to join an existing one
     */

    public void createjoingamecontroller(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Joingamecontroller controller = new Joingamecontroller();
                controller.setGui(this);
                this.joingamecontroller = controller;
                controller.start(this.stage);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *This method send the user's nickname to the client application.
     * @return 1 for creating a lobby, 0 to enter an existing one
     */

    @Override
    public int askCreateOrJoin() {
        try {
            return joingamecontroller.getChoiceFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Add a message to Log Event
     * @param msg the message to display
     * @param type
     */
    public void addLogEvent(String msg, String type) {

        flyghtController.addLogMessage(msg,type);

    }


    /**
     * This method is used to create the screen in witch the player decide how many players can join the lobby
     */

    public void createnumplayerscontroller(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Numplayercontroller controller = new Numplayercontroller();
                controller.setGui(this);
                this.numplayercontroller = controller;
                controller.start(this.stage);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *This method send the number of player in the new lobby to the client application.
     * @return the number of player
     */
    @Override
    public int askNumPlayers() {
        try {
            return numplayercontroller.getPlayerNumber().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }


    @Override
    public void updateLocalPlayer(Player localPlayer) {
        this.player_local = localPlayer;
    }

    public Player getPlayer_local() {
        return player_local;
    }

    @Override
    public void updateOtherPlayers(List<Player> otherPlayers) {

    }

    @Override
    public void updateAdventureDeck(Map<Direction, List<CardAdventure>> adventureDeck) {

    }

    @Override
    public void updateFacedUpCards(List<CardComponent> facedUpDeck) {

    }

    /**
     * creates a pop-up to show a quick message
     * @param message the message to be shown
     */

    @Override
    public void showMessage(String message) {
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Messaggio");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } else {
            CompletableFuture<Void> future = new CompletableFuture<>();

            Platform.runLater(() -> {
                try {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Messaggio Bloccante");
                    alert.setHeaderText(null);
                    alert.setContentText(message);

                    alert.showAndWait();

                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                System.err.println("La visualizzazione del messaggio è stata interrotta o ha fallito.");
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to create the screen in witch the player decide witch lobby to join
     */

    public void createselectlobbyscreen(List<Integer> lobbies){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Guiselectcontroller controller = new Guiselectcontroller();
                controller.setGui(this);
                this.guiselectcontroller = controller;
                this.lobbies = lobbies;
                controller.start(this.stage);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *This method send the number of the lobby chosen to the client application
     * @return lobby ID
     */
    @Override
    public int showLobbies(List<Integer> lobbies) {
        try {
            return guiselectcontroller.getSelectedLobbyFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method is used to create the screen in witch the player decide witch color he wants
     */
    public void createchoosecolorscreen(List<Color> colors) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Choosecolorcontroller controller = new Choosecolorcontroller();
                controller.setGUI(this);
                this.choosecolorcontroller = controller;
                this.colorsavailable = colors;
                controller.start(this.stage);
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *This method send the color chosen to the client application
     * @return RED,YELLOW,BLUE or GREEN
     */
    @Override
    public Color askColor(List<Color> colors) {
        try {
            return choosecolorcontroller.getColorChosen().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is called when a player chose a color to update the colors remaining for the other players
     * @param newColors a list containing the remaining colors
     */
    public void updateColors(List<Color> newColors) {
        Platform.runLater(() -> {
            this.colorsavailable = newColors;
            if (choosecolorcontroller != null) {
                choosecolorcontroller.setActiveButton(newColors);
                choosecolorcontroller.stateLabel.setText("Colori ancora disponibili:");
            }
        });
    }

    /**
     * This method is used to create the screen in witch the player build his ship
     */
    public void createbuildscreen() {
        if (this.buildcontroller != null && isbuildscreenactive) {
            Platform.runLater(() -> {
                    stage.toFront();
                    stage.requestFocus();
                    stage.show();
                    buildcontroller.updateFaceUpCardsDisplay();
            });
            return;
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/BuildShip.fxml"));
                Parent root = loader.load();

                Buildcontroller controller = loader.getController();
                controller.setGUI(this);
                this.buildcontroller = controller;

                Scene scene = new Scene(root);
                stage.setTitle("Build Ship");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
                isbuildscreenactive = true;

                stage.setOnCloseRequest((event) -> {
                    Platform.exit();
                    System.exit(0);
                });

                controller.setupPlayerButtons(client.getOther_players_local());
                controller.initializeShipBoard();
                controller.updateFaceUpCardsDisplay();
                controller.starttimer(270);

                future.complete(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to create the screen in witch the player can play
     */

    public void createFlyghtScreen(Map<Integer, Player> positions, Map<Integer, Player> laps) {
        if (this.flyghtController != null && isFlyghtScreenActive) {
            Platform.runLater(() -> {
                stage.toFront();
                stage.requestFocus();
                stage.show();
                flyghtController.updatePlayerShip();
            });
            return;
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlyghtBoard.fxml"));
                Parent root = loader.load();

                FlyghtController controller = loader.getController();
                controller.setGUI(this);
                this.flyghtController = controller;

                Scene scene = new Scene(root);
                stage.setTitle("Flyght Game Board");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
                isFlyghtScreenActive = true;

                stage.setOnCloseRequest((event) -> {
                    Platform.exit();
                    System.exit(0);
                });

                controller.updatePlayerShip();
                controller.setupPlayerButtons(client.getOther_players_local());

                if (client != null && client.getOther_players_local() != null) {

                     controller.updatePlayerPositions(positions, laps);
                }

                if (client != null && client.getPlayer_local() != null) {
                    controller.updateCurrentPlayer(client.getPlayer_local().getNickname());
                }

                future.complete(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public FlyghtController getFlyghtController() {
        return flyghtController;
    }

    /**
     * This method returns the action that the player wants to do in the build screen
     * @return 1 ask for a random card, 2 for a face up card, 3 for a reserved card
     */

    @Override
    public int selectDeck() {
        try {
            int selection=buildcontroller.getAction().get();
            buildcontroller.resetAction();
            return selection;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public CardComponent getActualcard() {
        return actualcard;
    }

    /**
     * This method is used to create the screen in witch the player decide what to do with the drawn card
     */
    public void createrandomcardcontroller(CardComponent card) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.actualcard = card;

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
                Parent root = loader.load();
                Randomcardcontroller controller = loader.getController();

                controller.setGui(this);
                Stage stage = new Stage();
                controller.setStage(stage);
                if (player_local.getShip().getExtra_components().contains(card) || player_local.getShip().getExtra_components().size()>1) {
                    controller.setBookButton();
                }

                controller.showCardImage(card);

                stage.setTitle("Random Card");
                stage.setScene(new Scene(root));


                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();


                double screenWidth = primaryScreenBounds.getWidth();
                double screenHeight = primaryScreenBounds.getHeight();
                double windowWidth = stage.getScene().getWidth();
                double windowHeight = stage.getScene().getHeight();

                double windowX = (screenWidth - windowWidth) / 2;
                double windowY = (screenHeight - windowHeight) / 2;


                stage.setX(windowX);
                stage.setY(windowY);


                stage.show();


                this.randomcardcontroller = controller;

                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method is used to create the screen in witch the player decide witch type of crewmate he wants
     */
    public void createCrewmateSelectionController(Pair<Integer, Integer> coords, List<CrewmateType> supportedCrewmates) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrewmateSelection.fxml"));
                Parent root = loader.load();
                CrewmateSelectionController controller = loader.getController();

                controller.setCardWithSupportedCrewmates(coords, supportedCrewmates);
                Stage stage = new Stage();
                controller.setStage(stage);

                stage.setTitle("Crewmate Selection");
                stage.setScene(new Scene(root));

                // Calcola posizione: sinistra + centro verticale
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                double x = 20; // Margine da sinistra
                double y = (screenBounds.getHeight() - root.prefHeight(-1)) / 2 - 50;
                stage.setX(x);
                stage.setY(y);

                stage.show();

                this.crewmateSelectionController = controller;
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });


        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }



    /**
     * This method sends to the client the action that the player wants to do with a card
     * @param card
     * @return 1 rotate, 2 place card on ship, 3 discard, 4 reserve
     */
    @Override
    public int showCard(CardComponent card) {
        try {
            return randomcardcontroller.getAction().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method asks the player to choose a planet from a list of options, excluding already taken ones.
     * @param planets a list of available planets, each represented by a list of cargos
     * @param planets_taken a set of indices representing planets already selected by other players
     * @return the index of the selected planet
     */
    @Override
    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planets_taken) {
        CargoSelector selector = new CargoSelector();
        return selector.askPlanet(planets, planets_taken);
    }
    /**
     * This method asks the player to provide coordinates for placing a card on the ship.
     * @param ship
     * @return a pair of integers representing the row and column of the selected coordinates;
     *         returns (-1, -1) if the selected cell is not empty
     */
    @Override
    public Pair<Integer, Integer> askCoords(Ship ship) {
        try {
            Pair<Integer,Integer> coords=buildcontroller.getCoords().get();

            if(client.getPlayer_local().getShip().getComponent(coords.getKey(),coords.getValue()).getComponentType() != ComponentType.Empty){

                return new Pair<>(-1,-1);

            }
            buildcontroller.resetCoords();
            Platform.runLater(() -> {
                CardComponent selectedCard = getActualcard();
                buildcontroller.placeCardOnShip(selectedCard, coords);
            });
            return coords;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * This method asks the player to select a reserved card from the available ones.
     * @param securedCards a list of cards that have been previously reserved
     * @return the index of the selected reserved card; returns -1 in case of error or cancellation
     */
    @Override
    public int askSecuredCard(List<CardComponent> securedCards) {
        try {
            return buildcontroller.getReservedCardIndexFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        } finally {
            buildcontroller.resetReservedCardIndex();
        }
    }

    /**
     * This method is used to create the screen of the final ranking
     */
    public void createrankingscreen(List<Player> finalRanks) {
        if (this.finalRanksScreen != null) {
            Platform.runLater(() -> {
                stage.toFront();
                stage.requestFocus();
                stage.show();
            });
            return;
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ranking.fxml"));
                Parent root = loader.load();

                FinalRanksScreen controller = loader.getController();
                controller.setGUI(this);
                this.finalRanksScreen = controller;

                Scene scene = new Scene(root);
                stage.setTitle("Final Ranking");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();

                stage.setOnCloseRequest((event) -> {
                    Platform.exit();
                    System.exit(0);
                });
                controller.displayFinalRanks(finalRanks);
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        });

    }

    @Override
    public void printFinalRanks(List<Player> finalRanks) {
    }

    /**
     * This method asks the player to select an action for a crewmate associated with the specified component.
     *
     * @param component the coordinates (row, column) of the ship component
     * @return the index corresponding to the chosen crewmate action;
     *         returns -1 in case of an error
     */
    @Override
    public int crewmateAction(Pair<Integer,Integer> component) {
        try {
            return crewmateSelectionController.getCrewmate().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method retrieves the list of crewmate types supported by the component at the given coordinates.
     * @param coords the coordinates (row, column) of the ship component
     * @return a list of supported {@link CrewmateType}
     */
    public List<CrewmateType> getCrewmates(Pair<Integer,Integer> coords) {
        CardComponent[][] ship = client.getPlayer_local().getShip().getShipBoard();
        CardComponent component = ship[coords.getKey()][coords.getValue()];
        List<CrewmateType> crewmateType = client.getPlayer_local().getShip().checkAlienSupport(component);
        crewmateType.add(CrewmateType.Astronaut);
        return crewmateType;
    }

    /**
     * this method asks the player to decide whether to use a double cannon for the current attack.
     * @return true if the player chooses to use the double cannon, false otherwise;
     *         returns null in case of an error
     */
    public Boolean useDoubleCannon() {
        try {
            Boolean selection=flyghtController.getUseDoubleCannon().get();
            flyghtController.resetUseDC();
            return selection;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method asks the player to select the coordinates of a battery to activate.
     * @return a pair of integers representing the selected battery's position (row, column);
     *         returns null in case of an error
     */
    public Pair<Integer,Integer> coordsBattery() {
        try {
            Pair<Integer,Integer> selection=flyghtController.getcoordsBattery().get();
            flyghtController.resetcoordsBattery();
            return selection;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method asks the player to select the coordinates of a crewmate (astronaut) to remove.
     * @return a pair of integers representing the selected crewmate's position (row, column);
     *         returns null in case of an error
     */
    public Pair<Integer,Integer> coordsCrewmate() {
        try {
            Pair<Integer,Integer> selection=flyghtController.getAstronautToRemove().get();
            flyghtController.resetastronautToRemove();
            return selection;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int nextMeteor() {
        return 0;
    }

    /**
     * This method asks the player whether to activate an engine using a battery.
     * Highlights the engine cell and displays a prompt to use a double cannon.
     * If the player confirms, proceeds to battery selection.
     * @param engine the coordinates (row, column) of the engine to activate
     * @return the coordinates of the selected battery
     */
    @Override
    public Pair<Integer, Integer> askEngine(Pair<Integer, Integer> engine) {
        int i = engine.getKey();
        int j = engine.getValue();


        flyghtController.highlightCell(i, j);
        flyghtController.showdc(i, j);
        Boolean useDC = useDoubleCannon();
        if (!useDC) {
            return new Pair<>(-1,-1);
        } else {
            return useBattery(player_local.getShip());
        }

    }

    /**
     * This method asks the player to select a battery card from the ship to activate it.
     * @param ship
     * @return the coordinates (row, column) of the selected battery;
     */
    @Override
    public Pair<Integer, Integer> useBattery(Ship ship) {
        Pair<Integer, Integer> battery = new Pair<>(-1, -1);
        Map<Pair<Integer, Integer>, Boolean> battery_usage_os = new HashMap<Pair<Integer, Integer>, Boolean>();
        Battery card_battery;
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(i, j);
                if (card.getComponentType() == ComponentType.Battery) {
                    showMessage("Scegliere la batteria");
                    getFlyghtController().showBatteries(ship);

                    try {
                        battery = coordsBattery();
                        System.out.println("Batteria scelta: " + battery);
                        return battery;

                    } catch (Exception e) {
                        System.err.println("Errore durante la selezione della batteria: " + e.getMessage());
                        battery_usage_os.put(new Pair<>(i, j), false);

                    }
                }
            }
        }
        return battery;

    }




    @Override
    public void printMeteors(List<Pair<MeteorType, Direction>> meteors) {

    }

    /**
     * This method creates a pop-up alerting the player of an incoming meteor or cannon shot.
     * The window shows the meteor's type, direction, and target coordinate, along with a related image.
     * @param meteor a pair containing the {@link MeteorType} and its {@link Direction}
     * @param coord the target coordinate (column or row) of the impact, depending on the direction
     */
    @Override
    public void printMeteor(Pair<MeteorType, Direction> meteor, int coord) {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage meteorStage = new Stage();

                meteorStage.initModality(Modality.APPLICATION_MODAL);
                meteorStage.setTitle("Allarme Meteora!");
                meteorStage.setResizable(false);

                VBox mainContainer = new VBox(15);
                mainContainer.setAlignment(Pos.CENTER);
                mainContainer.setPadding(new Insets(20));
                mainContainer.setStyle("-fx-background-color: #D8B7DD;");

                String imagePath = getMeteorImagePath(meteor.getKey());

                ImageView meteorImage = new ImageView();
                try {
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    meteorImage.setImage(image);
                    meteorImage.setFitWidth(102);
                    meteorImage.setFitHeight(162);
                    meteorImage.setPreserveRatio(true);
                    rotateMeteorImage(meteorImage, meteor.getValue());
                } catch (Exception e) {
                    meteorImage.setFitWidth(102);
                    meteorImage.setFitHeight(162);
                    System.err.println("Impossibile caricare l'immagine: " + imagePath);
                    e.printStackTrace();
                }

                Label meteorTypeLabel = new Label(getMeteorTypeText(meteor.getKey()));
                meteorTypeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
                meteorTypeLabel.setStyle("-fx-text-fill: #043a7e;");

                Label directionLabel = new Label("in arrivo " + getDirectionText(meteor.getValue()));
                directionLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
                directionLabel.setStyle("-fx-text-fill: #043a7e;");

                Label coordLabel = new Label("alla coordinata: " + coord);
                coordLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
                coordLabel.setStyle("-fx-text-fill: #043a7e;");


                Button continueButton = new Button("Continua");
                continueButton.setId("continue-button");
                continueButton.setPrefWidth(120);

                continueButton.setOnAction(e -> {
                    meteorStage.close();
                    latch.countDown();
                });

                meteorStage.setOnCloseRequest(e -> {
                    latch.countDown();
                });

                mainContainer.getChildren().addAll(meteorImage, meteorTypeLabel, directionLabel, coordLabel, continueButton);

                Scene scene = new Scene(mainContainer, 300, 350);
                meteorStage.setScene(scene);
                scene.getStylesheets().add(getClass().getResource("/Meteor.css").toExternalForm());
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

                double windowWidth = scene.getWidth();

                double x = screenBounds.getMinX() + (screenBounds.getWidth() - windowWidth) / 2;

                double y = screenBounds.getMinY() + 50;

                meteorStage.setX(x);
                meteorStage.setY(y);

                meteorStage.show();

            } catch (Exception e) {
                System.err.println("Errore durante la visualizzazione dell'allarme meteorite.");
                e.printStackTrace();
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Il thread è stato interrotto durante l'attesa dell'allarme meteora.");
        }
    } ;

    private String getMeteorImagePath(MeteorType type) {
        return switch (type) {
            case LargeMeteor -> "/images/meteortype/large_meteor.jpg";
            case SmallMeteor -> "/images/meteortype/small_meteor.jpg";
            case HeavyCannonFire -> "/images/meteortype/heavy_cannon.jpg";
            case LightCannonFire -> "/images/meteortype/small_cannon.jpg";
        };
    }

    private String getMeteorTypeText(MeteorType type) {
        return switch (type) {
            case LargeMeteor -> "METEORA GROSSA";
            case SmallMeteor -> "METEORA PICCOLA";
            case HeavyCannonFire -> "CANNONATA PESANTE";
            case LightCannonFire -> "CANNONATA LEGGERA";
        };
    }

    private String getDirectionText(Direction direction) {
        return switch (direction) {
            case South -> "da SUD";
            case East -> "da EST";
            case West -> "da OVEST";
            case North -> "da NORD";
        };
    }

    private void rotateMeteorImage(ImageView imageView, Direction direction) {
        double rotation = switch (direction) {
            case North -> 0;
            case South -> 180;
            case East -> 270;
            case West -> 90;
        };
        imageView.setRotate(rotation);
    }

    @Override
    public double declareCannonPower(Ship ship) {
        return 0;
    }

    @Override
    public double declareEnginePower(Ship ship) {
        return 0;
    }


    @Override
    public String chooseConnection() {
        return "";
    }

    @Override
    public String getInput() {
        return "";
    }

    @Override
    public void showGenericError(String error) {

    }






    @Override
    public Pair<Integer, Integer> askCoordsCrewmate(Ship ship) {
        return null;
    }

    @Override
    public void showPlayer(Player player) {

    }



    @Override
    public void printShip(CardComponent[][] ship) {

    }

    @Override
    public void earlyEndFlightResume(Player player) {

    }

    @Override
    public void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {

    }

    /**
     * This method asks the player to select one of the available ship pieces (connected components of tiles).
     * @param pieces a list of ship pieces, each represented as a list of coordinate pairs
     * @param ship
     * @return the index of the selected piece; returns -1 in case of error
     */
    @Override
    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        CompletableFuture<Integer> result = new CompletableFuture<>();

        Platform.runLater(() -> {
            SelectedPieceController controller = new SelectedPieceController();
            controller.setData(pieces, ship);
            Stage stage = new Stage();
            controller.show(stage);
            controller.getSelectedPieceFuture().thenAccept(result::complete);
        });


        try {
            return result.get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method requests an updated version of the ship with invalid connections removed.
     * The update is obtained asynchronously from the build controller.
     * @param ship the original ship containing possibly invalid connections
     * @param connectors a list of connector positions to be validated or removed
     * @return the updated ship after connection validation;
     *         returns null if an error occurs
     */
    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        try {
            return buildcontroller.getUpdatedShip().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method creates a popup showing that a card has been hit, indicating the direction of the hit.
     * @param card the {@link CardComponent} that was hit
     * @param direction the {@link Direction} from which the card was hit
     */
        @Override
        public void showHittedCard(CardComponent card, Direction direction) {
            if (!Platform.isFxApplicationThread()) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    createAndShowStage(card, direction, latch);
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                createAndShowStage(card, direction, null);
            }
            flyghtController.updatePlayerShip();
        }

    /**
     * This method creates a popup window showing a hit card.
     * The window displays the card image and hit direction.
     * @param card the {@link CardComponent} that was hit
     * @param direction the {@link Direction} of the incoming hit
     * @param latch an optional {@link CountDownLatch} used to block the calling thread until the window is closed
     */
    private void createAndShowStage(CardComponent card, Direction direction, CountDownLatch latch) {
        Stage stage = new Stage();
        stage.setTitle("Carta Colpita");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        Stage primaryStage = (Stage) Stage.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);
        if (primaryStage != null) {
            stage.initOwner(primaryStage);
        }

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #D8B7DD;");

        Label infoLabel = new Label("La carta è stata colpita \nin direzione: " + direction.toString());
        infoLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #043a7e;");

        ImageView cardImageView = new ImageView();

        String imagePath = card.getImagePath();
        if(!imagePath.isEmpty()) {
            Image cardImage = new Image(imagePath);
            cardImageView.setImage(cardImage);
        }

            cardImageView.setFitWidth(150);
            cardImageView.setFitHeight(200);
            cardImageView.setPreserveRatio(true);

            cardImageView.setRotate(card.getRotationAngle());

            StackPane imageContainer = new StackPane();
            imageContainer.getChildren().add(cardImageView);
            imageContainer.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");


        Button okButton = new Button("OK");
        okButton.setId("continue-button");
        okButton.setOnAction(e -> {
            stage.close();
            if (latch != null) {
                latch.countDown();
            }
        });

        root.getChildren().addAll(infoLabel, imageContainer, okButton);

        Scene scene = new Scene(root, 350, 300);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/Meteor.css").toExternalForm());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double windowWidth = scene.getWidth();

        double x = screenBounds.getMinX() + (screenBounds.getWidth() - windowWidth) / 2;

        double y = screenBounds.getMinY() + 50;

        stage.setX(x);
        stage.setY(y);

        if (latch != null) {
            stage.show();
        } else {
            stage.showAndWait();
        }
    }



    @Override
    public void printCardAdventure(CardAdventure adventure) {

    }

    /**
     * This method returns a mapping of ship components to the cargo they are carrying.
     * This method currently returns an empty structure and is likely a placeholder
     * for future cargo management logic.
     * @param ship
     * @return an empty map representing cargo distribution across components
     */
    @Override
    public Map<CardComponent,Map <Cargo, Integer>> manageCargo(Ship ship){
        Map<CardComponent, Map<Cargo, Integer>> cargos = new HashMap<>();

        return cargos;
    }

    /**
     * This method asks the player to add a cargo to a valid storage component on their ship.
     * The method opens a GUI for storage selection, then asks where to place the cargo inside it.
     * @param ship the current state of the ship
     * @param cargo the cargo to be added
     * @return a pair containing:
     *         - the coordinates (row, column) of the selected storage component
     *         - the position inside the storage where the cargo will be placed;
     *         returns null if the selection is invalid or canceled
     */
    @Override
    public Pair<Pair<Integer, Integer>, Integer> addCargo(Ship ship, Cargo cargo) {
        Pair<Pair<Integer, Integer>, Integer> pair;
        flyghtController.showStorage(ship, cargo);

        try {
            Pair<Integer,Integer> selection = flyghtController.getcoordsBattery().get();
            flyghtController.resetcoordsBattery();

            if (selection.getKey() == -1 && selection.getValue() == -1) {
                return null;
            }
            Storage selectedStorage = (Storage) ship.getComponent(selection.getKey(), selection.getValue());

            CargoSelector cargoSelector = new CargoSelector();
            int pos = cargoSelector.askCargo(selectedStorage.getCarried_cargos());

            if(pos == -1 ) return null;
            pair = new Pair<>(selection, pos);
            flyghtController.updatePlayerShip();

            return pair;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    public Map<CardComponent, Boolean> batteryUsage(Ship ship){return null;};

    @Override
    public boolean useShield(Ship ship){return true;};


    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public void printCargo(List<Cargo> cargos) {

    }

    @Override
    public Boolean acceptAdventure(String prompt) {
        return flyghtController.acceptAdv(prompt);
    }


    /**
     * This method updates the flight board with the current positions and lap information of all players.
     * This method creates a new instance of {@link FlyghtController} and applies the updates,
     * but does not display the board in the GUI.
     * @param positions a map associating player order indexes with their current positions on the board
     * @param laps a map associating player order indexes with the number of laps completed
     */
    @Override
    public void showBoard(Map<Integer, Player> positions, Map<Integer, Player> laps) {
        FlyghtController controller = new FlyghtController();
        controller.updatePlayerPositions(positions, laps);
    }

    @Override
    public Pair<Integer, Integer> askLivingUnit(Ship ship) {
        return null;
    }

    @Override
    public void showBasicBoard(Map<Integer, Player> positions, Map<Integer, Player> laps) {

    }

    @Override
    public void ShowRanking(Map<String, Double> rank, String type) {

    }

    /**
     * This method asks the player to remove a cargo from the ship.
     * Uses the {@link CargoSelector} to manage the removal process,
     * then updates the visual representation of the ship.
     * @param ship
     */
    @Override
    public void removeCargo(Ship ship) {
        CargoSelector cargoSelector = new CargoSelector();
        cargoSelector.removeCargo(ship);
        flyghtController.updatePlayerShip();
    }


    /**
     * This method asks the player to choose a crewmate (astronaut) to remove from the ship.
     * Displays a visual representation of crewmates, then waits for the player’s input.
     * @param ship the current state of the player's ship
     * @return the coordinates (row, column) of the selected crewmate
     */
    @Override
    public Pair<Integer, Integer> chooseAstronautLosses(Ship ship) {
        getFlyghtController().showCrewmates(player_local.getShip());
        Pair<Integer, Integer> lu = new Pair<>(-1, -1);
        try {
             lu = coordsCrewmate();
                LivingUnit l = (LivingUnit) player_local.getShip().getComponent(lu.getKey(), lu.getValue());
                showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");

        } catch (Exception e) {
            System.err.println("Errore durante la selezione del crewmate: " + e.getMessage());
        }
        return lu;
    }



    /**
     * This method asks the player whether to use a double cannon for a given cannon component,
     * Highlights the selected cannon on the GUI, and if confirmed and
     * requests battery coordinates to activate the cannon.
     * @param cannon the coordinates of the cannon to be activated
     * @return the coordinates of the selected battery; or (-1, -1) if the player declines
     */
    @Override

    public Pair<Integer, Integer> askCannon(Pair<Integer, Integer> cannon) {
        int i = cannon.getKey();
        int j = cannon.getValue();

        flyghtController.highlightCell(i, j);
        flyghtController.showdc(i, j);
        Boolean useDC = useDoubleCannon();
        if (!useDC) {
            return new Pair<>(-1,-1);
        } else {
            return useBattery(player_local.getShip());
        }
    }
    /**
     * This method executes an epidemic event on the ship, simulating the loss of crewmates.
     * @param ship
     */
    @Override
    public void executeEpidemic(Ship ship) {
        Epidemic epidemic = new Epidemic(1,0,CardAdventureType.Epidemic,"");
        epidemic.execute(ship);
        showMessage("è arrivata un epidemia, potresti aver perso astronauti");
        getFlyghtController().updatePlayerShip();
    }

    /**
     * This method asks the player to choose one cargo from a list of available cargo types.
     * @param cargos a list of available {@link Cargo} types
     * @return the index of the selected cargo
     */
    @Override
    public int askCargo(List<Cargo> cargos) {
        CargoSelector selector = new CargoSelector();
        return selector.askCargo(cargos);
    }


    public void setActualcard(CardComponent actualcard) {
        this.actualcard = actualcard;
    }
}