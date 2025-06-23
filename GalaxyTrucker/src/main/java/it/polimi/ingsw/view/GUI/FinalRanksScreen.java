package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import javafx.fxml.FXML;
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
    }


private void addStyledLabelToGrid(GridPane grid, String text, int col, int row, boolean isHeader) {
    Label label = new Label(text);
    label.setTextFill(Color.WHITE); // Colore predefinito per il testo

    if (isHeader) {
        label.setFont(Font.font("System Bold", 18)); // Font per le intestazioni
        label.setTextFill(Color.LIGHTGRAY); // Colore specifico per le intestazioni
    } else {
        label.setFont(Font.font("System", 16)); // Font per i dati dei giocatori
    }
    grid.add(label, col, row);
}

}




