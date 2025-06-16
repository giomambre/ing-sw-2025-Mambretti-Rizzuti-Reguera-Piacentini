package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.StandardMessageClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.UUID;

public class SocketConnectionHandler implements ConnectionHandler {
    private final Socket clientSocket;
    private final UUID clientId;
    private final Queue<Message> messageQueue;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickname;

    public SocketConnectionHandler(Socket clientSocket, Queue<Message> messageQueue) {
        this.clientSocket = clientSocket;
        this.messageQueue = messageQueue;
        this.clientId = UUID.randomUUID();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            sendMessage(new StandardMessageClient(MessageType.ASSIGN_UUID, "", clientId));
            sendMessage(new Message(MessageType.REQUEST_NAME, ""));

            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) in.readObject();
                synchronized (messageQueue) {
                    messageQueue.add(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Connessione persa con " + clientId);
        } finally {
            try {
                close();
            } catch (IOException ex) {

            }
        }
    }

    @Override
    public void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
    }

    @Override
    public UUID getClientId() {
        return clientId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
