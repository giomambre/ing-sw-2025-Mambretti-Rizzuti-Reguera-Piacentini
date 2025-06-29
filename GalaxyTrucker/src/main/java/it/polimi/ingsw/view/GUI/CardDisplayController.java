package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller responsible for displaying a component card in the GUI.
 */
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
     * Sets the image and description of the card to be displayed.
     * @param cardImagePath the relative path to the card image
     */
    public void setCard(String cardImagePath) {
        try {
            Image cardImage = new Image(getClass().getResourceAsStream(cardImagePath));
            cardImageView.setImage(cardImage);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine della carta: " + cardImagePath);
            e.printStackTrace();
            }
    }
}

