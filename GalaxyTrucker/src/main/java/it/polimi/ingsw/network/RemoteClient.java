package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    void receiveMessage(Message message) throws RemoteException;
}
