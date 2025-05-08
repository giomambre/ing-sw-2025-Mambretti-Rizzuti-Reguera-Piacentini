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

public class Choosecolorcontroller{
    private GUI gui;
    private List<Color> colorsavailable;
    private CompletableFuture<Color> colorChosen;


    @FXML public Button blueRectangle;
    @FXML public Button greenRectangle;
    @FXML public Button redRectangle;
    @FXML public Button yellowRectangle;
    @FXML public Label stateLabel;


    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setColorsavailable(List<Color> colorsavailable) {
        Platform.runLater(() -> {
            this.colorsavailable = new ArrayList<>(colorsavailable);
            setActiveButton(this.colorsavailable);
        });
    }

    public void setActiveButton(List<Color> colors) {
        if(colors.contains(Color.BLUE)) {
            blueRectangle.setVisible(true);
        }else{
            blueRectangle.setVisible(false);
        }

        if(colors.contains(Color.RED)) {
            redRectangle.setVisible(true);
        }else{
            redRectangle.setVisible(false);
        }

        if(colors.contains(Color.GREEN)){
            greenRectangle.setVisible(true);
        }else {
            greenRectangle.setVisible(false);
        }

        if(colors.contains(Color.YELLOW)){
            yellowRectangle.setVisible(true);
        }else {
            yellowRectangle.setVisible(false);
        }
    }

    /**
     * Initializes and displays the primary stage for the color selection UI.
     *
     * @param stage the primary stage for this application
     * @throws Exception if an error occurs during loading the FXML file or setting the scene
     */
    /*public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChooseColor.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        gui.setStage(stage);
        Scene scene = new Scene(root);
        stage.setTitle("Choosing Color");
        setColorsavailable(gui.getColorsavailable());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
        stage.setOnCloseRequest((event->{
            Platform.exit();
            System.exit(0);
        }));
    }*/
    public void start(Parent root) {
        Scene scene = new Scene(root);
        gui.getStage().setTitle("Choosing Color");
        gui.getStage().setScene(scene);
        gui.getStage().centerOnScreen();
        gui.getStage().show();
        gui.getStage().setOnCloseRequest((event -> {
            Platform.exit();
            System.exit(0);
        }));
    }



    @FXML
    public void blueChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.BLUE);
        gui.setChooseColorScreenOpen(false);
    }
    @FXML
    public void yellowChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.YELLOW);
        gui.setChooseColorScreenOpen(false);
    }
    @FXML
    public void redChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.RED);
        gui.setChooseColorScreenOpen(false);
    }
    @FXML
    public void greenChosen(ActionEvent event) throws IOException {
        colorChosen.complete(Color.GREEN);
        gui.setChooseColorScreenOpen(false);
    }

    public CompletableFuture<Color> getColorChosen() {
        if (colorChosen == null) {
            colorChosen = new CompletableFuture<>();
        }
        return colorChosen;
    }



}
