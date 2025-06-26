package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.application.Platform;
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

    @FXML
    private Button closeButtonFlyght;

    private Buildcontroller buildcontroller;

    private FlyghtController flyghtcontroller;

    public void setBuildcontroller(Buildcontroller buildcontroller) {
        this.buildcontroller = buildcontroller;
    }

    public void setFlyghtcontroller(FlyghtController flyghtcontroller) {
        this.flyghtcontroller = flyghtcontroller;
    }

    public void setPlayerShip(String nickname, CardComponent[][] shipBoard) {

        playerNameLabel.setText("Nave di " + nickname);
        shipGrid.getChildren().clear();
        playerNameLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-text-fill: #4B0082 ; -fx-font-weight: bold;");

        for (int i = 0; i < shipBoard.length; i++) {
            for (int j = 0; j < shipBoard[0].length; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(53, 53);

                CardComponent component = shipBoard[i][j];
                if(component==null||component.getComponentType()== ComponentType.NotAccessible||component.getComponentType()==ComponentType.Empty){

                    cell.setStyle("-fx-background-color: transparent;");

                }
                else  {

                    Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(component.getImagePath())));
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(66.25);
                    iv.setFitHeight(66.25);
                    iv.setPreserveRatio(true);
                    iv.setRotate(component.getRotationAngle());
                    cell.getChildren().add(iv);
                }

                shipGrid.add(cell, j, i);
            }
        }
    }

    public void showCloseButton() {
        Platform.runLater(() -> {
            closeButton.setVisible(true);
        });
    }

    public void showCloseButtonFlyght() {
        Platform.runLater(() -> {
            closeButtonFlyght.setVisible(true);
        });
    }

    public void closeStage(){
        buildcontroller.getPlayerStage().close();
    }
    public void closeStageFlyght(){
        flyghtcontroller.getPlayerStage().close();
    }


}
