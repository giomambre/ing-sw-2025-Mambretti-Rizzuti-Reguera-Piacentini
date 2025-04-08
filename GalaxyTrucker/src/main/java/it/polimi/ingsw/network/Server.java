package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.network.messages.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static final int PORT = 12345;
    private static Set<String> connectedNames = new HashSet<>();
    private static Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, ClientHandler> clients = new HashMap<>();
    private GameManager manager =  new GameManager();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server in ascolto sulla porta " + PORT + "...");
            // Avvia un thread per processare i messaggi in coda

            new Thread(this::processMessages).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuova connessione: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, connectedNames, messageQueue);
                clients.put(handler.getClientId(), handler);

                new Thread(handler).start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessages() {
        while (true) {
            Message msg;
            synchronized (messageQueue) {
                msg = messageQueue.poll();
            }
            if (msg != null) {
                handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {

        switch (msg.getType()) {

            case SENDED_NAME:
                StandardMessageClient msgClient = (StandardMessageClient) msg;
                String requestedName = msg.getContent();
                System.out.println("nick name inviato : " + requestedName);
                if (connectedNames.contains(requestedName)) {
                    sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_REJECTED, "❌ Nome già in uso. Inserisci un altro nickname.", msgClient.getId_client()));
                } else {
                    connectedNames.add(requestedName);
                    ClientHandler handler = clients.get(msgClient.getId_client());
                    handler.setNickname(requestedName);
                    sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_ACCEPTED, "✅ Nickname accettato: " + requestedName, msgClient.getId_client()));

                }
                break;

            case CREATE_LOBBY:
                CreateLobbyMessage msg_cast = (CreateLobbyMessage) msg;

                try {
                    int lobby_id = manager.createLobby(getNickname(msg_cast.getId_client()),msg_cast.getLimit());
                    System.out.println("🔹 Il client " + getNickname(msg_cast.getId_client()) + " ha creato una lobby con " + msg_cast.getLimit() + " id : " + lobby_id);
                    sendToClient(msg_cast.getId_client(), new Message(MessageType.CREATE_LOBBY, "" +lobby_id));
                }catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case SEE_LOBBIES:
                msgClient = (StandardMessageClient) msg;

                sendToClient(msgClient.getId_client(),new AvaiableLobbiesMessage(MessageType.SEE_LOBBIES,"",manager.getAvailableLobbies()));
                System.out.println("il player vuole vedere le lobby :");
                break;

            case SELECT_LOBBY:
                SelectedLobbyMessage msg_sel = (SelectedLobbyMessage) msg;

                try {
                    System.out.println("🔹 Il client " + getNickname(msg_sel.getId_client()) + " è entrato nella lobby id: " + msg_sel.getLobbyId());
                    sendToClient(msg_sel.getId_client(), new Message(MessageType.SELECT_LOBBY, "" +msg_sel.getLobbyId()));
                }catch (Exception e) {
                    e.printStackTrace();
                }





            default:
                System.out.println("⚠ Messaggio sconosciuto ricevuto: " + msg.getType());
                break;


        }
    }

    public String getNickname(UUID id) {
        ClientHandler client = clients.get(id);
        return client.getNickname();
    }

    private void sendToClient(UUID id, Message msg) {
        ClientHandler client = clients.get(id);
        if (client != null) {
            try {
                client.sendMessage(msg);
            } catch (IOException e) {
                System.err.println("❌ Errore nell'invio del messaggio a " + id);
            }
        }
    }


    public static void main(String[] args) {
        new Server().start();
    }
}

