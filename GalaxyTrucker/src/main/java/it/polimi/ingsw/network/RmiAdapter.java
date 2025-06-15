package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RmiAdapter implements NetworkAdapter {
    private RemoteServer server;
    private RemoteClient client;
    private final BlockingQueue<Message> rmiMessageQueue = new LinkedBlockingQueue<>();

    public RmiAdapter(String host, int port) {
        // Constructor can be empty or used for initial setup
    }

    @Override
    public void connect(String host, int port) throws IOException {
        try {
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(host, port);
            server = (RemoteServer) registry.lookup("RmiServer");

            client = new RemoteClientImpl(rmiMessageQueue);
            UUID clientId = server.registerClient(client);
            Client.setClientId(clientId);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            throw new IOException("Failed to connect to RMI server: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(Message msg) throws IOException, RemoteException {
        server.sendMessage(msg);
    }

    @Override
    public Message readMessage() throws IOException, ClassNotFoundException, RemoteException {
        try {
            return rmiMessageQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread interrupted while waiting for a message", e);
        }
    }
}
