package it.polimi.ingsw.view.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import it.polimi.ingsw.model.components.CardComponent;


import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the window where the player selects
 * a group of connected components from their ship grid.
 */
public class SelectedPieceController {

    private Stage stage;
    private List<List<Pair<Integer, Integer>>> pieces;
    private CardComponent[][] ship;

    private CompletableFuture<Integer> selectedPieceFuture = new CompletableFuture<>();

    /**
     * Sets the data used to display selectable pieces.
     * @param pieces a list of coordinate groups, each representing a piece
     * @param ship   the full ship grid containing component references
     */
    public void setData(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        this.pieces = pieces;
        this.ship = ship;
    }

    /**
     * Returns a CompletableFuture that will be completed with the index
     * of the piece selected by the user.
     * @return a CompletableFuture with the selected piece index
     */
    public CompletableFuture<Integer> getSelectedPieceFuture() {
        return selectedPieceFuture;
    }

    /**
     * Displays the selection window.
     * @param stage
     */
    public void show(Stage stage) {
        this.stage = stage;

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        for (int i = 0; i < pieces.size(); i++) {
            List<Pair<Integer, Integer>> piece = pieces.get(i);

            VBox pieceBox = new VBox(5);
            pieceBox.setStyle("-fx-border-color: black; -fx-padding: 5;");

            GridPane grid = createPieceGrid(piece);
            Button selectButton = new Button("Scegli");
            int finalI = i;
            selectButton.setOnAction(e -> handlePieceSelection(finalI));

            pieceBox.getChildren().addAll(grid, selectButton);
            root.getChildren().add(pieceBox);
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Seleziona un pezzo");
        stage.show();
    }

    /**
     * Creates a 5x7 grid representation of a piece's coordinates.
     * @param pieceCoords the coordinates
     * @return a GridPane containing a visual representation of the piece
     */
    private GridPane createPieceGrid(List<Pair<Integer, Integer>> pieceCoords) {
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);


                int finalRow = row;
                int finalCol = col;

                boolean isPartOfPiece = pieceCoords.stream()
                        .anyMatch(p -> p.getKey() == finalRow && p.getValue() == finalCol);

                if (isPartOfPiece) {
                    CardComponent component = ship[row][col];


                    if (component != null && component.getImagePath() != null) {

                        String imagePath = component.getImagePath();
                        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                        ImageView imageView = new ImageView(image);
                        imageView.setFitWidth(48);
                        imageView.setFitHeight(48);
                        imageView.setPreserveRatio(true);
                        cell.getChildren().add(imageView);

                    } else {
                        cell.setStyle("-fx-background-color: red;");
                    }
                } else {
                    cell.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
                }

                grid.add(cell, col, row);
            }
        }

        return grid;
    }


    public CompletableFuture<Integer> getSelectedPiece() {
        return selectedPieceFuture;
    }

    /**
     * Handles user selection of a piece by index.
     * Completes the future and closes the stage.
     * @param index the index of the selected piece
     */
    private void handlePieceSelection(int index) {
        if (!selectedPieceFuture.isDone()) {
            selectedPieceFuture.complete(index);
            if (stage != null) {
                stage.close();
            }
        }
    }
}
