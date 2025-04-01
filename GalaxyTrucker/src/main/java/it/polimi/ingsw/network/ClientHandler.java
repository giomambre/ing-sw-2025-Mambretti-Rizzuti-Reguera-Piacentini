package it.polimi.ingsw.network;

package it.polimi.ingsw.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private VirtualView virtualView;
    private OutputStream output;
    private InputStream input;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            output = socket.getOutputStream();
            input = socket.getInputStream();

            // Crea un'istanza della TUI per questo client
            virtualView = new TUI(socket);

            // Invia il messaggio di benvenuto
            virtualView.showMessage("Benvenuto! Attendere il messaggio del server...");

            // Riceve il nickname dal client
            String nickname = new String(input.readAllBytes()).trim();
            // Gestisce l'inserimento del nickname e i controlli del server
            Server.getInstance().addPlayer(nickname, virtualView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
