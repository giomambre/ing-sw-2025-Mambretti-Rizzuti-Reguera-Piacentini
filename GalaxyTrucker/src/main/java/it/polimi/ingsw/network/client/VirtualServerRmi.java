package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.network.VirtualServer;
import it.polimi.ingsw.network.server.VirtualViewRmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerRmi extends Remote, VirtualServer {
    void connect(VirtualViewRmi client) throws RemoteException;

    @Override
    CardComponent pickRandomCard() throws RemoteException;

    @Override
    void insertPlayer(String nickname, String color)  throws RemoteException;

    @Override
    void useEngines() throws RemoteException;

    @Override
    void useCannons() throws RemoteException;

    @Override
    void pickPlanet(int index) throws RemoteException;

    @Override
    void endBuild() throws RemoteException;

    @Override
    void selectPiece(int index) throws RemoteException;

    @Override
    void secureCard(CardComponent card) throws RemoteException;

    @Override
    void removeCard(CardComponent card) throws RemoteException;

    @Override
    void placeCard(CardComponent card, int x, int y) throws RemoteException;

    @Override
    void pickCardFacedUp(int index) throws RemoteException;



    @Override
    void dismissCard(CardComponent card) throws RemoteException;
}
