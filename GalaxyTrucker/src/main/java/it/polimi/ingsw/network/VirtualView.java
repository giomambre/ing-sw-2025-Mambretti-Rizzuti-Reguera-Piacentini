package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;

import java.rmi.RemoteException;

/**
 * Interfaccia che definisce i metodi utilizzati dal server per notificare i cambiamenti di stato ai client.
 */
public interface VirtualView {


    void showMessage(String msg) throws RemoteException;



}
