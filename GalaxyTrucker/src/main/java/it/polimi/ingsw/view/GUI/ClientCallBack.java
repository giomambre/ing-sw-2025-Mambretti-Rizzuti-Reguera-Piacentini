package it.polimi.ingsw.view.GUI;

/**
 * Interface for callbacks used by the GUI to communicate user input back to the client logic.
 */
public interface ClientCallBack {

    /**
     * Sends the chosen nickname to the server.
     * @param nickname the nickname selected by the user
     */
    void sendNicknameToServer(String nickname);
}
