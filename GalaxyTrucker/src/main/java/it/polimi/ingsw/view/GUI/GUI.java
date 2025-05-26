package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class GUI implements View {

    Joingamecontroller joingamecontroller;
    Numplayercontroller numplayercontroller;
    Guiselectcontroller guiselectcontroller;
    Choosecolorcontroller choosecolorcontroller;
    Buildcontroller buildcontroller;
    Randomcardcontroller randomcardcontroller;
    Stage stage;
    private String nicknamescelto;
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
    public void showMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Messaggio");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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

                // Dopo che tutto è pronto, setta i bottoni
                controller.setupPlayerButtons(client.getOther_players_local());
                controller.initializeShipBoard();

                controller.updateFaceUpCardsDisplay();

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
                Randomcardcontroller controller = loader.getController(); // usa questo

                controller.setGui(this); // associa la GUI
                controller.setStage(new Stage());
                //controller.setComboBox();
                controller.showCardImage(card);

                Stage stage = controller.getStage();
                stage.setTitle("Random Card");
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();

                /*stage.setOnCloseRequest(event -> {
                    Platform.exit();
                    System.exit(0);
                });*/

                this.randomcardcontroller = controller; // salva quello GIUSTO

                future.complete(null); // GUI pronta
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        try {
            future.get(); // aspetta la GUI
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
            return buildcontroller.getCrewmate().get();
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

    @Override
    public void printMeteors(List<Pair<MeteorType, Direction>> meteors) {

    }

    @Override
    public void printMeteor(Pair<MeteorType, Direction> meteor, int coord) {

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
        return 0;
    }

    @Override
    public int nextMeteor() {
        return 0;
    }

    @Override
    public void showHittedCard(CardComponent card, Direction direction) {

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
        return null;
    }

    @Override
    public Pair<Integer,Integer> useBattery(Ship ship){return null;};

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
    public Boolean acceptAdventure() {
        return null;
    }




    @Override
    public void showBoard(Map<Integer, Player> positions, Map<Integer, Player> laps) {

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
    public Pair<Integer, Integer> chooseAstronautLosses(Ship ship) {
        return null;
    }



    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> askEngine(Pair<Integer, Integer> cannon) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> askCannon(Pair<Integer, Integer> cannon) {
        return null;
    }

    @Override
    public void executeEpidemic(Ship ship) {

    }

    @Override
    public int askCargo(List<Cargo> cargos) {
        return 0;
    }


    public void setActualcard(CardComponent actualcard) {
        this.actualcard = actualcard;
    }
}