package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.GameManager;

import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.network.messages.*;
import javafx.util.Pair;


//import javax.smartcardio.Card;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


import static it.polimi.ingsw.controller.GameState.*;

import static it.polimi.ingsw.model.enumerates.Direction.*;
import static it.polimi.ingsw.model.enumerates.Direction.South;
import static it.polimi.ingsw.network.Client.throwDice;
import static it.polimi.ingsw.network.messages.MessageType.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server implements RemoteServer {
    private static final int SOCKET_PORT = 12345;
    private static final int RMI_PORT = 1099;
    private static Set<String> connectedNames = ConcurrentHashMap.newKeySet();
    private static Set<String> disconnectedNames = ConcurrentHashMap.newKeySet();
    private final Map<UUID, ConnectionHandler> dis_clients = new ConcurrentHashMap<>();
    private static Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, ConnectionHandler> clients = new ConcurrentHashMap<>();
    private GameManager manager = new GameManager();
    private Map<Integer, GameController> all_games = new ConcurrentHashMap<>();
    GameController controller;
    public String util_string = "";

    private Map<Integer, LobbyTimer> lobbyTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newScheduledThreadPool(1);

    public void start() {
        new Thread(this::startSocketServer).start();
        new Thread(this::startRmiServer).start();
        new Thread(this::processMessages).start();
        startHeartbeat();
        System.out.println("Server pronto.");
    }

    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            System.out.println("Server Socket in ascolto sulla porta " + SOCKET_PORT + "...");
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuova connessione Socket: " + clientSocket.getInetAddress());
                SocketConnectionHandler handler = new SocketConnectionHandler(clientSocket, messageQueue, this);
                clients.put(handler.getClientId(), handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Errore server Socket: " + e.getMessage());
        }
    }

    private void startRmiServer() {
        try {
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind("RmiServer", stub);
            System.out.println("Server RMI in ascolto sulla porta " + RMI_PORT);
        } catch (RemoteException e) {
            System.err.println("Errore avvio server RMI: " + e.getMessage());
        }
    }


    @Override
    public UUID registerClient(RemoteClient client) throws RemoteException {
        UUID clientId = UUID.randomUUID();
        RmiConnectionHandler handler = new RmiConnectionHandler(client, clientId);
        clients.put(clientId, handler);
        System.out.println("Nuovo client RMI registrato con ID: " + clientId);
        try {
            handler.sendMessage(new StandardMessageClient(MessageType.ASSIGN_UUID, "", clientId));
            handler.sendMessage(new Message(MessageType.REQUEST_NAME, ""));
        } catch (IOException e) {
            System.err.println("Disconnessione del client RMI durante la registrazione: " + clientId);
            clients.remove(clientId);
        }
        return clientId;
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        synchronized (messageQueue) {
            messageQueue.add(message);
        }
    }

    private void processMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            Message msg;
            synchronized (messageQueue) {
                msg = messageQueue.poll();
            }
            if (msg != null) {
                try {
                    handleMessage(msg);
                } catch (Exception e) {
                    System.err.println("Errore durante la gestione del messaggio: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleMessage(Message msg) {

        switch (msg.getType()) {


            case SENDED_NAME:

                StandardMessageClient msgClient = (StandardMessageClient) msg;
                String requestedName = msg.getContent();
                System.out.println("nick name inviato : " + requestedName);

                if (disconnectedNames.contains(requestedName)) {

                    sendToClient(msgClient.getId_client(), new NotificationMessage(NOTIFICATION, "✅ RICONESSIONE RIUSCITA ti sei unito alla lobby, ora attendi che sia di nuovo il tuo turno!", ""));

                    ConnectionHandler handler = clients.get(msgClient.getId_client());
                    if (handler != null) {
                        handler.setNickname(requestedName);
                    }
                    controller = all_games.get(getLobbyId(msgClient.getId_client()));


                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "✅ Il giocatore " + requestedName + " si è riconesso alla partita !", requestedName));

                    handleClientReconnection(msgClient.getId_client());
                    break;
                } else if (connectedNames.contains(requestedName)) {
                    sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_REJECTED, "❌ Nome già in uso. Inserisci un altro nickname.", msgClient.getId_client()));

                } else {
                    if (requestedName.isEmpty()) {
                        sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_REJECTED, "❌ Stringa vuota non accettata. Inserisci un altro nickname.", msgClient.getId_client()));
                        break;
                    } else {
                        sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_ACCEPTED, "✅ Nickname accettato: " + requestedName, msgClient.getId_client()));

                    }

                    connectedNames.add(requestedName);
                    ConnectionHandler handler = clients.get(msgClient.getId_client());
                    if (handler != null) {
                        handler.setNickname(requestedName);
                    }

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
                        System.out.println("Successo player joined lobby :" + lobby_id);
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

                        sendToClient(msgClient.getId_client(), new GameStartedMessage(MessageType.GAME_STARTED, " COLORE GIA PRESO", controller.getAvailable_colors()));

                    }


                    System.out.println(controller.getAvailable_colors());
                    if (4 - controller.getAvailable_colors().size() == controller.getLobby().getPlayers().size()) {
                        System.out.println("Tutti i player hanno scelto i colori fase di costruzione iniziata!");


                        controller.startGame();
                        LobbyTimer lt = new LobbyTimer(controller.getLobby().getLimit(), controller.getLobby());

                        lobbyTimers.put(controller.getLobby().getLobbyId(), lt);

                        // Invia BUILD_START

                        sendToAllClients(controller.getLobby(), new Message(MessageType.BUILD_START, "Hai 270s per costruire"));

                        // Avvia 120s

                        lt.start120(() -> lt.handle120End(
                                // callback per quando scadono i 120s E qualcuno ha già finito
                                () -> sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE, "⏳ 90s rimanenti")),
                                // callback per quando scadono i 120s E NESSUNO ha ancora finito
                                () -> sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE, "⏱ 270 secondi terminati : in attesa del primo giocatore..."))
                                ,
                                () -> {
                                    sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE,
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
                            sendToAllClients(lt.getLobby(), new Message(ADD_CREWMATES, ""));
                        },
                        // on30StartNeeded
                        () -> {
                            sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE,
                                    "🔔 Un giocatore ha finito! "));


                        },

                        () -> {
                            sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE,
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


                controller.setBuild_order_players(lt.getFinishOrder());
                break;


            case ADD_CREWMATES:

                AddCrewmateMessage addC_msg = (AddCrewmateMessage) msg;
                controller = all_games.get(getLobbyId(addC_msg.getId_client()));

                controller.crewmatesSupply(getNickname(addC_msg.getId_client()), addC_msg.getPos().getKey(), addC_msg.getPos().getValue(), addC_msg.getCmType());

                break;

            case CHECK_SHIPS:


                msgClient = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(msgClient.getId_client()));
                controller.setGamestate(FIXING_SHIPS);
                controller.finishSupplyPhase(getNickname(msgClient.getId_client()));
                List<Pair<Integer, Integer>> invalids_connections;

                //    if (controller.getFinished_supply_players().size() == controller.getLobby().getPlayers().size()) {  //quando tutti hanno fatto l equipaggiamento della ciurma


                invalids_connections = controller.checkShipConnectors(getNickname(msgClient.getId_client()));


                sendToClient(getId_client(getNickname(msgClient.getId_client())), new InvalidConnectorsMessage(INVALID_CONNECTORS, "", invalids_connections));
                //se la lista è empty allora i connettori sono giusti


                break;


            case FIXED_SHIP_CONNECTORS:
                ShipClientMessage update_msg = (ShipClientMessage) msg;
                System.out.println("Inizia fase di controllo delle navi");
                Ship ship = update_msg.getPlayer().getShip();
                controller = all_games.get(getLobbyId(update_msg.getId_client()));

                synchronized (controller) {

                    controller.setShipPlance(getNickname(update_msg.getId_client()), ship);


                }
                List<Player> safePlayers = new ArrayList<>();
                for (Player p : controller.getPlayers()) {
                    safePlayers.add(p.copyPlayer());
                }
                sendToClient(update_msg.getId_client(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                System.out.println(getNickname(update_msg.getId_client()) + " " + controller.getValidPieces(getNickname(update_msg.getId_client())).size());

                if (controller.getValidPieces(getNickname(update_msg.getId_client())).size() > 1) {

                    List<List<Pair<Integer, Integer>>> pieces = controller.getValidPieces(getNickname(update_msg.getId_client()));
                    sendToClient(update_msg.getId_client(), new ShipPiecesMessage(SELECT_PIECE, "", pieces));
                } else if (controller.getValidPieces(getNickname(update_msg.getId_client())).size() == 1) {

                    safePlayers = new ArrayList<>();
                    for (Player p : controller.getPlayers()) {
                        safePlayers.add(p.copyPlayer());
                    }
                    sendToClient(update_msg.getId_client(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                    sendToClient(update_msg.getId_client(), new Message(WAITING_FLIGHT, ""));
                    if (!controller.getWaitingFlyPlayers().contains(getNickname(update_msg.getId_client()))) {
                        controller.addWaitingFlyPlayer(getNickname(update_msg.getId_client()));
                    }


                } else if (controller.getValidPieces(getNickname(update_msg.getId_client())).isEmpty()) {
                    sendToClient(update_msg.getId_client(), new Message(INVALID_SHIP, ""));
                    controller.removePlayerFromOrder(getNickname(update_msg.getId_client()));

                }

                if (controller.getBuild_order_players().size() == 1) {

                    Player p = controller.getBuild_order_players().get(0);
                    sendToClient(getId_client(p.getNickname()), new Message(WIN, ""));
                    break;

                }
                if (controller.getWaitingFlyPlayers().size() == controller.getBuild_order_players().size()) {

                    handleMessage(new StandardMessageClient(START_FLIGHT, "", update_msg.getId_client()));
                    controller.setGamestate(FLYING_PHASE);

                }
                break;


            case SELECT_PIECE:
                StandardMessageClient select_msg = (StandardMessageClient) msg;
                int piece_chosen = Integer.parseInt(select_msg.getContent());
                controller = all_games.get(getLobbyId(select_msg.getId_client()));
                controller.choosePieces(piece_chosen, getNickname(select_msg.getId_client()));
                safePlayers = new ArrayList<>();

                for (Player p : controller.getPlayers()) {
                    safePlayers.add(p.copyPlayer());  // funzione che crea una "safe copy"
                }
                sendToClient(select_msg.getId_client(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                sendToClient(select_msg.getId_client(), new Message(WAITING_FLIGHT, ""));
                if (!controller.getWaitingFlyPlayers().contains(getNickname(select_msg.getId_client()))) {
                    controller.addWaitingFlyPlayer(getNickname(select_msg.getId_client()));
                }


                if (controller.getWaitingFlyPlayers().size() == controller.getBuild_order_players().size()) {

                    handleMessage(new StandardMessageClient(START_FLIGHT, "", select_msg.getId_client()));

                    controller.setGamestate(FLYING_PHASE);
                }

                break;


            case START_FLIGHT:
                StandardMessageClient start_msg = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(start_msg.getId_client()));
                controller.startFlight();
                sendToAllClients(controller.getLobby(), new Message(START_FLIGHT, ""));
                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                //CardAdventure adventure = controller.getRandomAdventure();

                //       CardAdventure adventure = new Epidemic(1, 0, CardAdventureType.Epidemic, "/images/cardAdventure/GT-epidemic_2.jpg");


                /*CardAdventure adventure = new Smugglers(2, 1, CardAdventureType.Smugglers, 8,
                        Arrays.asList(
                                Cargo.Red,
                                Cargo.Yellow,
                                Cargo.Yellow
                        ),
                        3,"");*/
                /*CardAdventure adventure = new MeteorSwarm(1, 0, CardAdventureType.MeteorSwarm,
                        List.of(
                                new Pair<>(MeteorType.SmallMeteor, North),
                                new Pair<>(MeteorType.SmallMeteor, North),
                                new Pair<>(MeteorType.SmallMeteor, West),
                                new Pair<>(MeteorType.SmallMeteor, East),
                                new Pair<>(MeteorType.SmallMeteor, South)
                        ),"/images/cardAdventure/GT-meteorSwarm_1.2.jpg"
                );*/

                CardAdventure adventure= new AbandonedShip(1,0,CardAdventureType.AbandonedShip,2,2,"/images/cardAdventure/GT-abandonedShip_1.1.jpg");

//             CardAdventure adventure = new Planets(1,0,CardAdventureType.Planets, Arrays.asList(Arrays.asList(
//                     Cargo.Red,
//                     Cargo.Yellow,
//                     Cargo.Yellow),
//                Arrays.asList(
//                        Cargo.Red,
//                        Cargo.Yellow,
//                        Cargo.Yellow),
//                Arrays.asList(
//                        Cargo.Red,
//                        Cargo.Yellow,
//                        Cargo.Yellow)
//             ),"/images/cardAdventure/GT-planets_1.1.jpg" );

//             CardAdventure adventure = new Smugglers(2, 1, CardAdventureType.Smugglers, 4,
//                        Arrays.asList(
//                                Cargo.Red,
//                                Cargo.Yellow,
//                                Cargo.Yellow
//                        ),
//                        3,"/images/cardAdventure/GT-smugglers_1.jpg");
                 adventure = new CombatZone(2, 4, CardAdventureType.CombatZone, 0, 0, 3,
                        List.of(
                                new Pair<>(MeteorType.LightCannonFire, North),
                                new Pair<>(MeteorType.LightCannonFire, West),
                                new Pair<>(MeteorType.LightCannonFire, East),
                                new Pair<>(MeteorType.HeavyCannonFire, South)
                       ),"/images/cardAdventure/GT-combatZone_2.jpg"
                );

                //CardAdventure adventure = new Stardust(1,0,CardAdventureType.Stardust,"");
                //CardAdventure adventure = controller.getRandomAdventure();
                //adventure = new Slavers(1, 1, CardAdventureType.Slavers, 6, 3, 5,"/images/cardAdventure/GT-slavers_1.jpg");

                manageAdventure(adventure, controller);


                break;


            case ASTRONAUT_LOSS:
                StandardMessageClient str_msg = (StandardMessageClient) msg;

                controller = all_games.get(getLobbyId(str_msg.getId_client()));

                String curr_nick = controller.nextAdventurePlayer();
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza cannoni ! \n", curr_nick));

                sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", controller.getCurrentAdventure()));
                controller.getEngineValues().clear();
                break;

            case ADVENTURE_COMPLETED:
                ShipClientMessage adv_msg = (ShipClientMessage) msg;
                controller = all_games.get(getLobbyId(adv_msg.getId_client()));

                if (adv_msg.getPlayer().getShip() != null) {
                    Ship s = adv_msg.getPlayer().getShip();
                    controller.setShipPlance(getNickname(adv_msg.getId_client()), s);
                    safePlayers = new ArrayList<>();
                    for (Player p : controller.getPlayers()) {
                        safePlayers.add(p.copyPlayer());
                    }
                    sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));

                }


                if (controller.getActivePlayers().size() == 1) {

                    manageAdventure(null, controller);

                }


                switch (controller.getCurrentAdventure().getType()) {


                    case OpenSpace:


                        int eng_power = (int) Double.parseDouble(adv_msg.getContent());

                        controller.movePlayer(getNickname(adv_msg.getId_client()), eng_power);
                        sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + getNickname(adv_msg.getId_client())
                                + " HA DICHIRATO UNA POTENZA MOTORE :  " + eng_power, controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));
                        System.out.println("OpenSpace completato");
                        if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                            adventure = controller.getRandomAdventure();

                            manageAdventure(adventure, controller);

                        } else {
                            String next_p = controller.nextAdventurePlayer();

                            sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta dichiarando la potenza motoreA ! \n", next_p));

                            sendToClient(getId_client(next_p), new AdventureCardMessage(OPEN_SPACE, "", controller.getCurrentAdventure()));

                        }

                        break;


                    case AbandonedStation:

                        ShipClientMessage abandoned_msg = (ShipClientMessage) msg;

                        if (abandoned_msg.getContent().isEmpty()) {
                            sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(abandoned_msg.getId_client()) + " NON ha ACCETTATO  ! \n", getNickname(abandoned_msg.getId_client())));

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {
                                String next_p = controller.nextAdventurePlayer();

                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se accettare la STAZIONE ABBANDONATA ! \n", next_p));

                                sendToClient(getId_client(next_p), new AdventureCardMessage(ABANDONED_STATION, "", controller.getCurrentAdventure()));


                            }

                        } else {
                            controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());

                            sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + getNickname(adv_msg.getId_client())
                                    + " HA ACCETTATO L'AVVENTURA, ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                            adventure = controller.getRandomAdventure();
                            manageAdventure(adventure, controller);
                        }

                        break;


                    case AbandonedShip:

                        abandoned_msg = (ShipClientMessage) msg;
                        if (abandoned_msg.getContent().isEmpty()) {
                            sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(abandoned_msg.getId_client()) + " NON ha ACCETTATO  ! \n", getNickname(abandoned_msg.getId_client())));

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {
                                String next_p = controller.nextAdventurePlayer();
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se accettare la NAVE ABBANDONATA ! \n", next_p));

                                sendToClient(getId_client(next_p), new AdventureCardMessage(ABANDONED_SHIP, "", controller.getCurrentAdventure()));


                            }

                        } else {
                            controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());
                            controller.addCredits(getNickname(adv_msg.getId_client()), ((AbandonedShip) controller.getCurrentAdventure()).getGiven_credits());
                            sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + getNickname(adv_msg.getId_client())
                                    + " HA ACCETTATO L'AVVENTURA, ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                            adventure = controller.getRandomAdventure();
                            manageAdventure(adventure, controller);


                        }
                        break;


                    case MeteorSwarm:

                        controller.removeFromAdventure(getNickname(adv_msg.getId_client()));

                        if (controller.getActivePlayers().size() <= 1) {

                        //gioco finito
                        manageAdventure(null,controller);

                        }


                        if (controller.getAdv_index() == controller.getAdv_index()) {
                            adventure = controller.getRandomAdventure();
                            manageAdventure(adventure, controller);
                        }

                        break;


                    case Planets:
                        ShipClientMessage planet_msg = (ShipClientMessage) msg;
                        if (planet_msg.getContent().isEmpty()) {
                            sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(planet_msg.getId_client()) + " non è sceso su NESSUN PIANETA ! \n", getNickname(planet_msg.getId_client())));

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {
                                String next_p = controller.nextAdventurePlayer();

                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se prendere un PIANETA ! \n", next_p));

                                sendToClient(getId_client(next_p), new AdventureCardMessage(PLANETS, controller.getPlanets(), controller.getCurrentAdventure()));

                            }

                        } else {

                            controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());
                            controller.addPlanetTaken(planet_msg.getContent());
                            sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + getNickname(adv_msg.getId_client())
                                    + " HA ACCETTATO L'AVVENTURA, ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));


                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);
                            } else {

                                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(PLANETS, controller.getPlanets(), controller.getCurrentAdventure()));

                            }


                        }
                        break;

                    case CombatZone:
                        String[] type = msg.getContent().split(" ");
                        CombatZone combatZone = (CombatZone) controller.getCurrentAdventure();
                        StandardMessageClient cbz_msg = (StandardMessageClient) msg;


                        if (combatZone.getId() == 1) {

                            switch (type[0]) {

                                case "eng":
                                    Double power = Double.parseDouble(type[1]);
                                    controller.addEngineValue(getNickname(cbz_msg.getId_client()), power);
                                    sendToAllClients(controller.getLobby(), new Message(ENGINE_POWER, getNickname(cbz_msg.getId_client()) + " " + power));

                                    if (controller.getEngineValues().size() == controller.getActivePlayers().size()) {
                                        controller.initializeAdventure(controller.getCurrentAdventure());

                                        sendToAllClients(controller.getLobby(), new RankingMessage(ENGINE_POWER_RANK, "1", controller.getEngineValues()));
                                        controller.getEngineValues().clear();

                                        break;

                                    }


                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "engine", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza motore ! \n", curr_nick));

                                    break;

                                case "can":

                                    power = Double.parseDouble(type[1]);
                                    controller.addCannonValue(getNickname(cbz_msg.getId_client()), power);
                                    sendToAllClients(controller.getLobby(), new Message(CANNON_POWER, getNickname(cbz_msg.getId_client()) + " " + power));

                                    if (controller.getListCannonPower().size() == controller.getActivePlayers().size()) {

                                        sendToAllClients(controller.getLobby(), new RankingMessage(CANNON_POWER_RANK, "1", controller.getEngineValues()));
                                        controller.getListCannonPower().clear();
                                        break;

                                    }

                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la sua potenza cannoni ! \n", curr_nick));


                                    break;


                            }


                        } else if (combatZone.getId() == 0) {
                            switch (type[0]) {

                                case "can":

                                    double power = Double.parseDouble(type[1]);
                                    controller.addCannonValue(getNickname(cbz_msg.getId_client()), power);
                                    sendToAllClients(controller.getLobby(), new Message(CANNON_POWER, getNickname(cbz_msg.getId_client()) + " " + power));

                                    if (controller.getListCannonPower().size() == controller.getActivePlayers().size()) {

                                        sendToAllClients(controller.getLobby(), new RankingMessage(CANNON_POWER_RANK, "0", controller.getListCannonPower()));
                                        controller.movePlayer(controller.getLeastCannon(), -controller.getCurrentAdventure().getCost_of_days());

                                        sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + controller.getLeastCannon()
                                                + " HA pagato la penitenza , ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));
                                        controller.getListCannonPower().clear();
                                        controller.initializeAdventure(controller.getCurrentAdventure());
                                        curr_nick = controller.nextAdventurePlayer();
                                        sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "engine", controller.getCurrentAdventure()));
                                        sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la sua POTENZA MOTORI ! \n", curr_nick));


                                        break;

                                    }

                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la POTENZA CANNONI! \n", curr_nick));


                                    break;


                                case "eng":

                                    power = Double.parseDouble(type[1]);
                                    controller.addEngineValue(getNickname(cbz_msg.getId_client()), power);
                                    sendToAllClients(controller.getLobby(), new Message(ENGINE_POWER, getNickname(cbz_msg.getId_client()) + " " + power));

                                    if (controller.getEngineValues().size() == controller.getActivePlayers().size()) {

                                        sendToAllClients(controller.getLobby(), new RankingMessage(ENGINE_POWER_RANK, "0", controller.getEngineValues()));
                                        controller.getEngineValues().clear();
                                        adventure = controller.getRandomAdventure();
                                        manageAdventure(adventure, controller);

                                        break;

                                    }


                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "engine", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la sua POTENZA MOTORE ! \n", curr_nick));

                                    break;
                            }


                        }
                        break;


                    case Smugglers:
                        String[] esit = msg.getContent().split("\\s+");

                        switch (esit[0]) {


                            case "w":

                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO i CONTRABBANDIERI, ma non ha riscosso la ricompensa  ! \n", getNickname(adv_msg.getId_client())));
                                adventure = controller.getRandomAdventure();

                                manageAdventure(adventure, controller);
                                break;

                            case "ww":
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO i CONTRABBANDIERI ! \n", getNickname(adv_msg.getId_client())));

                                controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());


                                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "\nIL PLAYER " + getNickname(adv_msg.getId_client())
                                        + " HA PRESO LA RICOMPENSA , ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo" + "\n", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));


                                adventure = controller.getRandomAdventure();

                                manageAdventure(adventure, controller);
                                break;

                            case "d":
                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();

                                    manageAdventure(adventure, controller);
                                    return;


                                }
                                curr_nick = controller.nextAdventurePlayer();
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PAREGGIATO i CONTRABBANDIERI ! \n", getNickname(adv_msg.getId_client())));
                                sendToClient(getId_client(curr_nick), new AdventureCardMessage(SMUGGLERS, "", controller.getCurrentAdventure()));
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici contrabbandieri ! \n", curr_nick));
                                break;


                            case "l":
                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();

                                    manageAdventure(adventure, controller);
                                    return;


                                }
                                curr_nick = controller.nextAdventurePlayer();
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PERSO contro i  CONTRABBANDIERI ! \n", getNickname(adv_msg.getId_client())));
                                sendToClient(getId_client(curr_nick), new AdventureCardMessage(SMUGGLERS, "", controller.getCurrentAdventure()));
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici contrabbandieri ! \n", curr_nick));
                                break;


                        }
                        break;

                    case Pirates:


                        esit = msg.getContent().split("\\s+");

                        switch (esit[0]) {

                            case "ww":

                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO i PIRATI ! \n", getNickname(adv_msg.getId_client())));

                                controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());


                                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "\nIL PLAYER " + getNickname(adv_msg.getId_client())
                                        + " HA PRESO LA RICOMPENSA , ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo" + "\n", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));


                                adventure = controller.getRandomAdventure();

                                manageAdventure(adventure, controller);
                                break;


                            case "w":
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO i PIRATI, ma non ha riscosso la ricompensa  ! \n", getNickname(adv_msg.getId_client())));
                                adventure = controller.getRandomAdventure();

                                manageAdventure(adventure, controller);
                                break;


                            case "d":

                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();

                                    manageAdventure(adventure, controller);
                                    return;


                                }
                                curr_nick = controller.nextAdventurePlayer();
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PAREGGIATO i PIRATI ! \n", getNickname(adv_msg.getId_client())));
                                sendToClient(getId_client(curr_nick), new AdventureCardMessage(PIRATES, controller.getPirates_coords(), controller.getCurrentAdventure()));
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Pirati ! \n", curr_nick));
                                break;

                            case "l":
                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();
                                    manageAdventure(adventure, controller);
                                } else {
                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PERSO contro i PIRATI ! \n", getNickname(adv_msg.getId_client())));
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(PIRATES, controller.getPirates_coords(), controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Pirati ! \n", curr_nick));
                                }
                                break;
                        }
                        break;

                    case Slavers:
                        esit = msg.getContent().split("\\s+");

                        switch (esit[0]) {
                            case "ww":
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO gli SCHIAVISTI ! \n", getNickname(adv_msg.getId_client())));
                                controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());
                                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "\nIL PLAYER " + getNickname(adv_msg.getId_client())
                                        + " HA PRESO LA RICOMPENSA , ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo" + "\n", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));
                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);
                                break;

                            case "w":
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA SCONFITTO gli SCHIAVISTI, ma non ha riscosso la ricompensa  ! \n", getNickname(adv_msg.getId_client())));
                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);
                                break;

                            case "d":
                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();
                                    manageAdventure(adventure, controller);
                                } else {
                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PAREGGIATO con gli SCHIAVISTI ! \n", getNickname(adv_msg.getId_client())));
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(SLAVERS, "", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Schiavisti ! \n", curr_nick));
                                }
                                break;

                            case "l":
                                if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {
                                    adventure = controller.getRandomAdventure();
                                    manageAdventure(adventure, controller);
                                } else {
                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PERSO contro gli SCHIAVISTI ! \n", getNickname(adv_msg.getId_client())));
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(SLAVERS, "", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Schiavisti ! \n", curr_nick));
                                }
                                break;
                        }
                        sendToAllClients(controller.getLobby(), new PlayersShipsMessage(UPDATED_SHIPS, "", controller.getActivePlayers()));
                        break;

                    case Epidemic:
                    case Stardust:

                        break;
                }
                break;

            case CARGO_LOSS:
                String[] type = msg.getContent().split("\\s+");
                StandardMessageClient cl_msg = (StandardMessageClient) msg;

                controller = all_games.get(getLobbyId(cl_msg.getId_client()));
                if (type[0].equals("cz")) {

                    adventure = controller.getRandomAdventure();

                    manageAdventure(adventure, controller);


                }
                break;


            case END_FLIGHT:
                StandardMessageClient end_msg = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(end_msg.getId_client()));
                controller.removeFromAdventure(getNickname(end_msg.getId_client()));
                controller.removeFromActivePlayers(getNickname(end_msg.getId_client()));

                sendToClient(end_msg.getId_client(), new Message(END_FLIGHT, ""));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "IL PLAYER " + getNickname(end_msg.getId_client()) + " è STATO KICKATO DALLA PARTITA PER NAVE INVALIDA", getNickname(end_msg.getId_client())));
                if (controller.getActivePlayers().size() <= 1) {
                    controller.setRewards();
                    sendToAllClients(controller.getLobby(), new PlayersShipsMessage(GAME_FINISHED, "", controller.getActivePlayers()));

                }
                break;


            default:
                System.out.println("⚠ Messaggio sconosciuto ricevuto: " + msg.getType());
                break;

        }

    }


    public void manageAdventure(CardAdventure adventure, GameController controller) {


        for (Player p : controller.getPlayers()) { //kick dei i giocatori doppiati

            if (!controller.getActivePlayers().contains(p)) {

                sendToClient(getId_client(p.getNickname()), new Message(END_FLIGHT, ""));
                controller.removeFromActivePlayers(p.getNickname());
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + p.getNickname() + " è stato kickato dalla partita ! \n", p.getNickname()));

            }

        }

        if (adventure == null || controller.getActivePlayers().size() <= 1) {

            controller.setRewards();
            sendToAllClients(controller.getLobby(), new PlayersShipsMessage(GAME_FINISHED, "", controller.getActivePlayers()));
            return;
        }


        if (controller.getActivePlayers().size() - controller.getDisconnected_players().size() == 1) {
            controller.setIn_pause(1);
            sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "SEI RIMASTO SOLO TU, IN PARTITA IN PAUSA! \n", "useless"));

            return;
        }

        switch (adventure.getType()) {


            case OpenSpace:

                controller.initializeAdventure(adventure);

                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                String next_p = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(next_p);
                sendToClient(getId_client(next_p), new AdventureCardMessage(OPEN_SPACE, "", adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta dichiarando la sua POTENZA MOTORE ! \n", next_p));

                break;


            case AbandonedStation:
                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));

                if (controller.getAdventureOrder().isEmpty()) {

                    sendToAllClients(controller.getLobby(), new Message(ADVENTURE_SKIP, ""));
                    adventure = controller.getRandomAdventure();
                    manageAdventure(adventure, controller);
                    break;
                }
                next_p = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(next_p);

                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se accettare la STAZIONE ABBANDONATA ! \n", next_p));

                sendToClient(getId_client(next_p), new AdventureCardMessage(ABANDONED_STATION, "", adventure));
                break;


            case AbandonedShip:


                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));

                if (controller.getAdventureOrder().isEmpty()) {

                    sendToAllClients(controller.getLobby(), new Message(ADVENTURE_SKIP, ""));
                    adventure = controller.getRandomAdventure();
                    manageAdventure(adventure, controller);
                    break;
                }
                next_p = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(next_p);

                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se accettare la NAVE ABBANDONATA ! \n", next_p));

                sendToClient(getId_client(next_p), new AdventureCardMessage(ABANDONED_SHIP, "", adventure));
                break;


            case MeteorSwarm:
                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                MeteorSwarm ms = (MeteorSwarm) adventure;
                StringBuilder coords_m = new StringBuilder();
                for (int i = 0; i < ms.getMeteors().size(); i++) {

                    coords_m.append(controller.throwDice()).append(" ");

                }

                sendToAllClients(controller.getLobby(), new AdventureCardMessage(METEOR_SWARM, coords_m.toString(), adventure));

                break;


            case Planets:

                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                next_p = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(next_p);

                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + next_p + " sta scegliendo se scendere su un PIANETA ! \n", next_p));

                sendToClient(getId_client(next_p), new AdventureCardMessage(PLANETS, "", adventure));
                break;


            case CombatZone:
                CombatZone combatZone = (CombatZone) adventure;
                controller.initializeAdventure(adventure);

                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                if (combatZone.getId() == 1) {


                    String nick_less_cw = controller.calculateLessCrewmates();

                    controller.movePlayer(getNickname(getId_client(nick_less_cw)), -controller.getCurrentAdventure().getCost_of_days());

                    sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "\nIL PLAYER " + nick_less_cw
                            + " HA PAGATO  per avere il MINOR NUMERO DI ASTRONAUTI , ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo" + "\n", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                    controller.initializeAdventure(adventure);
                    String curr_nick = controller.nextAdventurePlayer();
                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "engine", adventure));
                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la sua POTENZA MOTORE ! \n", curr_nick));


                } else if (combatZone.getId() == 0) {

                    controller.initializeAdventure(adventure);
                    String curr_nick = controller.nextAdventurePlayer();
                    controller.setCurr_adventure_player(curr_nick);

                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", adventure));
                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la sua POTENZA CANNONI ! \n", curr_nick));


                }

                break;


            case Smugglers:


                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                String curr_nick = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(curr_nick);

                sendToClient(getId_client(curr_nick), new AdventureCardMessage(SMUGGLERS, "", adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Contrabbandieri ! \n", curr_nick));
                break;


            case Pirates:

                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                curr_nick = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(curr_nick);

                coords_m = new StringBuilder();

                for (int k = 0; k < 2; k++) {

                    coords_m.append(throwDice()).append(" ");

                }
                controller.setPirates_coords(coords_m.toString());
                sendToClient(getId_client(curr_nick), new AdventureCardMessage(PIRATES, controller.getPirates_coords(), adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Pirati ! \n", curr_nick));
                break;


            case Slavers:

                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                curr_nick = controller.nextAdventurePlayer();
                controller.setCurr_adventure_player(curr_nick);

                sendToClient(getId_client(curr_nick), new AdventureCardMessage(SLAVERS, "", adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Schiavisti ! \n", curr_nick));
                break;


            case Epidemic:

                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(EPIDEMIC, "", adventure));
                manageAdventure(controller.getRandomAdventure(), controller);
                break;


            case Stardust:
                Stardust stardust = (Stardust) adventure;


                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                controller.executeStardust(stardust);
                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "TUTTI I PLAYER HANNO PERSO NUMERO DI VOLO in BASE AI LORO CONNETTORI ESPOSTI", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));
                manageAdventure(controller.getRandomAdventure(), controller);
                break;

        }


    }


    public String getNickname(UUID id) {
        ConnectionHandler client = clients.get(id);

        if(client == null) {
            client = clients.get(id);
        }
        return (client != null) ? client.getNickname() : null;
    }

    private UUID getId_client(String nickname) {
        for (Map.Entry<UUID, ConnectionHandler> entry : clients.entrySet()) {
            String handlerNickname = entry.getValue().getNickname();
            if (handlerNickname != null && handlerNickname.equals(nickname)) {
                return entry.getKey();
            }
        }
        for (Map.Entry<UUID, ConnectionHandler> entry : dis_clients.entrySet()) {
            String handlerNickname = entry.getValue().getNickname();
            if (handlerNickname != null && handlerNickname.equals(nickname)) {
                return entry.getKey();
            }
        }
        return null;
    }


    private void sendToAllClients(Lobby l, Message msg) {
        GameController controller = all_games.get(l.getLobbyId());
        for (String player : l.getPlayers()) {

            try {
                if(!controller.getDisconnected_players().contains(player)) {
                sendToClient(getId_client(player), msg);
                }
            } catch (NullPointerException ex) {
                System.out.println("un client è disconnesso, messaggio non inviato ! !");
            }
        }

    }

    private void sendToClient(UUID id, Message msg) {
        ConnectionHandler client = clients.get(id);
        if (client != null) {
            try {
                client.sendMessage(msg);
            } catch (IOException e) {
                System.err.println(" Errore nell'invio del messaggio a " + id);

            }
        }
    }


    private int getLobbyId(UUID id) {
        String nick = getNickname(id);
        if (nick == null) return -1;
        return manager.getAllLobbies().stream()
                .filter(lobby -> lobby.getPlayers().contains(nick))
                .map(Lobby::getLobbyId)
                .findFirst()
                .orElse(-1);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void startHeartbeat() {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<UUID, ConnectionHandler> entry : clients.entrySet()) {
                if (entry.getValue() instanceof RmiConnectionHandler ) {
                    try {
                        ((RmiConnectionHandler) entry.getValue()).getRemoteClient().ping();
                    } catch (RemoteException e) {
                        if(!disconnectedNames.contains(getNickname(entry.getKey()))) {
                            System.out.println("Client RMI disconnesso: " + entry.getKey());
                            handleClientDisconnection(entry.getKey());
                        }
                    }
                }
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    public void handleClientDisconnection(UUID clientId) {
        ConnectionHandler handler = clients.get(clientId);
        controller = all_games.get(getLobbyId(clientId));
        String nick = getNickname(clientId);

        Player player_disc = controller.getPlayer(nick);
        if (handler != null) {
            String nickname = handler.getNickname();
            controller.disconnect(nickname);

            if (nickname != null) {
                int lobbyId = getLobbyId(clientId);
                try {
                        controller.removeFromAdventure(nickname);

                } catch (Exception e) {
                    System.out.println("PARTITA ANCORA NON IN FASE DI VOLO");
                }
                disconnectedNames.add(nickname);

                if (controller.getCurr_adventure_player().equals(nickname)) {
                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Era il turno di  " + nick + ", che si è DISCONESSO -> salta il turno ! \n", nick));

                    switch (controller.getCurrentAdventure().getType()) {

                        case OpenSpace:
                            handleMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "0", clientId, player_disc));
                            break;

                        case AbandonedStation, AbandonedShip, Planets,MeteorSwarm:
                            handleMessage((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_disc)));
                            break;


                        case Pirates, Smugglers, Slavers:
                            handleMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "l", clientId, player_disc));
                            break;




                    }

                }

                if (lobbyId != -1) {
                    Lobby lobby = manager.getLobby(lobbyId);
                    if (lobby != null) {

                        Message disconnectMessage = new NotificationMessage(MessageType.PLAYER_DISCONNECTED, nickname + " si è disconnesso.", nickname);
                        sendToAllClients(lobby, disconnectMessage);
                    }
                }
                if(controller.getDisconnected_players().size() == controller.getActivePlayers().size()) {

                    for(Player p : controller.getActivePlayers()){

                        disconnectedNames.remove(p.getNickname());
                    }

                }
                System.out.println("Client " + nickname + " rimosso.");
            }
        }
    }


    public void handleClientReconnection(UUID clientId) {
        controller = all_games.get(getLobbyId(clientId));
        controller.reConnect(getNickname(clientId));
        disconnectedNames.remove(getNickname(clientId));
        Player player = null;
        List<Player> safePlayers = new ArrayList<>();
        for (Player p : controller.getPlayers()) {
            safePlayers.add(p.copyPlayer());
            if (p.getNickname().equals(getNickname(clientId))) {
                player = p.copyPlayer();
            }
        }


        for(Map.Entry<UUID, ConnectionHandler> entry : clients.entrySet()) {
            String nickname = getNickname(entry.getKey());
            if(nickname != null && nickname.equals(player.getNickname()) && !entry.getKey().equals(clientId)) {
                clients.remove(entry.getKey());
            }
        }
        sendToClient(clientId, new ShipClientMessage(UTIL, "", clientId, player));
        sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
        String nick = getNickname(clientId);
        switch (controller.getGamestate()) {

            case BUILD_PHASE:

                sendToClient(clientId, new ShipClientMessage(MessageType.BUILD_START, "", clientId, player));

                sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                for (CardComponent card : controller.getFacedUpCards()) {
                    sendToClient(clientId, new CardComponentMessage(MessageType.FACED_UP_CARD_UPDATED, "", clientId, card));
                }
                break;

            case SUPLLY_PHASE:
                sendToClient(clientId, new ShipClientMessage(ADD_CREWMATES, "", clientId, player));
                break;
            case FIXING_SHIPS:


                List<Pair<Integer, Integer>> invalids_connections;


                invalids_connections = controller.checkShipConnectors(getNickname(clientId));


                sendToClient(clientId, new InvalidConnectorsMessage(INVALID_CONNECTORS, "", invalids_connections));
                break;


            case FLYING_PHASE:

                sendToClient(clientId, new BoardMessage(UPDATE_BOARD, "", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                break;


        }

        if (controller.getIn_pause() == 1) {
            controller.setIn_pause(0);
            manageAdventure(controller.getRandomAdventure(), controller);
        }


    }
}

