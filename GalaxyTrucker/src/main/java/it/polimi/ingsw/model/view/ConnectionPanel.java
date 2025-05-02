package it.polimi.ingsw.model.view;

import javax.swing.*;

/**
 * The very first screen of the client application, where you choose server and network mode.
 */

public class ConnectionPanel extends JPanel {
    private GUI gui;

    public ConnectionPanel(GUI gui) {
        this.gui = gui;
        //questo è il pannello mostrato quando l'utente deve connettersi al server
    }
}
