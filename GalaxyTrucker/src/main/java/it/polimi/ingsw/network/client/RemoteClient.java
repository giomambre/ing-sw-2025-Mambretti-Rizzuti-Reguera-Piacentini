package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.network.client.VirtualServerRmi;
import it.polimi.ingsw.network.server.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Classe che rappresenta il client RMI.
 */
public class RemoteClient extends UnicastRemoteObject implements VirtualViewRmi {
    private final VirtualServerRmi server;


    public RemoteClient(VirtualServerRmi server, String nickname) throws RemoteException {
        super();
        this.server = server;

        server.connect(this);  // Registra il client sul server
    }

    public void showMessage(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public void showSecureCard(CardComponent card) throws RemoteException {

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
