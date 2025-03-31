package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.network.server.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RmiClient {





    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            final String serverName = "GameServer";
            // Connessione al server RMI
            System.out.println("Tentativo di connessione al server...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            VirtualServerRmi server = (VirtualServerRmi) registry.lookup("GameServer");




            String nickname = null;
            String color = null;

            while (nickname == null || color == null) {
                System.out.print("Inserisci il tuo nickname: ");
                nickname = scanner.nextLine();

                System.out.print("Inserisci il tuo colore (Red, Green, Blue, Yellow): ");
                color = scanner.nextLine();

                try {
                    // Chiamata al server per inserire il giocatore
                    server.insertPlayer(nickname, color);
                    System.out.println("Giocatore inserito correttamente!");
                } catch (RemoteException e) {
                    // Se c'è un errore (nickname già in uso o colore non valido), chiedi di nuovo
                    System.out.println(e.getCause().getMessage());
                    nickname = null; // Forza un altro tentativo
                    color = null; // Forza un altro tentativo
                }
            }

            color = scanner.nextLine();







        } catch (Exception e) {
            System.err.println("Errore nel client: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
