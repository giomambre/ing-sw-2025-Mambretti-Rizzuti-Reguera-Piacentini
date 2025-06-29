package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.ComponentType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static it.polimi.ingsw.model.enumerates.ComponentType.BlueStorage;
import static it.polimi.ingsw.model.enumerates.ComponentType.RedStorage;

/**
 * Controller for the screen where the player selects cargo to add to a storage component.
 */
public class CargoSelector {
    private GUI gui;
    private int selectedCargoIndex = -1;
    private CountDownLatch latch;

    /**
     * Opens a window to allow the user to select one of the given cargo options.
     * @param cargos the list of available Cargo options to choose from
     * @return the index of the selected cargo in the list, or -1 if no selection was made
     */
    public int askCargo(List<Cargo> cargos) {
        selectedCargoIndex = -1;
        latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            showCargoSelector(cargos);
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }

        return selectedCargoIndex;
    }

    /**
     * Displays a popup that allows the user to choose a cargo from the provided list.
     * @param cargos the list of Cargo options to be displayed
     */
    private void showCargoSelector(List<Cargo> cargos) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Seleziona Cargo");
        popupStage.setResizable(false);

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #D8B7DD;");

        Label titleLabel = new Label("Scegli quale cargo vuoi Posizionare:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #043a7e;");

        GridPane cargoGrid = new GridPane();
        cargoGrid.setHgap(15);
        cargoGrid.setVgap(15);
        cargoGrid.setAlignment(Pos.CENTER);

        int columns = Math.min(3, cargos.size());
        for (int i = 0; i < cargos.size(); i++) {
            Cargo cargo = cargos.get(i);
            Button cargoButton = createCargoButton(cargo, i, popupStage);

            int row = i / columns;
            int col = i % columns;
            cargoGrid.add(cargoButton, col, row);
        }

        Button noCargoButton = new Button("Nessun Cargo");
        noCargoButton.setId("continue-button");
        noCargoButton.setOnAction(e -> {
            selectedCargoIndex = -1;
            Platform.runLater(() -> {
                popupStage.close();
                latch.countDown();
            });
        });

        mainContainer.getChildren().addAll(titleLabel, cargoGrid, noCargoButton);

        Scene scene = new Scene(mainContainer);
        popupStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/Meteor.css").toExternalForm());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        popupStage.setOnCloseRequest(e -> {
            selectedCargoIndex = -1;
            latch.countDown();
        });

        popupStage.show();

        Platform.runLater(() -> {
            double x = screenBounds.getMinX() + (screenBounds.getWidth() - popupStage.getWidth()) / 2;
            double y = screenBounds.getMinY() + 50;
            popupStage.setX(x);
            popupStage.setY(y);
        });
    }

    /**
     * Creates a stylized button representing a Cargo item.
     * @param cargo the Cargo represented by the button
     * @param index the index of the cargo in the list
     * @param parentStage the Stage to be closed upon selection
     * @return a Button instance customized for the cargo
     */
    private Button createCargoButton(Cargo cargo, int index, Stage parentStage) {
        Button button = new Button();
        button.setPrefSize(120, 80);
        button.setText("Cargo " + "\n" + cargo);

        String baseStyle = getCargoButtonStyle(cargo);
        button.setStyle(baseStyle);

        button.setOnAction(e -> {
            selectedCargoIndex = index;
            Platform.runLater(() -> {
                parentStage.close();
                latch.countDown();
            });

        });

        String hoverStyle = getCargoButtonHoverStyle(cargo);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    /**
     * This method returns the base CSS style for a cargo button.
     * @param cargo
     * @return a CSS style
     */
    private String getCargoButtonStyle(Cargo cargo) {
        String backgroundColor = getCargoColorHex(cargo);
        String textColor = getTextColor(cargo);

        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #043a7e; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;",
                backgroundColor, textColor
        );
    }

    /**
     * Returns the CSS style for a cargo button when hovered.
     * @param cargo the Cargo whose hover style should be generated
     * @return a CSS hover style
     */
    private String getCargoButtonHoverStyle(Cargo cargo) {
        String backgroundColor = getCargoColorHoverHex(cargo);
        String textColor = getTextColor(cargo);

        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #043a7e; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 8;",
                backgroundColor, textColor
        );
    }

    /**
     * This method returns the hex color code for the background color of a cargo button.
     * @param cargo the Cargo whose color is being queried
     * @return the hex color
     */
    private String getCargoColorHex(Cargo cargo) {
        switch (cargo) {
            case Red:
                return "#dc3545";
            case Blue:
                return "#0d6efd";
            case Green:
                return "#198754";
            case Yellow:
                return "#ffc107";
            default:
                return "#6c757d";
        }
    }

    /**
     * @param cargo the Cargo whose hover color is being queried
     * @return the hex color
     */
    private String getCargoColorHoverHex(Cargo cargo) {
        switch (cargo) {
            case Red:
                return "#bb2d3b";
            case Blue:
                return "#0b5ed7";
            case Green:
                return "#146c43";
            case Yellow:
                return "#ffca2c";
            default:
                return "#5a6268";
        }
    }

    /**
     * Returns the text color for a given Cargo type.
     * @param cargo the Cargo whose text color is being determined
     * @return a string representing a color
     */
    private String getTextColor(Cargo cargo) {
        switch (cargo) {
            case Red:
            case Blue:
            case Green:
                return "white";
            case Yellow:
                return "black";
            default:
                return "white";
        }
    }

    /**
     * This method opens a window to allow the user to select a planet to claim from a list of planets,
     * each containing a list of cargos.
     * @param planets a list of planets
     * @param planets_taken a set of indices representing already claimed planets
     * @return the index of the selected planet, or -1 if the user cancels or closes the window
     */
    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planets_taken) {
        int selectedPlanetIndex = -1;
        CountDownLatch planetLatch = new CountDownLatch(1);
        final int[] result = {-1};

        Platform.runLater(() -> {
            showPlanetSelector(planets, planets_taken, planetLatch, result);
        });

        try {
            planetLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        }

        return result[0];
    }

    /**
     * This method displays a popup with visual options for each planet.
     * @param planets a list of planets
     * @param planets_taken a set of indices for planets that are already taken
     * @param planetLatch latch used to unblock the waiting thread after user interaction
     * @param result an array of size 1 to store the selected planet index
     */
    private void showPlanetSelector(List<List<Cargo>> planets, Set<Integer> planets_taken,
                                    CountDownLatch planetLatch, int[] result) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Seleziona Pianeta");
        popupStage.setResizable(false);


        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle(
                "-fx-background-color: #D8B7DD;"
        );

        StackPane root = new StackPane();
        root.getChildren().add(mainContainer);

        Label titleLabel = new Label("Scegli quale pianeta vuoi conquistare:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #043a7e;");

        GridPane planetGrid = new GridPane();
        planetGrid.setHgap(15);
        planetGrid.setVgap(15);
        planetGrid.setAlignment(Pos.CENTER);

        int columns = Math.min(3, planets.size());
        for (int i = 0; i < planets.size(); i++) {
            List<Cargo> planetCargos = planets.get(i);
            boolean isPlanetTaken = planets_taken.contains(i);
            Node planetButton = createPlanetButton(i, planetCargos, isPlanetTaken, popupStage, planetLatch, result);

            int row = i / columns;
            int col = i % columns;
            planetGrid.add(planetButton, col, row);
        }

        Button cancelButton = new Button("Annulla");
        cancelButton.setId("continue-button");
        cancelButton.setOnAction(e -> {
            result[0] = -1;
            Platform.runLater(() -> {
                popupStage.close();
                planetLatch.countDown();
            });
        });

        mainContainer.getChildren().addAll(titleLabel, planetGrid, cancelButton);

        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/Meteor.css").toExternalForm());
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        popupStage.setOnCloseRequest(e -> {
            result[0] = -1;
            planetLatch.countDown();
        });

        popupStage.show();

        Platform.runLater(() -> {
            double x = screenBounds.getMinX() + (screenBounds.getWidth() - popupStage.getWidth()) / 2;
            double y = screenBounds.getMinY() + 50;
            popupStage.setX(x);
            popupStage.setY(y);
        });

    }

    /**
     * This method creates a graphical button (or overlayed button if unavailable) representing a planet.
     * @param planetIndex the index of the planet in the list
     * @param cargos the list of cargos present on the planet
     * @param isPlanetTaken true if the planet is already claimed
     * @param parentStage the popup stage to be closed upon selection
     * @param planetLatch latch used to resume execution after a choice is made
     * @param result array to store the selected planet index
     * @return a Node representing the planet as a selectable or disabled button
     */
    private Node createPlanetButton(int planetIndex, List<Cargo> cargos, boolean isPlanetTaken,
                                    Stage parentStage, CountDownLatch planetLatch, int[] result) {
        Button button = new Button();
        button.setPrefSize(150, 100);

        StringBuilder buttonText = new StringBuilder("Pianeta ").append(planetIndex).append("\n");
        Color textColor;

        if (isPlanetTaken) {
            buttonText.append("OCCUPATO");
            textColor = Color.web("#8B0000");
        } else {
            buttonText.append("DISPONIBILE\n");
            for (int i = 0; i < cargos.size(); i++) {
                buttonText.append(cargos.get(i));
                if (i < cargos.size() - 1) buttonText.append(", ");
            }
            textColor = Color.web("#006400");
        }


        String[] planetColors = {
                "#A8E6CF",
                "#F5F5DC",
                "#FFE4B5",
                "#AEC6CF",
                "#FFFACD",
                "#D0F0C0",
                "#F8C8DC",
                "#E0FFFF",
                "#E6C9A8",
                "#D8B7DD"

        };
        Color bgColor = isPlanetTaken ? Color.LIGHTGRAY : Color.web(planetColors[planetIndex % planetColors.length]);

        button.setBackground(new Background(new BackgroundFill(
                bgColor,
                new CornerRadii(50),
                Insets.EMPTY
        )));

        button.setText(buttonText.toString());
        button.setTextFill(textColor);
        button.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);
        button.setCursor(Cursor.HAND);

        BorderStroke borderStroke = new BorderStroke(
                Color.web("#043a7e"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(50),
                new BorderWidths(2)
        );
        button.setBorder(new Border(borderStroke));

        if (!isPlanetTaken) {
            button.setOnAction(e -> {
                result[0] = planetIndex;
                Platform.runLater(() -> {
                    parentStage.close();
                    planetLatch.countDown();
                });
            });

            button.setOnMouseEntered(e -> button.setEffect(new DropShadow(10, Color.WHITE)));
            button.setOnMouseExited(e -> button.setEffect(null));
            return button;
        }

        Rectangle overlay = new Rectangle(150, 100);
        overlay.setArcWidth(100);
        overlay.setArcHeight(100);
        overlay.setFill(Color.rgb(255, 0, 0, 0.4));

        button.setDisable(true);
        return new StackPane(button, overlay);
    }

    private String removedCargoMessage = "";
    private CountDownLatch removalLatch;

    /**
     * This method removes a cargo or battery from the ship, following this priority:
     * <ol>
     *     <li>Red cargo from RedStorage components</li>
     *     <li>Yellow, Green, or Blue cargo from any appropriate storage component</li>
     *     <li>Battery from Battery components</li>
     * </ol>
     * If no resources are available, a message is shown to the user.
     * @param ship the ship from which to remove the resource
     */
    public void removeCargo(Ship ship) {
        List<Pair<Integer,Integer>> storage_with_red = new ArrayList<>();
        List<Pair<Integer,Integer>> other_storage = new ArrayList<>();
        List<Pair<Integer,Integer>> batteries = new ArrayList<>();

        for(int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(i, j);

                if (card.getComponentType() == RedStorage && ((Storage)card).containsCargo(Cargo.Red)) {
                    storage_with_red.add(new Pair<>(i,j));
                    other_storage.add(new Pair<>(i, j));
                } else if ((card.getComponentType() == BlueStorage || card.getComponentType() == RedStorage) &&
                        (((Storage)card).getCargoCount() > 0)) {
                    other_storage.add(new Pair<>(i, j));
                } else if (card.getComponentType() == ComponentType.Battery && ((Battery)card).getStored() > 0) {
                    batteries.add(new Pair<>(i, j));
                }
            }
        }

        if (!storage_with_red.isEmpty()) {
            handleRedCargoRemoval(ship, storage_with_red);
        }

        else if (!other_storage.isEmpty()) {
            handleOtherCargoRemoval(ship, other_storage);
        }

        else if (!batteries.isEmpty()) {
            handleBatteryRemoval(ship, batteries);
        }

        else {
            showRemovalMessage("NON HAI PERSO NIENTE!!!", "Non avevi cargo o batterie disponibili!", "#28a745");
        }
    }

    /**
     * This method attempts to remove a red cargo from one of the provided RedStorage positions.
     * @param ship the ship being modified
     * @param storage_with_red list of coordinates where red cargo is stored
     */
    private void handleRedCargoRemoval(Ship ship, List<Pair<Integer,Integer>> storage_with_red) {
        for (Pair<Integer, Integer> pos : storage_with_red) {
            CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
            if (((Storage)card).removeCargo(Cargo.Red)) {
                String message = String.format("HAI PERSO UN CARGO ROSSO a RIGA: %d COLONNA: %d",
                        pos.getKey(), pos.getValue());
                showRemovalMessage("Cargo Perso!", message, "#dc3545");
                return;
            }
        }
    }

    /**
     * Attempts to remove a Yellow, Green, or Blue cargo from storage components.
     * Follows the priority order: Yellow > Green > Blue.
     * @param ship the ship being modified
     * @param other_storage list of coordinates of storage components
     */
    private void handleOtherCargoRemoval(Ship ship, List<Pair<Integer,Integer>> other_storage) {
        Cargo[] cargoOrder = {Cargo.Yellow, Cargo.Green, Cargo.Blue};
        String[] cargoColors = {"#ffc107", "#198754", "#0d6efd"};
        String[] cargoNames = {"GIALLO", "VERDE", "BLU"};

        for (int cargoIndex = 0; cargoIndex < cargoOrder.length; cargoIndex++) {
            Cargo cargoType = cargoOrder[cargoIndex];
            for (Pair<Integer, Integer> pos : other_storage) {
                CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
                if (((Storage)card).removeCargo(cargoType)) {
                    String message = String.format("HAI PERSO UN CARGO %s a RIGA: %d COLONNA: %d",
                            cargoNames[cargoIndex], pos.getKey(), pos.getValue());
                    showRemovalMessage("Cargo Perso!", message, cargoColors[cargoIndex]);
                    return;
                }
            }
        }
    }

    /**
     * This method removes a battery from one of the battery components if any are stored.
     * @param ship the ship being modified
     * @param batteries list of coordinates of battery components with stored charge
     */
    private void handleBatteryRemoval(Ship ship, List<Pair<Integer,Integer>> batteries) {
        for (Pair<Integer, Integer> pos : batteries) {
            CardComponent card = ship.getComponent(pos.getKey(), pos.getValue());
            if (((Battery)card).getStored() > 0) {
                ((Battery)card).removeBattery();
                String message = String.format("HAI PERSO UNA BATTERIA a RIGA: %d COLONNA: %d",
                        pos.getKey(), pos.getValue());
                showRemovalMessage("Batteria Persa!", message, "#ff8c00");
                return;
            }
        }
    }

    /**
     * It shows a popup dialog with the specified title, message, and color to notify the user
     * about a removal event (cargo or battery lost).
     * @param title   the title of the popup window
     * @param message the message
     * @param color   the hex color code for the title text
     */
    private void showRemovalMessage(String title, String message, String color) {
        removalLatch = new CountDownLatch(1);

        Platform.runLater(() -> {
            showRemovalDialog(title, message, color);
        });

        try {
            removalLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Displays the actual removal popup window to the user with styling and an OK button.
     * @param title   the title of the popup window
     * @param message the message to display
     * @param color   the hex color code for the title text
     */
    private void showRemovalDialog(String title, String message, String color) {

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle(title);
        popupStage.setResizable(false);

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #D8B7DD;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle(String.format(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: %s;", color));

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #043a7e;-fx-font-family: 'Verdana'; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        Button okButton = new Button("OK");
        okButton.setId("continue-button");
        okButton.setPrefWidth(120);

        okButton.setOnAction(e -> {
            popupStage.close();
            removalLatch.countDown();
        });

        mainContainer.getChildren().addAll(titleLabel, messageLabel, okButton);

        Scene scene = new Scene(mainContainer);
        popupStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/Meteor.css").toExternalForm());

        popupStage.setOnCloseRequest(e -> {
            removalLatch.countDown();
        });

        popupStage.show();
        okButton.requestFocus();
    }

}