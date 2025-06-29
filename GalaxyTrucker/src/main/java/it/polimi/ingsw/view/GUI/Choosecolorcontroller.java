package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.enumerates.Color;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the screen where the player selects their color.
 */
public class Choosecolorcontroller{
    private GUI gui;
    private CompletableFuture<Color> colorChosen;


    @FXML public Button blueRectangle;
    @FXML public Button greenRectangle;
    @FXML public Button redRectangle;
    @FXML public Button yellowRectangle;
    @FXML public Label stateLabel;


    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * This method sets the visibility of the color rectangles based on the list of available colors.
     * @param colors list of available colors to be enabled for selection
     */
    public void setActiveButton(List<Color> colors) {
        blueRectangle.setVisible(false);
        redRectangle.setVisible(false);
        greenRectangle.setVisible(false);
        yellowRectangle.setVisible(false);


        if (colors.contains(Color.BLUE)) {
            blueRectangle.setVisible(true);
        }

        if (colors.contains(Color.RED)) {
            redRectangle.setVisible(true);
        }

        if (colors.contains(Color.GREEN)) {
            greenRectangle.setVisible(true);
        }

        if (colors.contains(Color.YELLOW)) {
            yellowRectangle.setVisible(true);
        }

    }

    /**
     * This method initializes and displays the color selection screen.
     * The method should be called to start the color selection process.
     * @param stage the JavaFX stage to show the color selection
     * @throws Exception if the FXML file cannot be loaded
     */
    public void start(Stage stage) throws Exception {
        this.colorChosen = new CompletableFuture<>();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChooseColor.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        Scene scene = new Scene(root);
        stage.setTitle("Choosing Color");
        setActiveButton(gui.getColorsavailable());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }

    /**
     * Disables all color buttons to prevent further selection.
     * Called after a color has been chosen.
     */
    private void disableAllColorButtons() {
        blueRectangle.setDisable(true);
        yellowRectangle.setDisable(true);
        redRectangle.setDisable(true);
        greenRectangle.setDisable(true);
    }

    /**
     * Initializes the controller after its element has been completely processed.
     */
    @FXML
    public void initialize() {

        if (gui != null && gui.getColorsavailable() != null) {
            setActiveButton(gui.getColorsavailable());
        }
    }

    /**
     * It completes the CompletableFuture with Color.BLUE, disables further interaction, and hides the rectangle.
     * @param event the ActionEvent triggered by clicking the blue button
     * @throws IOException if an error occurs
     */
    @FXML
    public void blueChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.BLUE);
        blueRectangle.setVisible(false);
        disableAllColorButtons();
    }

    /**
     * It completes the CompletableFuture with Color.YELLOW, disables further interaction, and hides the rectangle.
     * @param event the ActionEvent triggered by clicking the blue button
     * @throws IOException if an error occurs
     */
    @FXML
    public void yellowChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.YELLOW);
        yellowRectangle.setVisible(false);
        disableAllColorButtons();
    }

    /**
     * It completes the CompletableFuture with Color.RED, disables further interaction, and hides the rectangle.
     * @param event the ActionEvent triggered by clicking the blue button
     * @throws IOException if an error occurs
     */
    @FXML
    public void redChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.RED);
        redRectangle.setVisible(false);
        disableAllColorButtons();

    }

    /**
     * It completes the CompletableFuture with Color.GREEN, disables further interaction, and hides the rectangle.
     * @param event the ActionEvent triggered by clicking the blue button
     * @throws IOException if an error occurs
     */
    @FXML
    public void greenChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.GREEN);
        greenRectangle.setVisible(false);
        disableAllColorButtons();
    }

    /**
     * Returns the CompletableFuture that will be completed once the user selects a color.
     * @return a CompletableFuture<Color> that completes with the selected color
     */
    public CompletableFuture<Color> getColorChosen() {
        if (colorChosen == null) {
            colorChosen = new CompletableFuture<>();
        }
        return colorChosen;
    }
}