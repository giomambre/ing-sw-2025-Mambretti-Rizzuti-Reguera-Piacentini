package it.polimi.ingsw.network;

import it.polimi.ingsw.model.components.CardComponent;

import java.rmi.RemoteException;

/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer {

    void insertPlayer(String username,String color) throws RemoteException;

    void pickCardFacedUp(int index) throws RemoteException;
    void placeCard(CardComponent card, int x, int y) throws RemoteException;
    void secureCard(CardComponent card) throws RemoteException;
    void dismissCard(CardComponent card) throws RemoteException;
    CardComponent rotateCard(CardComponent card) throws RemoteException;
    CardComponent pickRandomCard() throws  RemoteException;

    void useCannons() throws RemoteException;
    void useEngines() throws RemoteException;
    void removeCard(CardComponent card) throws RemoteException;
    void selectPiece(int index) throws RemoteException;
    void endBuild() throws RemoteException;


    //card Adventure methods
    void pickPlanet(int index)  throws RemoteException;



}
