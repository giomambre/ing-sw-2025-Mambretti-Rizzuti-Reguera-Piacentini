package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.StandardMessageClient;
import it.polimi.ingsw.view.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class GUI implements View {

    NicknameController nicknameController;
    Stage primaryStage;
    //private boolean nicknamesettato=false;
    private String nicknamescelto;
    @FXML
    public TextField nicknameField;
    private CompletableFuture<String> nicknameFuture;
    private GuiApplication application;
    private ClientCallBack clientCallback;
    private UUID client;
    private static ObjectOutputStream out;

    public void setClientCallback(ClientCallBack callback) {
        this.clientCallback = callback;
    }
    public void setClient(UUID client) {
        this.client = client;
    }



    public GUI() {
    }

    public void setGuiApplication(GuiApplication application) {
        this.application = application;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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


    @Override
    public void showMessage(String message) {

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
    public int askCreateOrJoin() {
        return 0;
    }

    @Override
    public int askNumPlayers() {
        return 0;
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        return 0;
    }

    @Override
    public Color askColor(List<Color> colors) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> askCoordsCrewmate(Ship ship) {
        return null;
    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public void showShip(String nickname) {

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
    public Pair<Integer, Integer> askCoords(Ship ship) {
        return null;
    }

    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        return null;
    }



}