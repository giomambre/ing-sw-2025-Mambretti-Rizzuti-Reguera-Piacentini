package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.BlockingQueue;

public class RemoteClientImpl extends UnicastRemoteObject implements RemoteClient {
    private final BlockingQueue<Message> rmiMessageQueue;

    public RemoteClientImpl(BlockingQueue<Message> rmiMessageQueue) throws RemoteException {
        super();
        this.rmiMessageQueue = rmiMessageQueue;
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException {
        try {
            // Add the received message to the dedicated RMI queue.
            rmiMessageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrupted while queuing message", e);
        }
    }
}
