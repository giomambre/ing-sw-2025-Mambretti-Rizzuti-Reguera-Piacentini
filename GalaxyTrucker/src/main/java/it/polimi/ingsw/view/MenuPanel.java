package it.polimi.ingsw.view;

import it.polimi.ingsw.view.GUI.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * This class provides the interface where the user can choose to create or join a lobby.
 * It contains two buttons:
 * - "Create a Lobby"
 * - "Join a Lobby"
 * When the user clicks a button, the choice is saved and a confirmation message is displayed.
 */

public class MenuPanel extends JPanel {
    /*private GUI gui;
    private int choice = -1;

    private final Object lock = new Object();

    public MenuPanel(GUI gui) {
        this.gui = gui;
        setLayout(new GridLayout(2, 1)); //I riga → I bottone (“Crea una Lobby”), II riga → II bottone (“Entra in una Lobby”)

        JButton createLobby = new JButton("Crea una Lobby");
        JButton joinLobby = new JButton("Entra in una Lobby");

        createLobby.addActionListener(e -> {
            choice = 1;
            synchronized (lock) {
                lock.notifyAll();
            }
            gui.showMessage("Hai scelto di creare una lobby!");
        });

        joinLobby.addActionListener(e -> {
            choice = 2;
            synchronized (lock) {
                lock.notifyAll();
            }
            gui.showMessage("Hai scelto di entrare in una lobby!");
        });

        add(createLobby);
        add(joinLobby);
    }

    /**
     * @return the user's choice (1 for create, 2 for join).
     */
    /*
    public int getChoice() {
        synchronized (lock) {
            while (choice == -1) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return choice;
    }*/
}