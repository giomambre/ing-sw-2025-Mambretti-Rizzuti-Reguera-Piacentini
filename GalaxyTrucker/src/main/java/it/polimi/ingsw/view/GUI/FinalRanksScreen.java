package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class FinalRanksScreen {

    private VBox rankingContainer;
    private Label titleLabel;
    private Button backButton;
    private BorderPane mainLayout;

    public FinalRanksScreen() {
        setupUI();
        mainLayout = createMainLayout();
    }

    public BorderPane getLayout() {
        return mainLayout;
    }

    private void setupUI() {
        // Titolo principale
        titleLabel = new Label("CLASSIFICA FINALE");
        titleLabel.getStyleClass().add("title-label");

        // Container per la classifica
        rankingContainer = new VBox(10);
        rankingContainer.setPadding(new Insets(20));
        rankingContainer.setAlignment(Pos.TOP_CENTER);

        // Pulsante per tornare indietro
        backButton = new Button("Torna al Menu");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> handleBackButton());
    }

    private BorderPane createMainLayout() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // Header con titolo
        VBox header = new VBox(titleLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 10, 20));

        // ScrollPane per la classifica
        ScrollPane scrollPane = new ScrollPane(rankingContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        // Footer con pulsante
        VBox footer = new VBox(backButton);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 20, 20, 20));

        mainLayout.setTop(header);
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(footer);

        return mainLayout;
    }

    public void displayFinalRanks(List<Player> finalRanks) {
        rankingContainer.getChildren().clear();

        if (finalRanks.isEmpty()) {
            Label noPlayersLabel = new Label("Nessun giocatore da mostrare.");
            noPlayersLabel.getStyleClass().add("no-players-label");
            rankingContainer.getChildren().add(noPlayersLabel);
        } else {
            for (int i = 0; i < finalRanks.size(); i++) {
                Player player = finalRanks.get(i);
                Label playerRank = createPlayerRankLabel(i + 1, player);
                rankingContainer.getChildren().add(playerRank);
            }
        }
    }

    private Label createPlayerRankLabel(int position, Player player) {
        String rankText = String.format("%d. %s - Crediti: %d",
                position, player.getNickname(), player.getCredits());

        Label rankLabel = new Label(rankText);
        rankLabel.getStyleClass().add("rank-label");

        // Stile speciale per i primi 3 posti
        if (position == 1) {
            rankLabel.getStyleClass().add("first-place");
        } else if (position == 2) {
            rankLabel.getStyleClass().add("second-place");
        } else if (position == 3) {
            rankLabel.getStyleClass().add("third-place");
        }

        return rankLabel;
    }

    private void handleBackButton() {
        // Implementa la logica per tornare al menu principale

    }
}
