package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.view.TUI;
import it.polimi.ingsw.model.view.View;
import it.polimi.ingsw.network.messages.*;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static String nickname;
    private static View virtualView;
    private static UUID clientId;
    private static BlockingQueue<Message> inputQueue = new LinkedBlockingQueue<>();
    private static BlockingQueue<Message> notificationQueue = new LinkedBlockingQueue<>();
    private static List<Player> other_players_local = new ArrayList<>();
    private static Player player_local;
    private static List<CardComponent> facedUp_deck_local = new ArrayList<>();
    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Message response = (Message) in.readObject();
            if (response.getType() == MessageType.ASSIGN_UUID) {
                clientId = ((StandardMessageClient) response).getId_client();
                System.out.println("✅ Connesso con UUID: " + clientId);
            }

            virtualView = new TUI();

            /*new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("/menu")) {
                        showMenu();
                    }
                    try {
                        // Pausa di 1 secondo (1000 millisecondi)
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }
            }).start();*/

            new Thread(() -> {
                try {
                    while (true) {
                        Message msg = (Message) in.readObject();

                        switch (msg.getType()) {
                            case REQUEST_NAME, NAME_REJECTED, NAME_ACCEPTED,
                                 CREATE_LOBBY, SEE_LOBBIES, SELECT_LOBBY, GAME_STARTED, BUILD_START , ASK_CARD, CARD_UNAVAILABLE:
                                inputQueue.put(msg);
                                break;

                            case COLOR_SELECTED,DISMISSED_CARD,FACED_UP_CARD_ADDED,UPDATED_SHIPS:
                                notificationQueue.put(msg);
                                break;

                            default:
                                // messaggi non previsti o debug
                                System.out.println("Messaggio sconosciuto ricevuto: " + msg.getType());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    while (true) {
                        Message msg = inputQueue.take();
                        elaborate(msg); // funzione che chiede input e risponde al server
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            new Thread(() -> {
                try {
                    while (true) {
                        Message msg = notificationQueue.take();
                        handleNotification(msg); // stampa messaggi agli altri giocatori
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();




        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showMenu() {
        virtualView.showMessage("\n=== MENU ===");

        if (player_local != null) {
            virtualView.showPlayer(player_local); // oppure stampa manuale
        } else {
            virtualView.showMessage("🟥 Dati della tua nave non ancora disponibili.");
        }

        if (!other_players_local.isEmpty()) {
            virtualView.showMessage("\n🚀 Navi degli altri giocatori:");
            for (Player p : other_players_local) {
                virtualView.showPlayer(p); // oppure stampa nickname e status
            }
        } else {
            virtualView.showMessage("🟥 Nessuna nave avversaria disponibile.");
        }

        if (!facedUp_deck_local.isEmpty()) {
            virtualView.showMessage("\n🃏 Carte a faccia in su:");
            for (CardComponent c : facedUp_deck_local) {
                virtualView.showCard(c);
            }
        } else {
            virtualView.showMessage("🃏 Nessuna carta disponibile al momento.");
        }

        virtualView.showMessage("=================\n");
    }



    public static void elaborate(Message msg) throws IOException {

        switch (msg.getType()) {

            case REQUEST_NAME, NAME_REJECTED:  //send the nickname request to the server with his UUID
                nickname = virtualView.askNickname();

                try {
                    out.writeObject(new StandardMessageClient(MessageType.SENDED_NAME, nickname, clientId));
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case NAME_ACCEPTED:
                int join_or_create = virtualView.askCreateOrJoin();
                Message to_send;
                if (join_or_create == 1) {

                    int num = virtualView.askNumPlayers();
                    to_send = new CreateLobbyMessage(MessageType.CREATE_LOBBY, "", clientId, num);
                    try {
                        out.writeObject(to_send);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    to_send = new StandardMessageClient(MessageType.SEE_LOBBIES, "", clientId);
                    try {
                        out.writeObject(to_send);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        break;

            case CREATE_LOBBY:
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Errore nella creazione della lobby, riprovare\n");
                    elaborate(new Message(MessageType.NAME_ACCEPTED, ""));
                    break;
                } else {


                }
                break;

            case SEE_LOBBIES:
                AvaiableLobbiesMessage l_msg = (AvaiableLobbiesMessage) msg;

                if (!msg.getContent().isEmpty()) {
                    virtualView.showMessage(msg.getContent());
                }

                if (l_msg.getLobbies().size() == 0) {

                    virtualView.showMessage("Non ci sono Lobby disponibili!");
                    elaborate(new Message(MessageType.NAME_ACCEPTED, ""));
                    break;
                } else {

                    int lobby_index = virtualView.showLobbies(l_msg.getLobbies());

                    out.writeObject(new StandardMessageClient(MessageType.SELECT_LOBBY, "" + lobby_index, clientId));

                }
                break;

            case SELECT_LOBBY:
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Lobby selezionata non disponinbile, riprovare");
                    elaborate(new Message(MessageType.SEE_LOBBIES, ""));
                    break;
                } else {

                    virtualView.showMessage("Sei entrato nella lobby" + msg.getContent());

                }
                break;

            case GAME_STARTED:
                GameStartedMessage gs_msg = (GameStartedMessage) msg;
                if (gs_msg.getContent().isEmpty()) {
                    virtualView.showMessage("Partita avviata!");
                }
                Color c = virtualView.askColor(gs_msg.getAvaiable_colors());
                out.writeObject(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                break;

            case BUILD_START:

                int deck_selected = virtualView.selectDeck();

                if(deck_selected == 1){
                    out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, "", clientId));
                    break;//if content empty -> random card
                }
                else if(deck_selected == 2){

                        if(facedUp_deck_local.isEmpty()){
                            virtualView.showMessage("Non ci sono carte a faccia in alto!\n");
                            elaborate(new Message(MessageType.BUILD_START, ""));
                            break;
                        }else{

                            int index = virtualView.askFacedUpCard(facedUp_deck_local);
                            if(index == -1){
                                elaborate(new Message(MessageType.BUILD_START, ""));
                                break;
                            }
                            UUID selectedCardId = facedUp_deck_local.get(index).getCard_uuid();
                            out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, selectedCardId.toString(), clientId));

                        }
                }
            case CARD_UNAVAILABLE :
                virtualView.showMessage("La carta richiesta non è più disponibile ! ");
                elaborate(new Message(MessageType.BUILD_START, ""));
                break;


            case ASK_CARD:
                CardComponentMessage card_msg = (CardComponentMessage) msg;
                virtualView.showMessage("Carta disponibile");
                int sel = virtualView.showCard(card_msg.getCardComponent());
                if(sel == 3){
                    out.writeObject(new CardComponentMessage(MessageType.DISMISSED_CARD, "",clientId,card_msg.getCardComponent()));
                    elaborate(new Message(MessageType.BUILD_START, ""));
                    break;
                }
                if(sel == 2) {

                    Pair<Integer,Integer> coords = virtualView.askCoords(player_local.getShip());
                    if(coords.getKey()==-1 || coords.getValue()==-1){
                        elaborate(new Message(MessageType.BUILD_START, ""));
                        break;
                    }else{

                        out.writeObject(new CardComponentMessage(MessageType.PLACE_CARD,    coords.getKey() + " " + coords.getValue(),clientId,card_msg.getCardComponent()));
                        break;
                    }

                }
                break;
        }
    }



        public static void handleNotification(Message msg) {


        switch (msg.getType()) {


            case COLOR_SELECTED:

                String[] parts = msg.getContent().split(" ");
                if (parts[0].equals(nickname)) {
                    virtualView.showMessage("Hai scelto il colore : " + parts[1]);
                } else {
                    virtualView.showMessage("Il player " + parts[0] + " ha scelto il colore : " + parts[1]);

                }
                break;


                case UPDATED_SHIPS:
                    PlayersShipsMessage ps_msg = (PlayersShipsMessage) msg;
                    List<Player> tmp = ps_msg.getPlayers();
                    other_players_local.clear(); // rimuove vecchi dati

                    for (Player p : tmp) {

                        if (p.getNickname().equals(nickname)) {
                            player_local = p;
                        } else {
                            other_players_local.add(p);
                        }
                    }
                    for (Player p : tmp) {

                        virtualView.showPlayer(p);
                    }
                    break;



            case FACED_UP_CARD_ADDED:
                CardComponentMessage cpm = (CardComponentMessage) msg;
                if (facedUp_deck_local.stream().noneMatch(c -> c.getCard_uuid().equals(cpm.getCardComponent().getCard_uuid()))) {
                    facedUp_deck_local.add(cpm.getCardComponent());
                }

                System.out.println("ARRIVATA CARTA");
                break;




        }




    }







}
