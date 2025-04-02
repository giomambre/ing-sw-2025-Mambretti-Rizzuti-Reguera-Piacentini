package it.polimi.ingsw.network;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final int PORT = 12345;
    private final Set<String> connectedNames = new HashSet<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🚀 Server in ascolto sulla porta " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Nuova connessione: " + clientSocket.getInetAddress());

                // Crea un nuovo thread per gestire il client
                new Thread(new ClientHandler(clientSocket, connectedNames)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
