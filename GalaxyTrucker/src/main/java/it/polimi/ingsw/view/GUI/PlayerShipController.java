package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
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

    public void setPlayerShip(String nickname, Ship ship) {
        playerNameLabel.setText("Nave di " + nickname);
        shipGrid.getChildren().clear();
        playerNameLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-text-fill: #4B0082 ; -fx-font-weight: bold;");

        CardComponent[][] shipBoard = ship.getShipBoard();

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
        restoreOverlays(ship);
    }

    private void restoreOverlays(Ship ship) {
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component == null) continue;

                ComponentType type = component.getComponentType();

                if (type == ComponentType.LivingUnit || type == ComponentType.MainUnitRed || type == ComponentType.MainUnitBlue || type == ComponentType.MainUnitGreen || type == ComponentType.MainUnitYellow) {
                    LivingUnit unit = (LivingUnit) component;
                    if (unit.getNum_crewmates() > 0) {
                        switch (unit.getCrewmate_type()) {
                            case Astronaut -> addOverlay(i, j, "Astronaut", unit.getNum_crewmates());
                            case PinkAlien -> addOverlay(i, j, "PinkAlien", unit.getNum_crewmates());
                            case BrownAlien -> addOverlay(i, j, "BrownAlien", unit.getNum_crewmates());
                        }
                    }
                }

                if (type == ComponentType.Battery) {
                    Battery battery = (Battery) component;
                    if (battery.getStored() > 0) {
                        addOverlay(i, j, "Battery", battery.getStored());
                    }
                }

                if (type == ComponentType.RedStorage || type == ComponentType.BlueStorage) {
                    Storage storage = (Storage) component;
                    if (!storage.getCarried_cargos().isEmpty()) {
                        updateCargoOverlayAt(i, j, ship);
                    }
                }
            }
        }
    }

    private void updateCargoOverlayAt(int row, int col, Ship ship) {
        CardComponent component = ship.getComponent(row, col);

        if (component == null || (component.getComponentType() != ComponentType.RedStorage && component.getComponentType() != ComponentType.BlueStorage)) {
            return;
        }

        Storage storage = (Storage) component;

        removeOverlay(row, col, "RedCargo");
        removeOverlay(row, col, "BlueCargo");
        removeOverlay(row, col, "YellowCargo");
        removeOverlay(row, col, "GreenCargo");

        Map<Cargo, Integer> cargoCount = new HashMap<>();
        for (Cargo cargo : storage.getCarried_cargos()) {
            cargoCount.put(cargo, cargoCount.getOrDefault(cargo, 0) + 1);
        }

        for (Map.Entry<Cargo, Integer> entry : cargoCount.entrySet()) {
            Cargo cargoType = entry.getKey();
            int count = entry.getValue();

            switch (cargoType) {
                case Red -> addOverlay(row, col, "RedCargo", count);
                case Blue -> addOverlay(row, col, "BlueCargo", count);
                case Yellow -> addOverlay(row, col, "YellowCargo", count);
                case Green -> addOverlay(row, col, "GreenCargo", count);
            }
        }
    }


    private void addOverlay(int x, int y, String type, int count) {
        Platform.runLater(() -> {
            StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;

            Node overlay = createOverlayForType(type, count);
            if (overlay != null) {
                cell.getChildren().add(overlay);
            }
        });
    }

    private void removeOverlay(int x, int y, String type) {
        Platform.runLater(() -> {
            StackPane cell = getShipCellAt(x, y);
            if (cell == null) return;
            cell.getChildren().removeIf(child -> isOverlayOfType(child, type));
        });
    }

    private boolean isOverlayOfType(Node node, String type) {
        if (node == null) return false;
        String nodeId = node.getId();
        return nodeId != null && nodeId.equals("overlay-" + type);
    }

    private Node createOverlayForType(String type, int count) {
        String path;
        HBox container = new HBox(0);
        container.setAlignment(Pos.CENTER);
        container.setMouseTransparent(true);
        container.setId("overlay-" + type);

        switch(type){
            case "Astronaut":
                path = "/images/icons/astronautPawn.png";
                for (int i = 0; i < count; i++) {
                    ImageView img = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    img.setFitWidth(37.5);
                    img.setFitHeight(37.5);
                    img.setPreserveRatio(true);
                    container.getChildren().add(img);
                }
                return container;

            case "PinkAlien":
                path = "/images/icons/pinkAlien.png";
                ImageView pinkAlienImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                pinkAlienImg.setFitWidth(37.5);
                pinkAlienImg.setFitHeight(37.5);
                container.getChildren().add(pinkAlienImg);
                return container;

            case "BrownAlien":
                path = "/images/icons/brownAlien.png";
                ImageView brownAlienImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                brownAlienImg.setFitWidth(37.5);
                brownAlienImg.setFitHeight(37.5);
                container.getChildren().add(brownAlienImg);
                return container;

            case "Battery":
                path = "/images/icons/battery.png";
                for (int i = 0; i < count; i++) {
                    ImageView batteryImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
                    batteryImg.setFitWidth(27);
                    batteryImg.setFitHeight(27);
                    batteryImg.setPreserveRatio(true);
                    container.getChildren().add(batteryImg);
                }
                return container;

            case "RedCargo": path = "/images/icons/redCargo.png"; break;
            case "BlueCargo": path = "/images/icons/blueCargo.png"; break;
            case "YellowCargo": path = "/images/icons/yellowCargo.png"; break;
            case "GreenCargo": path = "/images/icons/greenCargo.png"; break;
            default: return null;
        }

        for (int i = 0; i < count; i++) {
            ImageView cargoImg = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
            cargoImg.setFitWidth(25);
            cargoImg.setFitHeight(25);
            cargoImg.setPreserveRatio(true);
            container.getChildren().add(cargoImg);
        }
        return container;
    }

    private StackPane getShipCellAt(int row, int col) {
        for (Node node : shipGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex != null && rowIndex != null && colIndex == col && rowIndex == row && node instanceof StackPane) {
                return (StackPane) node;
            }
        }
        return null;
    }

    public void showCloseButton() {
        Platform.runLater(() -> closeButton.setVisible(true));
    }

    public void showCloseButtonFlyght() {
        Platform.runLater(() -> closeButtonFlyght.setVisible(true));
    }

    public void closeStage(){
        buildcontroller.getPlayerStage().close();
    }

    public void closeStageFlyght(){
        flyghtcontroller.getPlayerStage().close();
    }
}