package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Questa classe rappresenta la logica del server implementata con tecnologia RMI.
 */
public class RmiServer {
    public static void main(String[] args) {
        try {
            GameController gameController = new GameController();
            RemoteGameServer remoteServer = new RemoteGameServer(gameController);

            // Crea il registry sulla porta 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("GameServer", remoteServer);

            System.out.println("Server RMI avviato e in ascolto...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

