package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.model.enumerates.MeteorType;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.TUI.TUI;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    void printShip(CardComponent[][] ship);

    void earlyEndFlightResume(Player player);

    void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship);

    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship);

    int nextMeteor();

    void showHittedCard(CardComponent card, Direction direction);

    void printCardAdventure(CardAdventure adventure);

    int selectDeck();

    int crewmateAction(Pair<Integer,Integer> coords);

    int askFacedUpCard(List<CardComponent> cards);

     void printCargo(List<Cargo> cargos);


        Boolean acceptAdventure();

    int askSecuredCard(List<CardComponent> cards);

    int showCard(CardComponent card);

    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planet_taken) ;

    void showBoard( Map<Integer, Player> positions, Map<Integer, Player> laps);

    public Pair<Integer,Integer> chooseAstronautLosses(Ship ship);

    public Map<CardComponent, Map<Cargo, Integer>> manageCargo(Ship ship);

    public Pair<Pair<Integer,Integer>, Integer> addCargo(Ship ship, Cargo cargo);

    public Pair<Integer,Integer> useBattery(Ship ship);

    double declareCannonPower(Ship ship);

    double declareEnginePower(Ship ship);

    public Map<CardComponent, Boolean> batteryUsage(Ship ship);

    public boolean useShield(Ship ship);

    Pair<Integer,Integer> askCoords(Ship ship);

     void printMeteors(List<Pair<MeteorType, Direction>> meteors) ;

     void printMeteor(Pair<MeteorType, Direction> meteor,int coord);


        Ship  removeInvalidsConnections(Ship ship,List<Pair<Integer,Integer>> connectors);

    Pair<Integer,Integer> askEngine(Pair<Integer,Integer>  cannon);


    int askCargo(List<Cargo> cargos);

}
