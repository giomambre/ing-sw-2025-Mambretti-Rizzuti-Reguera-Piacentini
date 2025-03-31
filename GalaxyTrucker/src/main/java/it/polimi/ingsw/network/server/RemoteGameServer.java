package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.network.VirtualServer;
import it.polimi.ingsw.network.client.VirtualServerRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RemoteGameServer extends UnicastRemoteObject implements VirtualServerRmi {
    private GameController gameController;
    private Map<String, String> clientToPlayerMap = new HashMap<>();
    final List<VirtualViewRmi> clients = new ArrayList<>();
    private int maxPlayers;
    public RemoteGameServer(GameController gameController) throws RemoteException {
        super();
        this.gameController = gameController;
    }


    @Override
    public void connect(VirtualViewRmi client) throws RemoteException {
        //TODO. Attenzione, più client possono invocare questo metodo simultaneamente!



        synchronized (this.clients) {
            if(clients.isEmpty() ){
                client.showCreateLobby();


            }else if (clients.size() >= maxPlayers) {
                // Se il numero massimo di giocatori è stato raggiunto, rifiuta la connessione
                client.showMessage("Impossibile connettersi: il numero massimo di giocatori è stato raggiunto.");
                return; // non aggiungere il client
            }
            client.showJoinLobby();
            this.clients.add(client);
        }
    }

    public void setMaxPlayers(int numPlayers) throws RemoteException {
        if (numPlayers >= 2 && numPlayers <= 4) {
            this.maxPlayers = numPlayers;
            // Avvisa il client che ha impostato il numero di giocatori
            for (VirtualViewRmi client : clients) {
                try {
                    client.showMessage("Il numero massimo di giocatori è stato impostato a " + numPlayers);
                } catch (RemoteException e) {
                    System.err.println("Errore nel notificare il client: " + e.getMessage());
                }
            }
        } else {
            throw new RemoteException("Il numero di giocatori deve essere tra 2 e 4.");
        }
    }
    @Override
    public void insertPlayer(String nickname,String color) throws RemoteException {

        try{
            Player p = gameController.addPlayer(nickname,Color.valueOf(color.toUpperCase()));
            clientToPlayerMap.put(getClientID(), nickname);
            System.out.println("added player " +  p.toString());
            // Notifica i client senza bloccare il thread principale
            new Thread(() -> {
                synchronized (this.clients) {
                    for (VirtualViewRmi client : clients) { //
                        try {
                            client.showNewPlayer(p);
                        } catch (RemoteException e) {
                            System.err.println("Errore nel notificare un client: " + e.getMessage());
                            // Possiamo rimuovere il client dalla lista se è disconnesso
                        }
                    }
                }
            }).start();
        }
        catch(IllegalArgumentException e){
            String errorMessage = "Errore: " + e.getMessage();  // Solo il messaggio
            System.err.println(errorMessage);  // Stampa l'errore personalizzato
            throw new RemoteException(errorMessage);  // Lancia l'errore con il messaggio
        }


    }

    // Metodo di supporto per ottenere ID del client
    private String getClientID() {

            return UUID.randomUUID().toString();

    }

    private String getNicknameFromClient() {
        return clientToPlayerMap.get(getClientID());
    }



    @Override
    public CardComponent pickRandomCard() throws RemoteException {
        return gameController.getRandomCard();
    }




    @Override
    public void pickCardFacedUp(int index) throws RemoteException {

        try {
            gameController.pickComponentFacedUp( index);
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void placeCard(CardComponent card, int x, int y) throws RemoteException {
        try{
        gameController.addComponent(getNicknameFromClient(), card, x, y);
    }catch(IllegalArgumentException e){
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public void secureCard(CardComponent card) throws RemoteException {
        try{
        gameController.secureComponent(getNicknameFromClient(), card);
    }catch(IllegalArgumentException e){
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public void dismissCard(CardComponent card) throws RemoteException {
        gameController.dismissComponent(getNicknameFromClient(), card);
    }

    @Override
    public CardComponent rotateCard(CardComponent card) throws RemoteException {
    return null;
    }

    @Override
    public void useCannons() throws RemoteException {

    }

    @Override
    public void useEngines() throws RemoteException {

    }

    @Override
    public void removeCard(CardComponent card) throws RemoteException {

    }

    @Override
    public void selectPiece(int index) throws RemoteException {

    }

    @Override
    public void endBuild() throws RemoteException {
        gameController.endPlayerBuildPhase(getNicknameFromClient());
    }

    @Override
    public void pickPlanet(int index) throws RemoteException {

    }
}