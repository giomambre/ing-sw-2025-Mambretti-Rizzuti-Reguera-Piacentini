package it.polimi.ingsw.network;



import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.*;

public class Server implements VirtualServerRmi {
    final List<VirtualView> clients = new ArrayList<>();

    public Server() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws RemoteException {
        final String serverName = "AdderServer";

        VirtualServerRmi server = new Server();

        Registry registry = LocateRegistry.createRegistry(1234);

        registry.rebind(serverName, server);

        System.out.println("Server RMI READY...");
    }



    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add(client);
        }
    }

    @Override
    public void sendMessage(String message) {
        synchronized (this.clients){
            for(VirtualView client: clients){
                try {
                    client.showMessage("è arrivato il Messaggio : " + message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void addPlayer(String nickname, VirtualView virtualView) {

    }
}
