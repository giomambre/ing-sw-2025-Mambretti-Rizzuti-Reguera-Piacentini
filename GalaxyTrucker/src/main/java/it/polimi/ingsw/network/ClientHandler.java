package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.StandardMessageClient;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UUID clientId;  // Identificativo univoco
    private final Set<String> connectedNames;
    private final Queue<Message> messageQueue;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickname;

    public ClientHandler(Socket clientSocket, Set<String> connectedNames, Queue<Message> messageQueue) {
        this.clientSocket = clientSocket;
        this.connectedNames = connectedNames;
        this.messageQueue = messageQueue;
        this.clientId = UUID.randomUUID();  // Genera un UUID per il client
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());



            sendMessage(new StandardMessageClient(MessageType.ASSIGN_UUID, "",clientId));
            sendMessage(new Message(MessageType.REQUEST_NAME, ""));

            while (true) {
                Message message = (Message) in.readObject();

                synchronized (messageQueue) {
                    messageQueue.add(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Connessione persa con " +  clientId);
        }
    }

    public void sendMessage(Message msg) throws IOException {
        try {
            out.writeObject(msg);
            out.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getClientId() {
        return clientId;
    }
}
