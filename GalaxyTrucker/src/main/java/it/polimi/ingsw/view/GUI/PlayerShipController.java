package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class PlayerShipController {
    @FXML
    private Label playerNameLabel;
    @FXML
    private GridPane shipGrid;
    @FXML
    private Button closeButton;

    private Buildcontroller buildcontroller;

    public void setBuildcontroller(Buildcontroller buildcontroller) {
        this.buildcontroller = buildcontroller;
    }

    public void setPlayerShip(String nickname, CardComponent[][] shipBoard) {

        playerNameLabel.setText("Nave di " + nickname);
        shipGrid.getChildren().clear();

        for (int i = 0; i < shipBoard.length; i++) {
            for (int j = 0; j < shipBoard[0].length; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);

                CardComponent component = shipBoard[i][j];
                if(component==null||component.getComponentType()== ComponentType.NotAccessible||component.getComponentType()==ComponentType.Empty){
                    if (component == null) {
                        System.out.println("DEBUG: shipBoard[" + i + "][" + j + "] is null");
                    }
                    cell.setStyle("-fx-background-color: lightgray;");

                }
                else  {
                    Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(component.getImagePath())));
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(38);
                    iv.setFitHeight(38);
                    iv.setPreserveRatio(true);
                    iv.setRotate(component.getRotationAngle());
                    cell.getChildren().add(iv);
                }

                shipGrid.add(cell, j, i);
            }
        }
    }

    public void closeStage(){
        buildcontroller.getPlayerStage().close();
    }
}
