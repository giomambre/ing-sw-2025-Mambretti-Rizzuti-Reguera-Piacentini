package it.polimi.ingsw.network;

import it.polimi.ingsw.model.components.CardComponent;

import java.rmi.RemoteException;

/**
 * Interfaccia che definisce i metodi del server che devono essere resi accessibili ai client per poter interagire con
 * la logica del server (controller).
 */
public interface VirtualServer {

    void sendMessage(String message) ;
    void addPlayer(String nickname, VirtualView virtualView);



}
