package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
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
   private Stage stage;
    @FXML
    public TextField nicknameField;

    @Override
    public void start(Stage primaryStage) {
            GUI gui = new GUI(); // inizializza GUI qui
            setGui(gui);
            gui.setGuiApplication(this);
            gui.setStage(primaryStage);
            this.stage = primaryStage;
            this.stage = primaryStage;
            Platform.runLater(()->{
                FXMLLoader loader= new FXMLLoader(getClass().getResource("/Startgui.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loader.setController(gui);

                double baseWidth=1000;
                double baseHeight=700;
                Group scalableRoot = new Group();
                Scene scene = new Scene(scalableRoot, baseWidth, baseHeight);
                StackPane wrapper = new StackPane(root);
                scalableRoot.getChildren().add(wrapper);
                root.scaleXProperty().bind(scene.widthProperty().divide(baseWidth));
                root.scaleYProperty().bind(scene.heightProperty().divide(baseHeight));


                primaryStage.setTitle("Startgui");
                primaryStage.setScene(scene);
                primaryStage.show();
                primaryStage.setMaximized(true);
                primaryStage.setOnCloseRequest(event -> {
                    Platform.exit();
                    System.exit(0);
                });
            });

    }

    public Stage getStage() {
        return stage;
    }

    public static void setGui(GUI g) {
        gui = g;
    }

    public static GUI getGui() {
        return gui;
    }

    /*public void sendNickname(ActionEvent actionEvent) {
            Platform.runLater(()->gui.setNicknamescelto(nicknameField.getText()));
            System.out.println("bottone cliccato");
    }*/
}
