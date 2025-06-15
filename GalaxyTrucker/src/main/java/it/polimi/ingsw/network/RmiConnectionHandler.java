package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.UUID;

public class RmiConnectionHandler implements ConnectionHandler {
    private final UUID clientId;
    private final RemoteClient remoteClient;
    private String nickname;

    public RmiConnectionHandler(RemoteClient remoteClient, UUID clientId) {
        this.remoteClient = remoteClient;
        this.clientId = clientId;
    }

    @Override
    public void sendMessage(Message msg) throws IOException {
        try {
            remoteClient.receiveMessage(msg);
        } catch (RemoteException e) {
            throw new IOException("Failed to send message via RMI", e);
        }
    }

    @Override
    public void close() {
        // For RMI, closing is less explicit than with sockets.
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

    @Override
    public void run() {
        // RMI is callback-based, so this thread can be idle.
    }
}
