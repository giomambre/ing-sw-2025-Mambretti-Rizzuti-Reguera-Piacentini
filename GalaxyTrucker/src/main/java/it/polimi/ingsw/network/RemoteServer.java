package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface RemoteServer extends Remote {
    UUID registerClient(RemoteClient client) throws RemoteException;
    void sendMessage(Message message) throws RemoteException;
}
