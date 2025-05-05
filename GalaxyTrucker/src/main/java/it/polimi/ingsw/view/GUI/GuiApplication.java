package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.ObjectOutputStream;
import java.util.List;

public class GuiApplication extends Application  {
   private StackPane root;
   private ConnectionScreen connectionScreen;
   private NicknameScreen nicknameScreen;

   @Override
    /*public void start(Stage stage) {
       this.root = new StackPane(); //contiene le varie schermate del gioco che si alterneranno in momenti diversi
        //connectionScreen = new ConnectionScreen();
        //nicknameScreen=new NicknameScreen();

        //root.getChildren().addAll(connectionScreen,nicknameScreen);
        //nicknameScreen.setVisible(false);

       Scene scene = new Scene(root, Color.ROYALBLUE);
       stage.setTitle("Galaxy Trucker");
       stage.setScene(scene);
       stage.show();
    }
    public static void main(String[] args) {
       launch(args);
    }*/

   public void start(Stage primaryStage) {
       TextField inputField = new TextField();
       inputField.setPromptText("Scrivi qualcosa...");

       Button submitButton = new Button("Invia");

       submitButton.setOnAction(e -> {
           String input = inputField.getText();
           System.out.println("Hai scritto: " + input);
           inputField.clear();
       });

       VBox layout = new VBox(10); // spaziatura verticale
       layout.getChildren().addAll(inputField, submitButton);

       Scene scene = new Scene(layout, 300, 150);
       primaryStage.setScene(scene);
       primaryStage.setTitle("Input Panel");
       primaryStage.show();
   }


}
