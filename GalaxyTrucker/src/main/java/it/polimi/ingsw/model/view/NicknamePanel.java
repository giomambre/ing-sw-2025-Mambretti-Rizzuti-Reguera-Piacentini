package it.polimi.ingsw.model.view;

import javax.swing.*;
import java.awt.*;

/**
 * This class allows the user to enter their nickname.
 * It contains:
 * - a text field (JTextField) to input the nickname.
 * - a "Confirm" button to save the entered nickname.
 */
public class NicknamePanel extends JPanel {
    private GUI gui;
    private JTextField nicknameField;
    private JButton confirmButton;
    private String nickname;

    private final Object lock = new Object(); // oggetto per sincronizzare

    public NicknamePanel(GUI gui) {
        this.gui = gui;
        setLayout(new BorderLayout());

        nicknameField = new JTextField();
        confirmButton = new JButton("Conferma");
        confirmButton.addActionListener(e -> {
            if (!nicknameField.getText().isEmpty()) {
                nickname = nicknameField.getText();
                synchronized (lock) {
                    lock.notifyAll();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Inserisci un nickname valido.");
            }
        });

        add(new JLabel("Inserisci il tuo nickname:"), BorderLayout.NORTH);
        add(nicknameField, BorderLayout.CENTER);
        add(confirmButton, BorderLayout.SOUTH);
    }

    /**
     * @return  a valid nickname
     */
    public String getNickname() {
        synchronized (lock) {
            while (nickname == null || nickname.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return nickname;
    }
}