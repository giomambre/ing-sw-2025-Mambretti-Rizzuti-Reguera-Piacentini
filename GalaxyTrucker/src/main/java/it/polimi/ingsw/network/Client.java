package it.polimi.ingsw.network;

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

    public static void eleborate(Message msg) {

        switch (msg.getType()) {

            case REQUEST_NAME, NAME_REJECTED:  //send the nickname request to the server with his UUID
                String nick = virtualView.askNickname();
                try {
                    out.writeObject(new StandardMessageClient(MessageType.SENDED_NAME, nick,clientId));
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case NAME_ACCEPTED:
                int join_or_create = virtualView.askCreateOrJoin();
                Message to_send;
                if(join_or_create == 1) {

                    int num = virtualView.askNumPlayers();
                    to_send = new CreateLobbyMessage(MessageType.CREATE_LOBBY, "", clientId,num);
                    try {
                        out.writeObject(to_send);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    to_send = new StandardMessageClient(MessageType.SEE_LOBBIES, "",clientId);
                    try {
                        out.writeObject(to_send);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


            case CREATE_LOBBY:
                if(msg.getContent().equals("")) {
                   virtualView.showGenericError("Errore nella creazione della lobby, riprovare");
                    eleborate(new Message(MessageType.NAME_ACCEPTED,""));
                    break;
                }else{



                }
            break;

            case SEE_LOBBIES:
                AvaiableLobbiesMessage l_msg = (AvaiableLobbiesMessage) msg;
                if(l_msg.getLobbies().size() == 0) {

                    virtualView.showMessage("Non ci sono Lobby disponibili!");
                    eleborate(new Message(MessageType.NAME_ACCEPTED,""));
                    break;
                }else {

                        int lobby_index = virtualView.showLobbies(l_msg.getLobbies());

                }
                break;



        }

    }




}
