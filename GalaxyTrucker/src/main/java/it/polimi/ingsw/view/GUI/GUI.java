package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.view.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import javafx.util.Pair;
import java.util.List;


public class GUI implements View {

    GuiApplication guiApp;
    public GUI() {
        guiApp = new GuiApplication();
    };

    @Override
    public void showMessage(String message) {

    }

    @Override
    public String askNickname() {
        guiApp.start(new Stage());
        return "ciao";
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
        return 0;
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
    public int crewmateAction(List<CrewmateType> crewmateType) {
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

    @Override
    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        return 0;
    }

    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
return null;
    }


    /*private CardLayout cardLayout; //layout a pagine
    private JPanel mainPanel; //contenitore
    private MenuPanel menuPanel;
    private NicknamePanel nicknamePanel;
    private ShipPanel shipPanel;
    private CardSelectionPanel cardSelectionPanel;
    private AdventurePanel adventurePanel;
    private ConnectionPanel connectionPanel;

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
        //mainPanel.add(connectionPanel, "Connection");

        add(mainPanel);

        setVisible(true);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Messaggio", JOptionPane.INFORMATION_MESSAGE);
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
        JOptionPane.showMessageDialog(this, error, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public int askCreateOrJoin() {
        cardLayout.show(mainPanel, "Menu");
        return menuPanel.getChoice();
    }

    @Override
    public int askNumPlayers() {
        JComboBox<Integer> comboBox = new JComboBox<>(new Integer[]{2, 3, 4});

        int result = JOptionPane.showConfirmDialog(
                this,
                comboBox,
                "Seleziona il numero di giocatori",
                JOptionPane.OK_CANCEL_OPTION, //pulsante ok e pulsante cancel
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (Integer) comboBox.getSelectedItem();
        } else {
            return -1;
        }
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        return 0;
    }

    @Override
    public Color askColor(java.util.List<Color> colors) {
        // Crea un array di stringhe con i nomi dei colori
        String[] colorNames = colors.stream() //JAVA FUNZIONALE, se vogliamo cambiarlo cambiamo
                .map(Color::name)
                .map(String::toLowerCase)
                .toArray(String[]::new);

        JComboBox<String> comboBox = new JComboBox<>(colorNames);

        int result = JOptionPane.showConfirmDialog(
                this,
                comboBox,
                "Scegli il tuo colore",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedName = (String) comboBox.getSelectedItem();
            for (Color color : colors) {
                if (color.name().equalsIgnoreCase(selectedName)) {
                    return color;
                }
            }
        }

        return null; // se annulla o chiude
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
    public void printShipPieces(java.util.List<java.util.List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship){
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
    public int askFacedUpCard(java.util.List<CardComponent> cards) {
        return 0;
    }
    public int askSecuredCard(java.util.List<CardComponent> cards){
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

    @Override
    public void removeInvalidsConnections(Ship ship, java.util.List<Pair<Integer, Integer>> connectors) {

    }*/
}