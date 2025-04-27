package it.polimi.ingsw.model.view;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.view.View;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GUI extends JFrame implements View {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private MenuPanel menuPanel;
    private NicknamePanel nicknamePanel;
    private ShipPanel shipPanel;
    private CardSelectionPanel cardSelectionPanel;
    private AdventurePanel adventurePanel;

    public GUI() {
        super("Galaxy Trucker - GUI Version");
        setDefaultCloseOperation(EXIT_ON_CLOSE); //quando l’utente chiude la finestra chiude anche tutto il programma
        setSize(800, 600);
        setLocationRelativeTo(null); //centra la finestra

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this);
        nicknamePanel = new NicknamePanel(this);
        shipPanel = new ShipPanel(this);
        cardSelectionPanel = new CardSelectionPanel(this);
        adventurePanel = new AdventurePanel(this);

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(nicknamePanel, "Nickname");
        mainPanel.add(shipPanel, "Ship");
        mainPanel.add(cardSelectionPanel, "CardSelection");
        mainPanel.add(adventurePanel, "Adventure");

        add(mainPanel);

        setVisible(true);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public String askNickname() {
        cardLayout.show(mainPanel, "Nickname");
        return nicknamePanel.getNickname();
    }

    @Override
    public String chooseConnection() {
        return "";
    }

    @Override
    public String getInput() {
        return "";
    }

    @Override
    public void showGenericError(String error) {

    }

    @Override
    public int askCreateOrJoin() {
        cardLayout.show(mainPanel, "Menu");
        return menuPanel.getChoice();
    }

    @Override
    public int askNumPlayers() {
        return 0;
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        return 0;
    }

    @Override
    public Color askColor(List<Color> colors) {
        return null;
    }

    @Override
    public Pair<Integer, Integer> askCoordsCrewmate(Ship ship) {
        return null;
    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public void showShip(String nickname) {

    }

    @Override
    public void printShip(CardComponent[][] ship) {

    }

    @Override
    public void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {

    }

    @Override
    public int selectDeck() {
        return 0;
    }

    @Override
    public int crewmateAction() {
        return 0;
    }

    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int askSecuredCard(List<CardComponent> cards) {
        return 0;
    }

    @Override
    public int showCard(CardComponent card) {
        return 0;
    }

    @Override
    public Pair<Integer, Integer> askCoords(Ship ship) {
        return null;
    }
}