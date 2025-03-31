package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;

import java.rmi.RemoteException;

/**
 * Interfaccia che definisce i metodi utilizzati dal server per notificare i cambiamenti di stato ai client.
 */
public interface VirtualView {
    void showNewPlayer(Player player) throws RemoteException;

    void showSecureCard(CardComponent card) throws RemoteException;

    void showCard(CardComponent card) throws RemoteException;

    void showRotatedCard(CardComponent card) throws RemoteException;

    void showInsertedCard(CardComponent card) throws RemoteException;

    void showRemovedCard(CardComponent card) throws RemoteException;
}
