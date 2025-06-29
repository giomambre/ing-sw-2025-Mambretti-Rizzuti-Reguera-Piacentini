package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.WindowEvent;
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

/**
 * Controller for the window that displays a randomly drawn component card.
 * It allows the user to rotate, place, discard, or reserve the card,
 * and to select coordinates for positioning it on the ship.
 */
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

    /**
     * Sets the reference to the main GUI object.
     * @param gui the main GUI instance
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Disables the "Reserve" button.
     */
    public void setBookButton(){
        bookButton.setDisable(true);
    }

    /**
     * Disables the "Discard" button.
     */
    public void setDiscardButton(){
        discardButton.setDisable(true);
    }

    /**
     * Initializes and displays the window for interacting with a randomly drawn card.
     * @param card the component card to be displayed in the window
     * @throws Exception if an error occurs while loading the FXML resource
     */
    public void start(CardComponent card) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RandomCard.fxml"));
        Parent root = loader.load();
        Randomcardcontroller controller = loader.getController();
        Stage randomCardStage = new Stage();
        controller.setStage(randomCardStage);
        this.stage = randomCardStage;
        randomCardStage.setTitle("Random Card");
        randomCardStage.setScene(new Scene(root));
        randomCardStage.centerOnScreen();
        randomCardStage.show();
        controller.showCardImage(card);
    }

    /**
     * Sets the stage used by this controller.
     * @param stage the stage to be assigned
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void setFour(ActionEvent event) {


        gui.getBuildcontroller().addReservedCard(gui.getActualcard());

        if (!action.isDone()) {
            action.complete(4);
        }

        if (stage != null) {
            stage.close();
        }
    }
    @FXML
    public void setThree(ActionEvent event) {
        if (!action.isDone()) {
            action.complete(3);
            if (gui.getPlayer_local().getShip().getExtra_components().contains(gui.getActualcard())) gui.getBuildcontroller().addReservedCard(gui.getActualcard());
        }

        if (stage != null) {
            stage.close();
        }
    }


    @FXML
    public void setTwo(ActionEvent event) {
        coordinatesBox.setVisible(true);
        action.complete(2);
        if(stage!=null) {
            System.out.println("chiudo lo stage");
            stage.close();
        }
    }

    @FXML
    public void setOne(ActionEvent event) throws Exception {
        if (!action.isDone()) action.complete(1);
        action = new CompletableFuture<>();
        stage.close();
    }

    /**
     * @return a CompletableFuture<Integer> with the action code (1=rotate, 2=place, 3=discard, 4=reserve)
     */
    public CompletableFuture<Integer> getAction() {
        if (action == null || action.isDone()) {
            action = new CompletableFuture<>();
        }
        return action;
    }

    /**
     * Displays the image of the provided card in the ImageView.
     * @param card the card to be displayed
     */
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

    /**
     * Completes the corresponding CompletableFuture with the selected x and y values.
     * @param event the action event triggered by the confirmation button
     */
    @FXML
    public void confirmCoords(ActionEvent event) {
        Integer x = xComboBox.getValue();
        Integer y = yComboBox.getValue();

        if (x == null || y == null) {
            System.out.println("Coordinate non selezionate");
            return;
        }

        if (!coords.isDone()) {
            coords.complete(new Pair<>(x, y));
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

    /**
     * Updates the ImageView.
     * @param card the card whose image should be shown
     */
    public void updateImage(CardComponent card) {
            String imagePath = card.getImagePath();
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream == null) {
                System.err.println("Immagine non trovata per il path: " + imagePath);
                return;
            }
            Image image = new Image(stream);
            cardImageView.setImage(image);
            cardImageView.setRotate(card.getRotationAngle());
    }



}

