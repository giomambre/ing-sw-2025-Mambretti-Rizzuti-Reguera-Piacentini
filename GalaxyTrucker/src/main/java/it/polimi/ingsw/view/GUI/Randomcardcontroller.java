package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Randomcardcontroller {
    private GUI gui;
    private CompletableFuture<Integer> action = new CompletableFuture<>();
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

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        Scene scene = new Scene(root);
        stage.setTitle("Random Card");
        stage.setScene(scene);
        stage.centerOnScreen();

        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
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

    public void showCardImage(CardComponent card) {
        String imagePath = card.getImagePath();
        System.out.println(">> Percorso immagine: " + imagePath);

        InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            System.out.println(">> ERRORE: immagine non trovata nel path!");
            return; // esce dal metodo, evitando crash
        }

        Image image = new Image(stream);
        cardImageView.setImage(image);
        System.out.println(">> Immagine caricata correttamente.");
    }
}

