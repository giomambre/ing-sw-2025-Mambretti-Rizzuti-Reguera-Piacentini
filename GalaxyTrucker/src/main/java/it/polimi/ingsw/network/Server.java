package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.model.Game;
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
import static it.polimi.ingsw.network.Client.throwDice;
import static it.polimi.ingsw.network.messages.MessageType.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<String> connectedNames = new HashSet<>();
    private static Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final Map<UUID, ClientHandler> clients = new HashMap<>();
    private GameManager manager = new GameManager();
    private Map<Integer, GameController> all_games = new HashMap<>();
    GameController controller;
    public  String util_string = "";
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
                    if (requestedName=="") {
                        sendToClient(msgClient.getId_client(), new StandardMessageClient(MessageType.NAME_REJECTED, "❌ Stringa vuota non accettata. Inserisci un altro nickname.", msgClient.getId_client()));
                        break;
                    }
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

                        sendToClient(msgClient.getId_client(), new GameStartedMessage(MessageType.GAME_STARTED, " COLORE GIA PRESO", controller.getAvailable_colors()));

                    }


                    System.out.println(controller.getAvailable_colors());
                    if (4 - controller.getAvailable_colors().size() == controller.getLobby().getPlayers().size()) {
                        System.out.println("Tutti i player hanno scelto i colori fase di costruzione iniziata!");


                        controller.startGame();
                        LobbyTimer lt = new LobbyTimer(controller.getLobby().getLimit(), controller.getLobby());

                        lobbyTimers.put(controller.getLobby().getLobbyId(), lt);

                        // Invia BUILD_START

                        sendToAllClients(controller.getLobby(), new Message(MessageType.BUILD_START, "Hai 120s per costruire"));

                        // Avvia 120s

                        lt.start120(() -> lt.handle120End(
                                // callback per quando scadono i 120s E qualcuno ha già finito
                                () -> sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE, "⏳ 30s rimanenti")),
                                // callback per quando scadono i 120s E NESSUNO ha ancora finito
                                () -> sendToAllClients(lt.getLobby(), new Message(MessageType.TIME_UPDATE, "⏱ 120 secondi terminati : in attesa del primo giocatore..."))
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
                            System.out.println("mandato");
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
                System.out.println("FINISHED ORDER" + lt.getFinishOrder());
                break;


            case ADD_CREWMATES:

                AddCrewmateMessage addC_msg = (AddCrewmateMessage) msg;
                controller = all_games.get(getLobbyId(addC_msg.getId_client()));

                controller.crewmatesSupply(getNickname(addC_msg.getId_client()), addC_msg.getPos().getKey(), addC_msg.getPos().getValue(), addC_msg.getCmType());

                break;

            case CHECK_SHIPS:


                msgClient = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(msgClient.getId_client()));
                controller.finishSupplyPhase(getNickname(msgClient.getId_client()));
                List<Pair<Integer, Integer>> invalids_connections = new ArrayList<>();

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
                    safePlayers.add(p.copyPlayer());  // funzione che crea una "safe copy"
                }
                sendToClient(update_msg.getId_client(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                System.out.println(getNickname(update_msg.getId_client()) + " " + controller.getValidPieces(getNickname(update_msg.getId_client())).size());

                if (controller.getValidPieces(getNickname(update_msg.getId_client())).size() > 1) {

                    List<List<Pair<Integer, Integer>>> pieces = controller.getValidPieces(getNickname(update_msg.getId_client()));
                    sendToClient(update_msg.getId_client(), new ShipPiecesMessage(SELECT_PIECE, "", pieces));
                } else if (controller.getValidPieces(getNickname(update_msg.getId_client())).size() == 1) {

                    safePlayers = new ArrayList<>();
                    for (Player p : controller.getPlayers()) {
                        safePlayers.add(p.copyPlayer());  // funzione che crea una "safe copy"
                    }
                    sendToClient(update_msg.getId_client(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));
                    sendToClient(update_msg.getId_client(), new Message(WAITING_FLIGHT, ""));
                    controller.addWaitingFlyPlayer(getNickname(update_msg.getId_client()));


                } else if (controller.getValidPieces(getNickname(update_msg.getId_client())).isEmpty()) {
                    sendToClient(update_msg.getId_client(), new Message(INVALID_SHIP, ""));
                    controller.removePlayerFromOrder(getNickname(update_msg.getId_client()));

                }


                if (controller.getWaitingFlyPlayers().size() == controller.getBuild_order_players().size()) {

                    handleMessage(new StandardMessageClient(START_FLIGHT, "", update_msg.getId_client()));

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
                controller.addWaitingFlyPlayer(getNickname(select_msg.getId_client()));

                if (controller.getWaitingFlyPlayers().size() == controller.getBuild_order_players().size()) {

                    handleMessage(new StandardMessageClient(START_FLIGHT, "", select_msg.getId_client()));

                }

                break;


            case START_FLIGHT:
                StandardMessageClient start_msg = (StandardMessageClient) msg;
                controller = all_games.get(getLobbyId(start_msg.getId_client()));
                controller.startFlight();
                sendToAllClients(controller.getLobby(), new Message(START_FLIGHT, ""));
                sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                //CardAdventure adventure = controller.getRandomAdventure();


                /*adventure = new Smugglers(2, 1, CardAdventureType.Smugglers, 8,
                        Arrays.asList(
                                Cargo.Red,
                                Cargo.Yellow,
                                Cargo.Yellow
                        ),
                        3,"");*/
                CardAdventure adventure = new OpenSpace(1, 3, CardAdventureType.OpenSpace, "/images/cardAdventure/GT-openSpace_2.1.jpg");

                /*adventure = new Pirates(1, 1, CardAdventureType.Pirates, 5, 4,
                        List.of(
                                new Pair<>(MeteorType.LightCannonFire, North),
                                new Pair<>(MeteorType.HeavyCannonFire, North),
                                new Pair<>(MeteorType.LightCannonFire, North)
                        ),"/images/cardAdventure/GT-pirates_1.jpg"
                );*/

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
                Ship s = adv_msg.getPlayer().getShip();
                controller.setShipPlance(getNickname(adv_msg.getId_client()), s);
                safePlayers = new ArrayList<>();
                for (Player p : controller.getPlayers()) {
                    safePlayers.add(p.copyPlayer());
                }
                sendToAllClients(controller.getLobby(), new PlayersShipsMessage(MessageType.UPDATED_SHIPS, "", safePlayers));

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

                            sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(OPEN_SPACE, "", controller.getCurrentAdventure()));

                        }

                        break;


                    case AbandonedStation:

                        ShipClientMessage abandoned_msg = (ShipClientMessage) msg;

                        if (abandoned_msg.getContent().isEmpty()) {

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {

                                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(ABANDONED_STATION, "", controller.getCurrentAdventure()));


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

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {

                                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(ABANDONED_SHIP, "", controller.getCurrentAdventure()));


                            }

                        } else {
                            controller.movePlayer(getNickname(adv_msg.getId_client()), -controller.getCurrentAdventure().getCost_of_days());

                            sendToAllClients(controller.getLobby(), new BoardMessage(UPDATE_BOARD, "IL PLAYER " + getNickname(adv_msg.getId_client())
                                    + " HA ACCETTATO L'AVVENTURA, ha perso :  " + controller.getCurrentAdventure().getCost_of_days() + " giorni di volo", controller.getBoard().copyPlayerPositions(), controller.getBoard().copyLaps()));

                            adventure = controller.getRandomAdventure();
                            manageAdventure(adventure, controller);


                        }
                        break;


                    case MeteorSwarm:
                        ShipClientMessage meteor_msg = (ShipClientMessage) msg;

                        controller.removeFromAdventure(getNickname(meteor_msg.getId_client()));

                        if (controller.getActivePlayers().size() <= 1) {


                            System.out.println("GIOCO FINITO DA GESTIRE!!!");

                        }


                        if (controller.getAdventureOrder().isEmpty()) {
                            adventure = controller.getRandomAdventure();
                            manageAdventure(adventure, controller);
                        }

                        break;


                    case Planets:
                        ShipClientMessage planet_msg = (ShipClientMessage) msg;
                        if (planet_msg.getContent().isEmpty()) {

                            if (controller.getAdv_index() >= controller.getAdventureOrder().size()) {

                                adventure = controller.getRandomAdventure();
                                manageAdventure(adventure, controller);


                            } else {

                                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(PLANETS, controller.getPlanets(), controller.getCurrentAdventure()));

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
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza cannoni ! \n", curr_nick));


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
                                        sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza motori ! \n", curr_nick));


                                        break;

                                    }

                                    curr_nick = controller.nextAdventurePlayer();
                                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", controller.getCurrentAdventure()));
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza cannoni ! \n", curr_nick));


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
                                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza motore ! \n", curr_nick));

                                    break;
                            }


                        }


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
                                    return;


                                }
                                curr_nick = controller.nextAdventurePlayer();
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + getNickname(adv_msg.getId_client()) + " HA PERSO contro i  PIRATI ! \n", getNickname(adv_msg.getId_client())));
                                sendToClient(getId_client(curr_nick), new AdventureCardMessage(PIRATES, controller.getPirates_coords(), controller.getCurrentAdventure()));
                                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Pirati ! \n", curr_nick));
                                break;


                        }
                        sendToAllClients(controller.getLobby(),new PlayersShipsMessage(UPDATED_SHIPS,"",controller.getActivePlayers()));

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

            }

        }


        switch (adventure.getType()) {


            case OpenSpace:

                controller.initializeAdventure(adventure);

                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));

                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(OPEN_SPACE, "", adventure));

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

                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(ABANDONED_STATION, "", adventure));
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

                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(ABANDONED_SHIP, "", adventure));
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
                sendToClient(getId_client(controller.nextAdventurePlayer()), new AdventureCardMessage(PLANETS, "", adventure));
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
                    //gestione potenza di fuoco
                    String curr_nick = controller.nextAdventurePlayer();
                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "engine", adventure));
                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza motore ! \n", curr_nick));


                } else if (combatZone.getId() == 0) {

                    controller.initializeAdventure(adventure);
                    String curr_nick = controller.nextAdventurePlayer();

                    sendToClient(getId_client(curr_nick), new AdventureCardMessage(COMBAT_ZONE, "cannon", adventure));
                    sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta dichiarando la potenza cannoni ! \n", curr_nick));


                }

                break;


            case Smugglers:

                Smugglers smugglers = (Smugglers) adventure;
                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                String curr_nick = controller.nextAdventurePlayer();
                sendToClient(getId_client(curr_nick), new AdventureCardMessage(SMUGGLERS, "", adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Contrabbandieri ! \n", curr_nick));
                break;


            case Pirates:
                Pirates pirates = (Pirates) adventure;
                controller.initializeAdventure(adventure);
                sendToAllClients(controller.getLobby(), new AdventureCardMessage(NEW_ADVENTURE_DRAWN, "", adventure));
                curr_nick = controller.nextAdventurePlayer();
                 coords_m = new StringBuilder();

                for (int k = 0; k < 2; k++) {

                    coords_m.append(throwDice()).append(" ");

                }
                controller.setPirates_coords(coords_m.toString());
                sendToClient(getId_client(curr_nick), new AdventureCardMessage(PIRATES, controller.getPirates_coords(), adventure));
                sendToAllClients(controller.getLobby(), new NotificationMessage(NOTIFICATION, "Il player " + curr_nick + " sta affrontando i nemici Pirati ! \n", curr_nick));
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

