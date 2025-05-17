package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Randomcardcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
    private CompletableFuture<Pair<Integer,Integer>> coords = new CompletableFuture<>();
    private int currentRotation = 0;

    @FXML
    private Button rotateButton;
    @FXML
    private Button positionButton;
    @FXML
    private Button discardButton;
    @FXML
    private Button bookButton;
    @FXML
    private ImageView cardImageView;
    @FXML
    private HBox coordinatesBox;
    @FXML
    private ComboBox<Integer> xComboBox;
    @FXML
    private ComboBox<Integer> yComboBox;
    @FXML
    private Button confirmButton;

    private Stage stage;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void setComboBox() {
        ObservableList<Integer> coordinateValuesy = FXCollections.observableArrayList();
        for (int i = 0; i <= 4; i++) {
            coordinateValuesy.add(i);
        }
        yComboBox.setItems(coordinateValuesy);
        ObservableList<Integer> coordinateValuesx = FXCollections.observableArrayList();
        for (int i = 0; i <= 6; i++) {
            coordinateValuesx.add(i);
        }
        xComboBox.setItems(coordinateValuesx);

    }


    /*public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Stage RandomCardStage = new Stage();
        RandomCardStage.setTitle("Random Card");
        RandomCardStage.setScene(new Scene(root));
        RandomCardStage.centerOnScreen();
        setComboBox();
        coordinatesBox.setVisible(false);
        RandomCardStage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }*/
    public void start(CardComponent card) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
        //loader.setController(this);
        Parent root = loader.load();
        Randomcardcontroller controller = loader.getController();
        Stage randomCardStage = new Stage();
        controller.setStage(randomCardStage);
        this.stage = randomCardStage;
        randomCardStage.setTitle("Random Card");
        randomCardStage.setScene(new Scene(root));
        randomCardStage.centerOnScreen();
        randomCardStage.show();
        controller.setComboBox();

        controller.showCardImage(card);

        randomCardStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    public void setFour(ActionEvent event) {
        action.complete(4);
    }
    @FXML
    public void setThree(ActionEvent event) {
        action.complete(3);
    }
    //posiziona
    @FXML
    public void setTwo(ActionEvent event) {
        System.out.println("bottone set2 chiamato");
        coordinatesBox.setVisible(true);
        action.complete(2);
    }
    @FXML
    public void setOne(ActionEvent event) {
        currentRotation = (currentRotation + 90) % 360;
        cardImageView.setRotate(currentRotation);
        gui.getActualcard().setRotationAngle(currentRotation); // AGGIORNA IL MODELLO
        action.complete(1);
    }

    public CompletableFuture<Integer> getAction() {
        if (action == null) {
            action = new CompletableFuture<>();
        }
        return action;
    }

    public void showCardImage(CardComponent card) {
        String imagePath = card.getImagePath();
        InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            return;
        }
        Image image = new Image(stream);
        cardImageView.setImage(image);
        cardImageView.setRotate(card.getRotationAngle());
    }
    public CompletableFuture<Pair<Integer,Integer>> getCoords() {
        if (coords == null) {
            coords = new CompletableFuture<>();
        }
        return coords;
    }
    @FXML
    public void confirmCoords(ActionEvent event) {
        Integer x = xComboBox.getValue();
        Integer y = yComboBox.getValue();

        if (x == null || y == null) {
            // Mostra errore o ignora l'input incompleto
            System.out.println("Coordinate non selezionate");
            return;
        }

        // Completa la future solo se non è già completata
        if (!coords.isDone()) {
            coords.complete(new Pair<>(x, y));
            // Nascondi o disabilita il box se vuoi
            coordinatesBox.setVisible(false);
            System.out.println("Coordinate confermate: " + x + ", " + y);
        }
        if(stage!=null) {
            System.out.println("chiudo lo stage");
            stage.close();
        }
    }
    public Stage getStage() {
        return stage;
    }
}

