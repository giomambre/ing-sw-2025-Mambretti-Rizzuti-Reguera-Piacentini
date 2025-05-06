package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.TUI.TUI;
import javafx.util.Pair;

import java.util.List;

/**
 * Abstract base class representing the view of a client-side application.
 * It defines methods for displaying different screens and handling user interactions.
 * Existing view implementations include:
 *  * <ul>
 *  *     <li>{@link TUI}</li>
 *  *     <li>{@link GUI}</li>
 *  * </ul>
 */
public interface View {

    public void showMessage(String message);

    public String askNickname();

    public String chooseConnection();

    public String getInput();

    public void showGenericError(String error);

    public int askCreateOrJoin();

    public int askNumPlayers();

    public int showLobbies(List<Integer> lobbies);

    public Color askColor(List<Color> colors);

    Pair<Integer, Integer> askCoordsCrewmate(Ship ship);

    public void showPlayer(Player player);
    void showShip(String nickname);

    void printShip(CardComponent[][] ship);

    void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship);

    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship);

    int selectDeck();

    int crewmateAction(CardComponent component);

    int askFacedUpCard(List<CardComponent> cards);

    int askSecuredCard(List<CardComponent> cards);

    int showCard(CardComponent card);

    Pair<Integer,Integer> askCoords(Ship ship);

    Ship  removeInvalidsConnections(Ship ship,List<Pair<Integer,Integer>> connectors);
}
