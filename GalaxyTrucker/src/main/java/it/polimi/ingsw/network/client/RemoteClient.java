package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.network.client.VirtualServerRmi;
import it.polimi.ingsw.network.server.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Classe che rappresenta il client RMI.
 */
public class RemoteClient extends UnicastRemoteObject implements VirtualViewRmi {
    private final VirtualServerRmi server;
    private  boolean is_lobby_creator = false;
    private final CountDownLatch lobbyLatch = new CountDownLatch(1); // Sincronizza l'attesa
    private Scanner scanner = new Scanner(System.in);
    public RemoteClient(VirtualServerRmi server) throws RemoteException {
        super();
        this.server = server;

        server.connect(this);  // Registra il client sul server
    }

    public boolean getIs_lobby_creator() {
        return is_lobby_creator;
    }

    public void showCreateLobby() throws RemoteException {
        is_lobby_creator = true;
        System.out.println("Sei il primo giocatore! Crea una nuova lobby.");
        lobbyLatch.countDown(); // Sblocca il client
        askMaxPlayers();
    }
    public void showJoinLobby() throws RemoteException {
        is_lobby_creator = false;
        System.out.println("Ti sei unito a una lobby esistente!");
        lobbyLatch.countDown(); // Sblocca il client
    }

    private void askMaxPlayers() {
        System.out.print("Inserisci il numero massimo di giocatori (2-4): ");
        int maxPlayers = scanner.nextInt();
        scanner.nextLine(); // Consuma il carattere di nuova linea rimasto
        try {
            server.setMaxPlayers(maxPlayers);
            System.out.println("Numero massimo di giocatori impostato a " + maxPlayers);
        } catch (RemoteException e) {
            System.err.println("Errore nell'invio del numero massimo di giocatori: " + e.getMessage());
        }
    }

    @Override
    public void showRemovedFromLobby() throws RemoteException {
        System.out.println("Sei stato rimosso dalla Lobby da server");

    }

    @Override
    public void showSecureCard(CardComponent card) throws RemoteException {

    }

    public void showMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }

    /**
     * Metodo richiamato dal server quando un nuovo giocatore si unisce alla partita.
     */
    @Override
    public void showNewPlayer(Player player) throws RemoteException {
        System.out.println("Nuovo giocatore aggiunto: " + player.toString());
    }

    @Override
    public void showCard(CardComponent card) throws RemoteException {
        System.out.println("Carta ricevuta: " + card);
    }

    @Override
    public void showInsertedCard(CardComponent card) throws RemoteException {
        System.out.println("Carta posizionata: " + card);
    }

    @Override
    public void showRemovedCard(CardComponent card) throws RemoteException {
        System.out.println("Carta rimossa: " + card);
    }

    @Override
    public void showRotatedCard(CardComponent card) throws RemoteException {
        System.out.println("Carta ruotata: " + card);
    }

    /**
     * Metodo per inviare il nickname e il colore al server.
     */
    public void registerPlayer(String nickname,String color) throws RemoteException {
        server.insertPlayer(nickname, color);
    }

    /**
     * Metodo per richiedere una carta casuale al server.
     */
    public void requestRandomCard() throws RemoteException {
        CardComponent card = server.pickRandomCard();
        System.out.println("Carta pescata: " + card);
    }
}
