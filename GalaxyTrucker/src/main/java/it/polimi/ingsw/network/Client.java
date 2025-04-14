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


            new Thread(() -> {
                try {
                    while (true) {
                        Message msg = (Message) in.readObject();

                        switch (msg.getType()) {
                            case REQUEST_NAME, NAME_REJECTED, NAME_ACCEPTED,
                                 CREATE_LOBBY, SEE_LOBBIES, SELECT_LOBBY, GAME_STARTED, BUILD_START, ASK_CARD,
                                 REJECTED_CARD, CARD_UNAVAILABLE:
                                inputQueue.put(msg);
                                break;

                            case COLOR_SELECTED:
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

                if (deck_selected == 1) {
                    out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, "", clientId));
                    break;//if content empty random card
                } else if (deck_selected == 2) {
                    if (facedUp_deck_local.isEmpty()) {
                        virtualView.showMessage("Non ci sono carte a faccia in alto!\n");
                        elaborate(new Message(MessageType.BUILD_START, ""));
                        break;
                    } else {

                        int index = virtualView.askFacedUpCard(facedUp_deck_local);
                        if (index == -1) {
                            elaborate(new Message(MessageType.BUILD_START, ""));
                            break;
                        }
                        UUID selectedCardId = facedUp_deck_local.get(index).getCard_uuid();
                        out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, selectedCardId.toString(), clientId));

                    }
                }
            case CARD_UNAVAILABLE:
                virtualView.showMessage("La carta richiesta non è più disponibile ! ");
                elaborate(new Message(MessageType.BUILD_START, ""));
                break;


            case ASK_CARD:
                CardComponentMessage card_msg = (CardComponentMessage) msg;
                virtualView.showMessage("Carta disponibile");
                int sel = virtualView.showCard(card_msg.getCardComponent());
                if (sel == 3) {
                    out.writeObject(new CardComponentMessage(MessageType.REJECTED_CARD, "", clientId, card_msg.getCardComponent()));
                }
                if (sel == 2) System.out.println("da capire");
                break;

            case ADD_CARD:
                AddCardMessage posCard_msg = (AddCardMessage) msg;
                virtualView.showMessage("Stai scegliendo dove posizionare  " + posCard_msg.getCardComponent());
                Pair pos = virtualView.addCard();
                out.writeObject(new AddCardMessage(MessageType.ADD_CARD, "" + pos, clientId, posCard_msg.getCardComponent(), pos));
                break;

            case POSITION_UNAVAILABLE:
                virtualView.showMessage("Hai posizionato la carta in una cella già occupata!");
                elaborate(new Message(MessageType.BUILD_START, ""));
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

            case FACED_UP_CARD_ADDED:
                CardComponentMessage cpm = (CardComponentMessage) msg;
                facedUp_deck_local.add(cpm.getCardComponent());


        }


    }


}
