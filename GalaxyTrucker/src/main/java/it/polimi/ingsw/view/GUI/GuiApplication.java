package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class GuiApplication extends Application  {
   private static GUI gui;
    @FXML
    public TextField nicknameField;

    @Override
    public void start(Stage primaryStage) {
            GUI gui = new GUI(); // inizializza GUI qui
            setGui(gui);
            gui.setPrimaryStage(primaryStage);
            Platform.runLater(()->{
                FXMLLoader loader= new FXMLLoader(getClass().getResource("/Nickname.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loader.setController(this);
                Scene scene = new Scene(root);
                primaryStage.setTitle("Username");
                primaryStage.setScene(scene);
                primaryStage.show();

                primaryStage.setOnCloseRequest(event -> {
                    Platform.exit();
                    System.exit(0);
                });
            });

    }


    public static void setGui(GUI g) {
        gui = g;

    }

    public static GUI getGui() {
        return gui;
    }

    public void sendNickname(ActionEvent actionEvent) {
            Platform.runLater(()->gui.setNicknamescelto(nicknameField.getText()));
            System.out.println("bottone cliccato");
    }
}
