package it.polimi.ingsw.model.view;

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

}
