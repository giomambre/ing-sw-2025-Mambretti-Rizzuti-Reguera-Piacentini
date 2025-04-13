package it.polimi.ingsw.model.view;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;

import java.util.List;

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

    void showShip(String nickname);

    void printShip(CardComponent[][] ship);
    int selectDeck();
    int askFacedUpCard(List<CardComponent> cards);
    int showCard(CardComponent card);
}
