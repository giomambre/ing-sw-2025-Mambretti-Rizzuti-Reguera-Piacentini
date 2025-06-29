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

/**
 * Controller for displaying a player's ship in a separate window.
 * This class is used both during the build phase and the flight phase
 * to visualize the structure of a player's ship.
 */
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

    /**
     * Sets the reference to the Buildcontroller for managing the stage during the build phase.
     * @param buildcontroller the build controller to be referenced
     */
    public void setBuildcontroller(Buildcontroller buildcontroller) {
        this.buildcontroller = buildcontroller;
    }

    /**
     * Sets the reference to the FlyghtController for managing the stage during the flight phase.
     * @param flyghtcontroller the flight controller to be referenced
     */
    public void setFlyghtcontroller(FlyghtController flyghtcontroller) {
        this.flyghtcontroller = flyghtcontroller;
    }

    /**
     * Populates the ship grid with the provided CardComponent.
     * @param nickname   the nickname of the player whose ship is displayed
     * @param shipBoard  a 2D array representing the player's ship grid with CardComponents
     */
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

    /**
     * Makes the close button visible in the build phase view.
     */
    public void showCloseButton() {
        Platform.runLater(() -> {
            closeButton.setVisible(true);
        });
    }

    /**
     * Makes the close button visible in the flight phase view.
     */
    public void showCloseButtonFlyght() {
        Platform.runLater(() -> {
            closeButtonFlyght.setVisible(true);
        });
    }

    /**
     * Closes the stage associated with the Buildcontroller.
     */
    public void closeStage(){
        buildcontroller.getPlayerStage().close();
    }

    /**
     * Closes the stage associated with the FlyghtController.
     */
    public void closeStageFlyght(){
        flyghtcontroller.getPlayerStage().close();
    }


}
