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


public class GUI implements View {

    Joingamecontroller joingamecontroller;
    Numplayercontroller numplayercontroller;
    Guiselectcontroller guiselectcontroller;
    Choosecolorcontroller choosecolorcontroller;
    Buildcontroller buildcontroller;
    Randomcardcontroller randomcardcontroller;
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

    @FXML
    public void sendNickname(ActionEvent event) {
        System.out.println("sendNickname() chiamato");
        this.nicknamescelto = nicknameField.getText();
        if (clientCallback != null) {
            clientCallback.sendNicknameToServer(nicknamescelto);
        }
        System.out.println("nicknamescelto: " + nicknamescelto);
    }
    @Override
    public String askNickname()  {
        return nicknamescelto;
    }

    /*public void createjoingamecontroller(){
        Platform.runLater(() -> {
            try {
                Joingamecontroller joingamecontroller = new Joingamecontroller();
                this.joingamecontroller = joingamecontroller;
                joingamecontroller.setGui(this);
                joingamecontroller.start(this.stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }*/

    public void createjoingamecontroller(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Joingamecontroller controller = new Joingamecontroller();
                controller.setGui(this);
                this.joingamecontroller = controller;
                controller.start(this.stage);
                future.complete(null);  // Segnala che la GUI è pronta
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get(); // aspetta che la GUI venga inizializzata
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int askCreateOrJoin() {
        try {
            return joingamecontroller.getChoiceFuture().get(); // blocca finché non c'è risposta
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void createnumplayerscontroller(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Numplayercontroller controller = new Numplayercontroller();
                controller.setGui(this);
                this.numplayercontroller = controller;
                controller.start(this.stage);
                future.complete(null);  // Segnala che la GUI è pronta
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get(); // aspetta che la GUI venga inizializzata
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
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

    @Override
    public void updateOtherPlayers(List<Player> otherPlayers) {

    }

    @Override
    public void updateAdventureDeck(Map<Direction, List<CardAdventure>> adventureDeck) {

    }

    @Override
    public void updateFacedUpCards(List<CardComponent> facedUpDeck) {

    }

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

    public void createselectlobbyscreen(List<Integer> lobbies){
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Guiselectcontroller controller = new Guiselectcontroller();
                controller.setGui(this);
                this.guiselectcontroller = controller;
                this.lobbies = lobbies;
                controller.start(this.stage);
                future.complete(null);  // Segnala che la GUI è pronta
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get(); // aspetta che la GUI venga inizializzata
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        try {
            return guiselectcontroller.getSelectedLobbyFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }
//
    public void createchoosecolorscreen(List<Color> colors) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                Choosecolorcontroller controller = new Choosecolorcontroller();
                controller.setGUI(this);
                this.choosecolorcontroller = controller;
                this.colorsavailable = colors;
                controller.start(this.stage);
                future.complete(null);  // Segnala che la GUI è pronta
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get(); // aspetta che la GUI venga inizializzata
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Color askColor(List<Color> colors) {
        try {
            return choosecolorcontroller.getColorChosen().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void updateColors(List<Color> newColors) {
        Platform.runLater(() -> {
            this.colorsavailable = newColors;
            if (choosecolorcontroller != null) {
                choosecolorcontroller.setActiveButton(newColors);
                choosecolorcontroller.stateLabel.setText("Colori ancora disponibili:");
            }
        });
    }


    public void createbuildscreen() {
        if (this.buildcontroller != null && isbuildscreenactive) {
            Platform.runLater(() -> {
                    stage.toFront();
                    stage.requestFocus();
                    stage.show(); // riportala in primo piano
                    buildcontroller.updateFaceUpCardsDisplay();
            });
            return;
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/BuildShip.fxml"));
                Parent root = loader.load();

                Buildcontroller controller = loader.getController(); // usa controller FXML
                controller.setGUI(this); // passa la GUI, con il client corretto
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

                future.complete(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get(); // aspetta la GUI
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createFlyghtScreen(Map<Integer, Player> positions, Map<Integer, Player> laps) {
        if (this.flyghtController != null && isFlyghtScreenActive) {
            Platform.runLater(() -> {
                stage.toFront();
                stage.requestFocus();
                stage.show(); // riportala in primo piano
                flyghtController.updatePlayerShip();
            });
            return;
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FlyghtBoard.fxml"));
                Parent root = loader.load();

                FlyghtController controller = loader.getController(); // usa controller FXML
                controller.setGUI(this); // passa la GUI, con il client corretto
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

                // Dopo che tutto è pronto, inizializza i componenti
                controller.updatePlayerShip();

                // Se ci sono già posizioni dei giocatori, aggiornale
                if (client != null && client.getOther_players_local() != null) {

                     controller.updatePlayerPositions(positions, laps);
                }

                // Aggiorna il giocatore corrente se disponibile
                if (client != null && client.getPlayer_local() != null) {
                    controller.updateCurrentPlayer(client.getPlayer_local().getNickname());
                }

                future.complete(null);  // GUI pronta
            } catch (Exception ex) {
                ex.printStackTrace();
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get(); // aspetta la GUI
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public FlyghtController getFlyghtController() {
        return flyghtController;
    }

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
    public void createrandomcardcontroller(CardComponent card) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.actualcard = card;

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
                Parent root = loader.load();
                Randomcardcontroller controller = loader.getController();

                controller.setGui(this);
                Stage stage = new Stage(); // Crea la stage qui
                controller.setStage(stage); // Passa la stage al controller
                //controller.setComboBox();
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

            /*stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });*/

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

    public void createCrewmateSelectionController(Pair<Integer, Integer> coords, List<CrewmateType> supportedCrewmates) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrewmateSelection.fxml"));
                Parent root = loader.load();
                CrewmateSelectionController controller = loader.getController();

                controller.setCardWithSupportedCrewmates(coords, supportedCrewmates); // usa il metodo alternativo
                controller.setStage(new Stage());

                Stage stage = controller.getStage();
                stage.setTitle("Crewmate Selection");
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
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

    @Override
    public int showCard(CardComponent card) {
        try {
            return randomcardcontroller.getAction().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planets_taken) {
        return 0;
    }

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

    @Override
    public int crewmateAction(Pair<Integer,Integer> component) {
        try {
            return crewmateSelectionController.getCrewmate().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<CrewmateType> getCrewmates(Pair<Integer,Integer> coords) {
        CardComponent[][] ship = client.getPlayer_local().getShip().getShipBoard();
        CardComponent component = ship[coords.getKey()][coords.getValue()];
        List<CrewmateType> crewmateType = client.getPlayer_local().getShip().checkAlienSupport(component);
        crewmateType.add(CrewmateType.Astronaut);
        return crewmateType;
    }


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
//        try {
//            return flyghtController.getNextMeteor().get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            return -1;
//        } finally {
//            flyghtController.resetNextMeteor();
//        }
        return 0;
    }

    @Override
    public Pair<Integer, Integer> askEngine(Pair<Integer, Integer> engine) {
        int i = engine.getKey();
        int j = engine.getValue();
        FlyghtController controller = getFlyghtController();

        controller.highlightCell(i, j);
        controller.showdc(i, j);
        Boolean useDC = useDoubleCannon();
        getFlyghtController().resetHighlights(i, j);
        if (!useDC) {
            return new Pair<>(-1,-1);
        } else {
            return useBattery(player_local.getShip());
        }

    }

    @Override
    public Pair<Integer, Integer> useBattery(Ship ship) {
        Pair<Integer, Integer> battery = new Pair<>(-1, -1);
        Map<Pair<Integer, Integer>, Boolean> battery_usage_os = new HashMap<Pair<Integer, Integer>, Boolean>();
        Battery card_battery;
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(i, j);
                if (card.getComponentType() != ComponentType.Battery) {
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



    @Override
    public void printMeteor(Pair<MeteorType, Direction> meteor, int coord) {
        CountDownLatch latch = new CountDownLatch(1);

        // Esegui tutto sul thread JavaFX
        Platform.runLater(() -> {
            try {
                Stage meteorStage = new Stage();

                meteorStage.initModality(Modality.APPLICATION_MODAL);
                meteorStage.setTitle("Allarme Meteora!");
                meteorStage.setResizable(false);

                VBox mainContainer = new VBox(15);
                mainContainer.setAlignment(Pos.CENTER);
                mainContainer.setPadding(new Insets(20));
                mainContainer.setStyle("-fx-background-color: #2c3e50;");

                String imagePath = getMeteorImagePath(meteor.getKey());

                ImageView meteorImage = new ImageView();
                try {
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    meteorImage.setImage(image);
                    meteorImage.setFitWidth(150);
                    meteorImage.setFitHeight(150);
                    meteorImage.setPreserveRatio(true);
                    rotateMeteorImage(meteorImage, meteor.getValue());
                } catch (Exception e) {
                    meteorImage.setFitWidth(150);
                    meteorImage.setFitHeight(150);
                    System.err.println("Impossibile caricare l'immagine: " + imagePath);
                    e.printStackTrace();
                }

                Label meteorTypeLabel = new Label(getMeteorTypeText(meteor.getKey()));
                meteorTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                meteorTypeLabel.setStyle("-fx-text-fill: #e74c3c;");

                Label directionLabel = new Label("in arrivo " + getDirectionText(meteor.getValue()));
                directionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                directionLabel.setStyle("-fx-text-fill: #ecf0f1;");

                Label coordLabel = new Label("alla coordinata: " + coord);
                coordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                coordLabel.setStyle("-fx-text-fill: #f39c12;");

                // Bottone Continua
                Button continueButton = new Button("Continua");
                continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                continueButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 10px 20px;");
                continueButton.setPrefWidth(120);

                // Event handler per il bottone
                continueButton.setOnAction(e -> {
                    meteorStage.close();
                    latch.countDown(); // Rilascia il thread in attesa
                });

                // Aggiungi anche la possibilità di chiudere con ESC o clic sulla X
                meteorStage.setOnCloseRequest(e -> {
                    latch.countDown(); // Rilascia il thread anche se chiuso diversamente
                });

                mainContainer.getChildren().addAll(meteorImage, meteorTypeLabel, directionLabel, coordLabel, continueButton);

                Scene scene = new Scene(mainContainer, 300, 400); // Altezza aumentata per il bottone
                meteorStage.setScene(scene);

                meteorStage.show();

            } catch (Exception e) {
                System.err.println("Errore durante la visualizzazione dell'allarme meteorite.");
                e.printStackTrace();
                latch.countDown(); // Rilascia in caso di errore
            }
        }); // Fine di Platform.runLater

        // Blocca il thread corrente finché l'utente non preme Continua
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Il thread è stato interrotto durante l'attesa dell'allarme meteora.");
        }
    }
    // Metodo ausiliario per ottenere il percorso dell'immagine
    private String getMeteorImagePath(MeteorType type) {
        return switch (type) {
            case LargeMeteor -> "/images/meteortype/large_meteor.jpg";
            case SmallMeteor -> "/images/meteortype/small_meteor.jpg";
            case HeavyCannonFire -> "/images/meteortype/heavy_cannon.jpg";
            case LightCannonFire -> "/images/meteortype/light_cannon.jpg";
        };
    }

    // Metodo ausiliario per ottenere il testo del tipo di meteora
    private String getMeteorTypeText(MeteorType type) {
        return switch (type) {
            case LargeMeteor -> "METEORA GROSSA";
            case SmallMeteor -> "METEORA PICCOLA";
            case HeavyCannonFire -> "CANNONATA PESANTE";
            case LightCannonFire -> "CANNONATA LEGGERA";
        };
    }

    // Metodo ausiliario per ottenere il testo della direzione
    private String getDirectionText(Direction direction) {
        return switch (direction) {
            case South -> "da SUD";
            case East -> "da EST";
            case West -> "da OVEST";
            case North -> "da NORD";
        };
    }

    // Metodo ausiliario per ruotare l'immagine basata sulla direzione
    private void rotateMeteorImage(ImageView imageView, Direction direction) {
        double rotation = switch (direction) {
            case North -> 0;      // Verso il basso
            case South -> 180;    // Verso l'alto
            case East -> 270;     // Verso sinistra
            case West -> 90;      // Verso destra
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

        // Aspetta che l'utente selezioni il pezzo
        try {
            return result.get(); // blocca fino a quando viene completato
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // fallback in caso di errore
        }
    }

    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        try {
            return buildcontroller.getUpdatedShip().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
/*
    @Override
    public int nextMeteor() {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        Platform.runLater(() -> {
//            Stage popupStage = new Stage();
//            popupStage.initModality(Modality.APPLICATION_MODAL);
//            popupStage.setTitle("Continua l'Avventura");
//            popupStage.setResizable(false);
//
//            VBox mainContainer = new VBox(15);
//            mainContainer.setAlignment(Pos.CENTER);
//            mainContainer.setPadding(new Insets(20));
//            mainContainer.setStyle("-fx-background-color: #2c3e50;");
//
//            Label messageLabel = new Label("Premi Continua per proseguire.");
//            messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//            messageLabel.setStyle("-fx-text-fill: #ecf0f1;");
//
//            Button continueButton = new Button("Continua");
//            continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//            continueButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
//            continueButton.setPrefWidth(100);
//
//            continueButton.setOnAction(e -> {
//                popupStage.close();
//                latch.countDown(); // Rilascia il thread in attesa
//            });
//
//            mainContainer.getChildren().addAll(messageLabel, continueButton);
//
//            Scene scene = new Scene(mainContainer, 250, 150);
//            popupStage.setScene(scene);
//
//            popupStage.show();
//        });
//
//        try {
//            latch.await(); // Aspetta che l'utente prema il bottone
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            System.err.println("Il thread è stato interrotto durante l'attesa per il popup Continua.");
//            return 0; // Ritorna 0 in caso di interruzione
//        }
//
      return 1;
   }*/


        @Override
        public void showHittedCard(CardComponent card, Direction direction) {
            // Se non siamo sul JavaFX Application Thread, usa CountDownLatch
            if (!Platform.isFxApplicationThread()) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    createAndShowStage(card, direction, latch);
                });
                try {
                    latch.await(); // Aspetta che la finestra venga chiusa
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                // Se siamo già sul JavaFX thread, crea direttamente lo stage
                createAndShowStage(card, direction, null);
            }
            flyghtController.updatePlayerShip();
        }

    private void createAndShowStage(CardComponent card, Direction direction, CountDownLatch latch) {
        // Crea un nuovo stage
        Stage stage = new Stage();
        stage.setTitle("Carta Colpita");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        // Ottieni la finestra principale per impostare il proprietario
        Stage primaryStage = (Stage) Stage.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);
        if (primaryStage != null) {
            stage.initOwner(primaryStage);
        }

        // Layout principale
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2E8B57;");

        // Label informativa
        Label infoLabel = new Label("La carta è stata colpita in direzione: " + direction.toString());
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // ImageView per la carta
        ImageView cardImageView = new ImageView();

        // Carica l'immagine della carta
        String imagePath = card.getImagePath(); // Assumendo che CardComponent abbia questo metodo
        Image cardImage = new Image(imagePath);
        cardImageView.setImage(cardImage);

        // Imposta dimensioni dell'immagine
        cardImageView.setFitWidth(150);
        cardImageView.setFitHeight(200);
        cardImageView.setPreserveRatio(true);

        // Applica la rotazione in base alla direzione
        cardImageView.setRotate(card.getRotationAngle());

        // Container per l'immagine con bordo
        StackPane imageContainer = new StackPane();
        imageContainer.getChildren().add(cardImageView);
        imageContainer.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-background-color: white;");
        imageContainer.setPadding(new Insets(10));

        // Bottone OK
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-font-size: 14px; -fx-min-width: 80px; -fx-min-height: 35px;");
        okButton.setOnAction(e -> {
            stage.close();
            if (latch != null) {
                latch.countDown(); // Sblocca il thread chiamante
            }
        });

        // Aggiunge tutti i componenti al layout
        root.getChildren().addAll(infoLabel, imageContainer, okButton);

        // Crea la scena
        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);

        // Centra lo stage rispetto al parent
        stage.centerOnScreen();

        // Mostra lo stage
        if (latch != null) {
            stage.show(); // Non usa showAndWait() perché stiamo gestendo il blocking con CountDownLatch
        } else {
            stage.showAndWait(); // Usa showAndWait() se siamo già sul JavaFX thread
        }
    }



    @Override
    public void printCardAdventure(CardAdventure adventure) {

    }



    @Override
    public Map<CardComponent,Map <Cargo, Integer>> manageCargo(Ship ship){
        Map<CardComponent, Map<Cargo, Integer>> cargos = new HashMap<>();

        return cargos;
    }

    @Override
    public Pair<Pair<Integer, Integer>, Integer> addCargo(Ship ship, Cargo cargo) {
        Pair<Pair<Integer, Integer>, Integer> pair;
        flyghtController.showStorage(ship, cargo);

        try {
            Pair<Integer,Integer> selection = flyghtController.getcoordsBattery().get();
            flyghtController.resetcoordsBattery();

            // Controlla se la selezione è valida (non -1, -1)
            if (selection.getKey() == -1 && selection.getValue() == -1) {
                // Nessuno storage disponibile o selezione annullata
                return null;
            }
            Storage selectedStorage = (Storage) ship.getComponent(selection.getKey(), selection.getValue());

            CargoSelector cargoSelector = new CargoSelector();
            int pos = cargoSelector.askCargo(selectedStorage.getCarried_cargos());

            pair = new Pair<>(selection, pos);
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

    @Override
    public void removeCargo(Ship ship) {

    }

    @Override
    public Pair<Integer, Integer> chooseAstronautLosses(Ship ship) {
        return null;
    }




    @Override
    public Pair<Integer, Integer> askCannon(Pair<Integer, Integer> cannon) {
        return null;
    }

    @Override
    public void executeEpidemic(Ship ship) {
        flyghtController.executeEpidemic(ship);
    }

    @Override
    public int askCargo(List<Cargo> cargos) {
        CargoSelector selector = new CargoSelector();
        return selector.askCargo(cargos);
    }


    public void setActualcard(CardComponent actualcard) {
        this.actualcard = actualcard;
    }
}