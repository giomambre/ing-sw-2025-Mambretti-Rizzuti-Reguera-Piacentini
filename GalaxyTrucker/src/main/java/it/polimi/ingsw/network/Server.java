import it.polimi.ingsw.network.VirtualView;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int SOCKET_PORT = 12345;
    private static final String RMI_SERVICE_NAME = "GameService";
    private static final String SERVER_IP = "127.0.0.1";  // Indirizzo IP del server

    private Map<UUID, VirtualView> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try {
            // Avvio del servizio RMI
            startRMIService();

            // Avvio del server Socket in un thread separato
            startSocketServer();

            System.out.println("Server in esecuzione. In attesa di connessioni...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRMIService() throws RemoteException {
        try {
            // Crea e avvia il server RMI
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(RMI_SERVICE_NAME, new GameRMIImpl(this));
            System.out.println("RMI service started on port 1099.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSocketServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    handleNewSocketClient(clientSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleNewSocketClient(Socket clientSocket) {
        try {
            UUID clientId = UUID.randomUUID();  // Genera un UUID per il nuovo client
            VirtualView clientView = new TUI(clientSocket);  // Usa la TUI per la gestione del client
            clients.put(clientId, clientView);

            // Invia il messaggio di benvenuto
            clientView.showMessage("Benvenuto! Scrivi il tuo nickname.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo chiamato per inviare messaggi a tutti i client
    public void sendMessageToAllClients(String message) {
        for (VirtualView clientView : clients.values()) {
            try {
                clientView.showMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
