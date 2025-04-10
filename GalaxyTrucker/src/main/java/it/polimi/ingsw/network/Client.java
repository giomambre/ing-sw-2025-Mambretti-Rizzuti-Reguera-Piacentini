package it.polimi.ingsw.network;

import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.view.TUI;
import it.polimi.ingsw.model.view.View;
import it.polimi.ingsw.network.messages.*;

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

public class Client {
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static String nickname;
    private static View virtualView;
    private static UUID clientId;
    private static List<Message> messages = new ArrayList<>();

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

            while (true) {
                Message serverMessage = (Message) in.readObject();
                eleborate(serverMessage);


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void eleborate(Message msg) throws IOException {

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
                    eleborate(new Message(MessageType.NAME_ACCEPTED, ""));
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
                    eleborate(new Message(MessageType.NAME_ACCEPTED, ""));
                    break;
                } else {

                    int lobby_index = virtualView.showLobbies(l_msg.getLobbies());

                    out.writeObject(new StandardMessageClient(MessageType.SELECT_LOBBY, "" + lobby_index, clientId));

                }
                break;

            case SELECT_LOBBY:
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Lobby selezionata non disponinbile, riprovare");
                    eleborate(new Message(MessageType.SEE_LOBBIES, ""));
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

            case COLOR_SELECTED:

                String[] parts = msg.getContent().split(" ");

                if (parts[0].equals(nickname)) {
                    virtualView.showMessage("Hai scelto il colore : " + parts[1]);
                } else {
                    virtualView.showMessage("Il player " + parts[1] + " ha scelto il colore : " + parts[0]);

                }
                break;

        }

    }




}
