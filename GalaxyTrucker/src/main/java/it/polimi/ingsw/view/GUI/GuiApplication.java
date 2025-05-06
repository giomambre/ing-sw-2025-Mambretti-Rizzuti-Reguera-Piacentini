package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.fxml.FXMLLoader;
import java.io.ObjectOutputStream;
import java.util.List;

public class GuiApplication extends Application  {
   private static GUI gui;

    @Override
    public void start(Stage primaryStage) {
            GUI gui = new GUI(); // inizializza GUI qui
            setGui(gui);
            Platform.runLater(()->{
                //Parent root = new FXMLLoader(getClass().getResource("sample.fxml"));
                primaryStage.setTitle("Galaxy Trucker");
                primaryStage.show();
            });

    }

    public static void setGui(GUI g) {
        gui = g;

    }

    public static GUI getGui() {
        return gui;
    }
}
