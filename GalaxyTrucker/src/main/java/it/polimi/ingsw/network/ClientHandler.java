package it.polimi.ingsw.network;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static java.lang.System.out;
import it.polimi.ingsw.network.Server;

//It manages multiple client connections via socket.
import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Set<String> connectedNames;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, Set<String> connectedNames) {
        this.socket = socket;
        this.connectedNames = connectedNames;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // Manda richiesta di nome al client
                sendMessage(new Message(MessageType.REQUEST_NAME, "Inserisci il tuo nome:"));

                // Riceve risposta
                Message response = (Message) in.readObject();
                if (response.getType() == MessageType.REQUEST_NAME) {
                    String name = response.getContent();

                    synchronized (connectedNames) {
                        if (!connectedNames.contains(name)) {
                            connectedNames.add(name);
                            sendMessage(new Message(MessageType.NAME_ACCEPTED, "Nome accettato!"));
                            break;
                        } else {
                            sendMessage(new Message(MessageType.NAME_REJECTED, "Nome già in uso, scegline un altro."));
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }
}


