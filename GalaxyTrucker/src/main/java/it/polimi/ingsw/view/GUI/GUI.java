package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.enumerates.Cargo;
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
import it.polimi.ingsw.model.enumerates.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class GUI implements View {

    Joingamecontroller joingamecontroller;
    Numplayercontroller numplayercontroller;
    Guiselectcontroller guiselectcontroller;
    Choosecolorcontroller choosecolorcontroller;
    Stage stage;
    private String nicknamescelto;
    @FXML
    public TextField nicknameField;
    private GuiApplication application;
    private ClientCallBack clientCallback;
    private List<Integer> lobbies;
    private List<Color> colorsavailable;
    private boolean chooseColorScreenOpen = false;


    public Stage getStage(){
        return stage;
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
            System.out.println(">>> [DEBUG] updateColors called with: " + newColors);
            this.colorsavailable = newColors;
            if (choosecolorcontroller != null) {
                choosecolorcontroller.setActiveButton(newColors);
                choosecolorcontroller.stateLabel.setText("Colori aggiornati!");
            }
        });
    }
    /*public void createchoosecolorscreen(List<Color> colors) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChooseColor.fxml"));
                Parent root = loader.load();
                Choosecolorcontroller controller = loader.getController(); // ✅ ottieni il controller FXML

                controller.setGUI(this);
                controller.setColorsavailable(colors); // imposta i colori disponibili
                this.choosecolorcontroller = controller;
                this.colorsavailable = colors;
                setChooseColorScreenOpen(true);

                controller.start(root); // adesso il controller si occupa di mostrare lo stage
                future.complete(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                future.completeExceptionally(ex);
            }
        });
        try {
            future.get(); // aspetta che la GUI venga inizializzata
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }*/





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
    public void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {

    }

    @Override
    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        return 0;
    }

    @Override
    public int selectDeck() {
        return 0;
    }

    @Override
    public int crewmateAction(Pair<Integer,Integer> component) {
        return 0;
    }

    @Override
    public Map<CardComponent,Integer> chooseAstronautLosses(Ship ship, int astronautLoss){
        Map<CardComponent,Integer> astronaut_losses = new HashMap<>();

        return astronaut_losses;
    };

    @Override
    public Map<CardComponent,Map <Cargo, Integer>> manageCargo(Ship ship){
        Map<CardComponent, Map<Cargo, Integer>> cargos = new HashMap<>();

        return cargos;
    };

    @Override
    public Map<CardComponent, Map<Cargo,Integer>> addCargo(Ship ship, List<Cargo> cargoReward){
        Map<CardComponent, Map<Cargo,Integer>> cargos = new HashMap<>();
        Map<Cargo, Integer> cargo = new HashMap<>();

        return cargos;
    }

    @Override
    public CardComponent useBattery(Ship ship){return null;};

    @Override
    public Map<CardComponent, Boolean> batteryUsage(Ship ship){return null;};

    @Override
    public boolean useShield(Ship ship){return true;};


    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int askSecuredCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int showCard(CardComponent card) {
        return 0;
    }

    @Override
    public void showBoard(Board board) {

    }

    @Override
    public Pair<Integer, Integer> askCoords(Ship ship) {
        return null;
    }

    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        return null;
    }

    @Override
    public int askCannon() {
        return 0;
    }


}