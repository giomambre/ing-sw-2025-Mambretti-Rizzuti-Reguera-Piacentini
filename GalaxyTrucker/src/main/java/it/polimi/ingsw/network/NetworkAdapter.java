package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface NetworkAdapter {
        void connect(String host, int port) throws IOException, NotBoundException;
        void sendMessage(Message msg) throws IOException, RemoteException;
        Message readMessage() throws IOException, ClassNotFoundException, RemoteException;


}
