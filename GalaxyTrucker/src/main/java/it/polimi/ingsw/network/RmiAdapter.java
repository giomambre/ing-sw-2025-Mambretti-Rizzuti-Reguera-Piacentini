package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.net.NetworkInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiAdapter implements NetworkAdapter {
    private final String host;
    private final int port;

    public RmiAdapter(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect(String host, int port) throws IOException, NotBoundException {

    }

    @Override
    public void sendMessage(Message msg) throws IOException, RemoteException {

    }

    @Override
    public Message readMessage() throws IOException, ClassNotFoundException, RemoteException {
        return null;
    }
}
