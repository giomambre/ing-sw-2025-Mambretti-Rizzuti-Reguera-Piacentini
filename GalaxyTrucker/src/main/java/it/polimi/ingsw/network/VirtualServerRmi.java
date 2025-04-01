package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface VirtualServerRmi extends Remote , VirtualServer {

    void connect(VirtualView client) throws RemoteException;
    @Override
    void addPlayer(String nickname, VirtualView virtualView);
}
