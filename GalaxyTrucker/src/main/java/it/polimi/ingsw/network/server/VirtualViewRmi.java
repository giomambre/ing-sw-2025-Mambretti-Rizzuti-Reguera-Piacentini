package it.polimi.ingsw.network.server;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.network.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Questa interfaccia specializza l'interfaccia VirtualView per la tecnologia RMI
 */
public interface VirtualViewRmi extends Remote, VirtualView {

    @Override
    void showSecureCard(CardComponent card) throws RemoteException;

    @Override
    void showNewPlayer(Player player)throws RemoteException;

    @Override
    void showCard(CardComponent card) throws RemoteException;

    @Override
    void showInsertedCard(CardComponent card) throws RemoteException;


    @Override
    void showRemovedCard(CardComponent card) throws RemoteException;

    @Override
    void showRotatedCard(CardComponent card) throws RemoteException;

   void showMessage(String msg) throws RemoteException;
}
