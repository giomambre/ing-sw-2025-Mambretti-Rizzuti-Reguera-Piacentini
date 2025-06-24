package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardDisplayController {
    @FXML
    private Label deckNameLabel;
    @FXML
    private ImageView cardImageView;
    @FXML
    private Label cardDescriptionLabel;

    /**
     * Sets the name of the deck to be displayed in the label.
     * @param deckName The name of the deck.
     */
    public void setDeckName(String deckName) {
        deckNameLabel.setText("First Card of " + deckName);
    }

    /**
     * Imposta l'immagine e la descrizione della carta da visualizzare.
     * Il percorso dell'immagine deve essere relativo al classpath (es. "/images/cards/my_card.png").
     * @param cardImagePath Il percorso relativo dell'immagine della carta.
     */
    public void setCard(String cardImagePath) {
        try {
            // Carica l'immagine dal percorso specificato
            // Utilizziamo getClass().getResourceAsStream() per un caricamento robusto da risorse interne
            Image cardImage = new Image(getClass().getResourceAsStream(cardImagePath));
            cardImageView.setImage(cardImage);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine della carta: " + cardImagePath);
            e.printStackTrace();
            // Puoi impostare un'immagine di placeholder per errori, ad esempio:
            // cardImageView.setImage(new Image(getClass().getResourceAsStream("/it/polimi/ingsw/view/GUI/images/cards/error_card.png")));
        }
    }
}

