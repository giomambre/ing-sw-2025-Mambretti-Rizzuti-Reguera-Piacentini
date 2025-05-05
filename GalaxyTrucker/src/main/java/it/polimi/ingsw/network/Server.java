package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.network.messages.*;
import javafx.util.Pair;


//import javax.smartcardio.Card;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static it.polimi.ingsw.controller.GameState.*;
import static it.polimi.ingsw.network.messages.MessageType.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<String> connectedNames = new HashSet<>();
    private static Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, ClientHandler> clients = new HashMap<>();
    private GameManager manager = new GameManager();
    private Map<Integer, GameController> all_games = new HashMap<>();
    GameController controller;

    private boolean build120Ended = false;
    private Map<Integer, LobbyTimer> lobbyTimers = new HashMap<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server in ascolto sulla porta " + PORT + "...");
            // Avvia un thread per processare i messaggi in coda

            new Thread(this::processMessages).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuova connessione: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, connectedNames, messageQueue);
                clients.put(handler.getClientId(), handler);

                new Thread(handler).start();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessages() {
        while (true) {
            Message msg;
            synchronized (messageQueue) {
                msg = messageQueue.poll();
            }
            if (msg != null) {
                handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {

        switch (msg.getType()) {

            case SENDED_NAME:
                StandardMessageClient msgClient = (StandardMessageClient) msg;
                String requestedName = msg.getContent();
                System.out.println("nick name inviato : " + requestedName);
                if (connectedNames.contains(requestedName)) {
                    sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_REJECTED, "❌ Nome già in uso. Inserisci un altro nickname.", msgClient.getId_client()));
                } else {
                    connectedNames.add(requestedName);
                    ClientHandler handler = clients.get(msgClient.getId_client());
                    handler.setNickname(requestedName);
                    sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_ACCEPTED, "✅ Nickname accettato: " + requestedName, msgClient.getId_client()));

                }
                break;

            case CREATE_LOBBY:
                CreateLobbyMessage msg_cast = (CreateLobbyMessage) msg;

                try {
                    int lobby_id = manager.createLobby(getNickname(msg_cast.getId_client()), msg_cast.getLimit());
                    System.out.println("🔹 Il client " + getNickname(msg_cast.getId_client()) + " ha creato una lobby con " + msg_cast.getLimit() + " id : " + lobby_id);
                    sendToClient(msg_cast.getId_client(), new Message(MessageType.CREATE_LOBBY, "" + lobby_id));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case SEE_LOBBIES:
                msgClient = (StandardMessageClient) msg;

                sendToClient(msgClient.getId_client(), new AvaiableLobbiesMessage(MessageType.SEE_LOBBIES, "", manager.getAvaibleLobbies()));
                System.out.println("il player vuole vedere le lobby :");
                break;

            case SELECT_LOBBY:
                msgClient = (StandardMessageClient) msg;

                int lobby_id = Integer.parseInt(msg.getContent());
                System.out.println("il player vuole entrare nella lobby :" + lobby_id);

                synchronized (manager) {

                    if (manager.getAvaibleLobbies().contains(lobby_id)) {
                        System.out.println("Successso player joined lobby :" + lobby_id);
                        manager.joinLobby(getNickname(msgClient.getId_client()), lobby_id);
                        Lobby lobby = manager.getLobby(lobby_id);
                        sendToClient(msgClient.getId_client(), new Message(MessageType.SELECT_LOBBY, "" + lobby_id));
                        if (lobby.isLobbyFull()) {

                            GameController game = new GameController(lobby);
                            all_games.put(lobby_id, game);
                            for (String player : lobby.getPlayers()) {

                                sendToClient(getId_client(player), new GameStartedMessage(MessageType.GAME_STARTED, "", all_games.get(lobby_id).getAvailable_colors()));

                            }

                        }

                    } else {
                        System.out.println("Fail player joined lobby :" + lobby_id);
                        sendToClient(msgClient.getId_client(), new AvaiableLobbiesMessage(MessageType.SEE_LOBBIES, "Lobby full o partita iniziata \n", manager.getAvaibleLobbies()));

                    }
                }

                break;

            case COLOR_SELECTED:
                msgClient = (StandardMessageClient) msg;

                controller = all_games.get(getLobbyId(msgClient.getId_client()));

                synchronized (controller) {

                    Color c = Color.valueOf(msg.getContent().toUpperCase());
                    if (controller.getAvailable_colors().contains(c)) {
                        System.out.println("COLORE " + c + " PRESO ");
                        controller.addPlayer(getNickname(msgClient.getId_client()), c);


                        sendToAllClients(controller.getLobby(), new Message(MessageType.COLOR_SELECTED, getNickname(msgClient.getId_client()) + " " + c));


                    } else {

                        sendToClient(msgClient.getId_client(), new GameStartedMessage(MessageType.GAME_STARTED, "", controller.getAvailable_colors()));

                    }


                    if (4 - controller.getAvailable_colors().size() == controller.getLobby().getPlayers().size()) {
                        System.out.println("Tutti i player hanno scelto i colori fase di costruzione iniziata!");


                        controller.startGame();
                        LobbyTimer lt = new LobbyTimer(controller.getLobby().getLimit());

                        lobbyTimers.put(controller.getLobby().getLobbyId(), lt);

                        // Invia BUILD_START

                        sendToAllClients(controller.getLobby(), new Message(MessageType.BUILD_START, "Hai 120s per costruire"));

                        // Avvia 120s

                        lt.start120(() -> lt.handle120End(
                                // callback per quando scadono i 120s E qualcuno ha già finito
                                () -> sendToAllClients(controller.getLobby(), new Message(MessageType.TIME_UPDATE, "⏳ 30s rimanenti")),
                                // callback per quando scadono i 120s E NESSUNO ha ancora finito
                                () -> sendToAllClients(controller.getLobby(), new Message(MessageType.TIME_UPDATE, "⏱ 120 secondi terminati : in attesa del primo giocatore..."))
                                ,
                                () -> {
                                    sendToAllClients(controller.getLobby(), new Message(MessageType.TIME_UPDATE,
                                            "✅ Tempo SCADUTO si comincia con la fase di Controllo! "));


                                    for (Player p : controller.getPlayers()) {

                                        if (!lt.getFinishOrder().contains(p.getNickname())) {
                                            lt.addPlayer(p.getNickname());
                                            sendToClient(getId_client(p.getNickname()), new Message(FORCE_BUILD_PHASE_END, lt.getPositionByPlayer(p.getNickname())));

                                        }

                                    }
                                }

                        ));


                        sendToAllClients(controller.getLobby(), new CardAdventureDeckMessage(MessageType.DECK_CARD_ADVENTURE_UPDATED, "", controller.seeDecksOnBoard()));

                        List<Player> safePlayers = new ArrayList<>();
                        for (Player p : controller.getPlayers()) {
                            safePlayers.add(p.copyPlayer());
                        }
                        sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));


                    }

                }
                break;

            case ASK_CARD:

                msgClient = (StandardMessageClient) msg;

                GameController controller = all_games.get(getLobbyId(msgClient.getId_client()));

                if (controller.getGamestate() != BUILD_PHASE) return;

                synchronized (controller) {

                    if (msgClient.getContent().isEmpty()) { //ha richiesto una carta casuale

                        sendToClient(msgClient.getId_client(), new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", msgClient.getId_client(), controller.getRandomCard()));

                    } else {

                        int i = 0;
                        for (CardComponent c : controller.getFacedUpCards()) {
                            if (c.getCard_uuid().equals(UUID.fromString(msgClient.getContent()))) {
                                CardComponent tmp = controller.removeCardFacedUp(i);
                                sendToClient(msgClient.getId_client(), new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", msgClient.getId_client(), tmp));
                                sendToAllClients(controller.getLobby(), new CardComponentMessage(MessageType.FACED_UP_CARD_UPDATED, "", msgClient.getId_client(), tmp));

                                return;
                            }
                            i++;
                        }
                        sendToClient(msgClient.getId_client(), new Message(MessageType.CARD_UNAVAILABLE, ""));
                    }
                }
                break;


            case DISMISSED_CARD:
                CardComponentMessage card_msg = (CardComponentMessage) msg;
                controller = all_games.get(getLobbyId(card_msg.getId_client()));
                if (controller.getGamestate() == BUILD_PHASE) {

                    synchronized (controller) {

                        controller.dismissComponent(getNickname(card_msg.getId_client()), card_msg.getCardComponent());
                        System.out.println(controller.getFacedUpCards().toString());
                        sendToAllClients(controller.getLobby(), new CardComponentMessage(MessageType.FACED_UP_CARD_UPDATED, "", card_msg.getId_client(), card_msg.getCardComponent()));

                    }
                }
                break;

            case PLACE_CARD:
                CardComponentMessage place_msg = (CardComponentMessage) msg;
                controller = all_games.get(getLobbyId(place_msg.getId_client()));

                if (controller.getGamestate() == BUILD_PHASE) {
                    String[] parts = msg.getContent().split(" ");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    synchronized (controller) {
                        controller.addComponent(getNickname(place_msg.getId_client()), place_msg.getCardComponent(), x, y);
                        List<Player> safePlayers = new ArrayList<>();

                        for (Player p : controller.getPlayers()) {
                            safePlayers.add(p.copyPlayer());  // funzione che crea una "safe copy"
                        }
                        sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));


                    }
                } else {
                    sendToClient(place_msg.getId_client(), new Message(MessageType.UNAVAILABLE_PLACE, ""));
                }
                break;

            case BUILD_PHASE_ENDED:


                StandardMessageClient endMsg = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(endMsg.getId_client()));
                LobbyTimer lt = lobbyTimers.get(controller.getLobby().getLobbyId());
                lt.addPlayer(getNickname(endMsg.getId_client()));
                sendToClient(endMsg.getId_client(), new Message(BUILD_PHASE_ENDED, lt.getPositionByPlayer(getNickname(endMsg.getId_client()))));


                lt.notifyFinished(getNickname(endMsg.getId_client()),
                        // onAllFinished
                        () -> {

                            controller.setGamestate(SUPLLY_PHASE);
                            sendToAllClients(controller.getLobby(), new Message(ADD_CREWMATES, ""));
                            System.out.println("mandato");
                        },
                        // on30StartNeeded
                        () -> {
                            sendToAllClients(controller.getLobby(), new Message(MessageType.TIME_UPDATE,
                                    "🔔 Un giocatore ha finito! "));


                        },

                        () -> {
                            sendToAllClients(controller.getLobby(), new Message(MessageType.TIME_UPDATE,
                                    "✅ Tempo SCADUTO si comincia con la fase di Controllo! "));


                            for (Player p : controller.getPlayers()) {

                                if (!lt.getFinishOrder().contains(p.getNickname())) {
                                    lt.addPlayer(p.getNickname());
                                    sendToClient(getId_client(p.getNickname()), new Message(FORCE_BUILD_PHASE_END, lt.getPositionByPlayer(p.getNickname())));

                                }

                            }
                            controller.setGamestate(SUPLLY_PHASE);

                            sendToAllClients(controller.getLobby(), new Message(ADD_CREWMATES, ""));

                        }

                );


                break;


            case ADD_CREWMATES:

                AddCrewmateMessage addC_msg = (AddCrewmateMessage) msg;
                controller = all_games.get(getLobbyId(addC_msg.getId_client()));

                controller.crewmatesSupply(getNickname(addC_msg.getId_client()), addC_msg.getPos().getKey(), addC_msg.getPos().getValue(), addC_msg.getCmType());
                sendToClient(addC_msg.getId_client(), new Message(MessageType.ADD_CREWMATES, ""));

                break;

            case CHECK_SHIPS:
                System.out.println("Inizia fase di controllo delle navi");

                msgClient = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(msgClient.getId_client()));
                controller.finishSupplyPhase(getNickname(msgClient.getId_client()));
                List<Pair<Integer, Integer>> invalids_connections = new ArrayList<>();

                //    if (controller.getFinished_supply_players().size() == controller.getLobby().getPlayers().size()) {  //quando tutti hanno fatto l equipaggiamento della ciurma

                System.out.println("Inizia fase di controllo delle navi");


                invalids_connections = controller.checkShipConnectors(getNickname(msgClient.getId_client()));


                sendToClient(getId_client(getNickname(msgClient.getId_client())), new InvalidConnectorsMessage(INVALID_CONNECTORS, "", invalids_connections));
                //se la lista è empty allora i connettori sono giusti


                break;


            case FIXED_SHIP_CONNECTORS:
                ShipClientMessage update_msg = (ShipClientMessage) msg;
                Ship ship = update_msg.getPlayer().getShip();
                controller = all_games.get(getLobbyId(update_msg.getId_client()));

                synchronized (controller) {

                    controller.setShipPlance(getNickname(update_msg.getId_client()), ship);


                }
                List<Player> safePlayers = new ArrayList<>();
                for (Player p : controller.getPlayers()) {
                    safePlayers.add(p.copyPlayer());  // funzione che crea una "safe copy"
                }
                sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));


                if(controller.getValidPieces(getNickname(update_msg.getId_client())).size() > 1){

                    List<List<Pair<Integer, Integer>>> pieces = controller.getValidPieces(getNickname(update_msg.getId_client()));
                    sendToClient(update_msg.getId_client(),new ShipPiecesMessage(SELECT_PIECE,"",pieces));
                }
                else if(controller.getValidPieces(getNickname(update_msg.getId_client())).size() ==1){

                    sendToClient(update_msg.getId_client(), new Message(WAITING_FLIGHT, ""));


                } else if (controller.getValidPieces(getNickname(update_msg.getId_client())).size() == 0) {
                    //rimuoverlo dalla partita
                }
                break;


            case SELECT_PIECE:
                StandardMessageClient select_msg = (StandardMessageClient) msg;
                int piece_chosen = Integer.parseInt( select_msg.getContent());
                controller = all_games.get(getLobbyId(select_msg.getId_client()));
                controller.choosePieces(piece_chosen, getNickname(select_msg.getId_client()));
                sendToClient(select_msg.getId_client(), new Message(WAITING_FLIGHT, ""));
                break;

            default:
                System.out.println("⚠ Messaggio sconosciuto ricevuto: " + msg.getType());
                break;


        }
    }


    public String getNickname(UUID id) {
        ClientHandler client = clients.get(id);
        return client.getNickname();
    }

    public UUID getId_client(String nickname) {

        UUID id = UUID.randomUUID();
        for (ClientHandler client : clients.values()) {
            if (client.getNickname() != null && client.getNickname().equals(nickname)) {
                id = client.getClientId();
            }
        }
        return id;
    }


    private void sendToAllClients(Lobby l, Message msg) {

        for (String player : l.getPlayers()) {

            sendToClient(getId_client(player), msg);

        }


    }

    private void sendToClient(UUID id, Message msg) {
        ClientHandler client = clients.get(id);
        if (client != null) {
            try {
                client.sendMessage(msg);
            } catch (IOException e) {
                System.err.println("❌ Errore nell'invio del messaggio a " + id);
            }
        }
    }


    private int getLobbyId(UUID id) {

        String nick = getNickname(id);

        List<Lobby> ls = manager.getAllLobbies();
        for (Lobby lobby : ls) {
            if (lobby.getPlayers().contains(nick)) {
                return lobby.getLobbyId();
            }
        }

        return -1;

    }

    public static void main(String[] args) {
        new Server().start();
    }
}

