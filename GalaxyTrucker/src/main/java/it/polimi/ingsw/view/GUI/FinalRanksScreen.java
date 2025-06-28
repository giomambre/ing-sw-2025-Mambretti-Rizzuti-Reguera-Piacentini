package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FinalRanksScreen {

    public GUI gui;
   @FXML
   private GridPane rankingGridPane;


    private BorderPane mainLayout;

    public void setGUI(GUI gui) {
        this.gui = gui;
    }



    public void displayFinalRanks(List<Player> players) {

        rankingGridPane.getChildren().clear();

        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getCredits).reversed()) // Ordina per crediti decrescenti
                .collect(Collectors.toList());


        addStyledLabelToGrid(rankingGridPane, "Posizione", 0, 0, true);
        addStyledLabelToGrid(rankingGridPane, "Giocatore", 1, 0, true);
        addStyledLabelToGrid(rankingGridPane, "Crediti", 2, 0, true);

        int row = 1;
        for (Player player : sortedPlayers) {

            addStyledLabelToGrid(rankingGridPane, String.valueOf(row), 0, row, false);

            addStyledLabelToGrid(rankingGridPane, player.getNickname(), 1, row, false);

            addStyledLabelToGrid(rankingGridPane, String.valueOf(player.getCredits()), 2, row, false);

            row++;
        }


        while (rankingGridPane.getRowConstraints().size() < row) {
            javafx.scene.layout.RowConstraints rc = new javafx.scene.layout.RowConstraints();
            rc.setMinHeight(10.0);
            rc.setPrefHeight(30.0);
            rc.setVgrow(javafx.scene.layout.Priority.SOMETIMES);
            rankingGridPane.getRowConstraints().add(rc);
        }

        rankingGridPane.applyCss();
        rankingGridPane.layout();

    }


    private void addStyledLabelToGrid(GridPane grid, String text, int col, int row, boolean isHeader) {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);

        String textColor;
        FontWeight fontWeight = (row <= 3) ? FontWeight.BOLD : FontWeight.NORMAL;

        if (isHeader) {
            textColor = "lightgray";
            label.setText(text);
        } else {
            // Colore testo in base alla posizione
            switch (row) {
                case 1 -> textColor = "#FFD700"; // Oro
                case 2 -> textColor = "#C0C0C0"; // Argento
                case 3 -> textColor = "#CD7F32"; // Bronzo
                default -> textColor = "#fef6d5";
            }

            // Emoji solo per colonna posizione
            if (col == 0) {
                switch (text) {
                    case "1" -> label.setText("🥇 1");
                    case "2" -> label.setText("🥈 2");
                    case "3" -> label.setText("🥉 3");
                    default -> label.setText(text);
                }
            } else {
                label.setText(text);
            }
        }


        // Imposta sempre Verdana 20, con fontWeight deciso sopra
        label.setFont(Font.font("Verdana", fontWeight, 30));
        label.setStyle("-fx-text-fill: " + textColor + ";");

        grid.add(label, col, row);
        GridPane.setHalignment(label, HPos.CENTER);
    }




}




