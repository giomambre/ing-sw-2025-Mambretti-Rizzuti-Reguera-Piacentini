package it.polimi.ingsw.model.view;

public interface View {

    public void showMessage(String message);
    public String askNickname();
    public String chooseConnection();
    public String getInput();
    public int askCreateOrJoin();
    public int askNumPlayers();

}
