package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.AbandonedShip;
import it.polimi.ingsw.model.adventures.AbandonedStation;
import it.polimi.ingsw.model.adventures.CardAdventure;
import it.polimi.ingsw.model.adventures.OpenSpace;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.Cargo;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import it.polimi.ingsw.model.enumerates.Direction;
import it.polimi.ingsw.view.GUI.Buildcontroller;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.GUI.GuiApplication;
import it.polimi.ingsw.view.TUI.TUI;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.network.messages.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.util.Pair;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.IOException;
import java.net.Socket;

import java.io.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;

public class Client {
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static String nickname;
    private static View virtualView;
    private static VirtualViewType virtualViewType;
    private static UUID clientId;
    private static BlockingQueue<Message> inputQueue = new LinkedBlockingQueue<>();
    private static BlockingQueue<Message> notificationQueue = new LinkedBlockingQueue<>();
    private static List<Player> other_players_local = new ArrayList<>();
    private static GameState gameState;
    private static boolean forced_close = false;

    private static Map<Integer, Player> local_board_positions;
    private static Map<Integer, Player> local_board_laps;

    private static Player player_local;
    private static List<CardComponent> facedUp_deck_local = new ArrayList<>();
    private static Map<Direction, List<CardAdventure>> local_adventure_deck = new HashMap<>();
    private static List<Color> still_Available_colors = new ArrayList<>();
    private static CompletableFuture<Void> otherPlayersReady = new CompletableFuture<>();

    public static void setOtherPlayersLocal(List<Player> players) {
        other_players_local = players;
        otherPlayersReady.complete(null); // Sblocca l’attesa
    }

    public static void setNickname(String nickname) {
        Client.nickname = nickname;
    }


    public static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            Message response = (Message) in.readObject();
            if (response.getType() == MessageType.ASSIGN_UUID) {
                clientId = ((StandardMessageClient) response).getId_client();
                System.out.println("✅ Connesso con UUID: " + clientId);
            }
            int choice = -1;

            do {
                System.out.println("Inserisci 1 per la TUI 2 per la GUI : ");
                choice = scanner.nextInt();

            } while (choice != 1 && choice != 2);

            if (choice == 1) {
                virtualView = new TUI();
                virtualViewType = VirtualViewType.TUI;

            } else {
                GuiApplication.setClient(new Client());
                new Thread(() -> Application.launch(GuiApplication.class)).start();
                while (GuiApplication.getGui() == null) {
                    Thread.sleep(50);
                }
                virtualView = GuiApplication.getGui();
                virtualViewType = VirtualViewType.GUI;

            }
            still_Available_colors.add(Color.BLUE);
            still_Available_colors.add(Color.RED);
            still_Available_colors.add(Color.YELLOW);
            still_Available_colors.add(Color.GREEN);

            new Thread(() -> {
                try {
                    while (true) {

                        Message msg = (Message) in.readObject();

                        switch (msg.getType()) {
                            case ABANDONED_SHIP,ABANDONED_STATION, REQUEST_NAME, NAME_REJECTED, NAME_ACCEPTED,
                                 CREATE_LOBBY, SEE_LOBBIES, SELECT_LOBBY, GAME_STARTED, BUILD_START,
                                 CARD_COMPONENT_RECEIVED,
                                 CARD_UNAVAILABLE, UNAVAILABLE_PLACE, ADD_CREWMATES, INVALID_CONNECTORS, SELECT_PIECE:
                                inputQueue.put(msg);
                                break;

                            case ADVENTURE_SKIP, NEW_ADVENTURE_DRAWN, UPDATE_BOARD, WAITING_FLIGHT, INVALID_SHIP,
                                 START_FLIGHT, FORCE_BUILD_PHASE_END, COLOR_SELECTED, DISMISSED_CARD,
                                 FACED_UP_CARD_UPDATED, UPDATED_SHIPS, DECK_CARD_ADVENTURE_UPDATED, TIME_UPDATE,
                                 BUILD_PHASE_ENDED:
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void elaborate(Message msg) throws IOException {


        switch (msg.getType()) {

            case REQUEST_NAME, NAME_REJECTED:  //send the nickname request to the server with his UUID

                if (msg.getType()==MessageType.NAME_REJECTED){
                    virtualView.showMessage("\n username già utilizzato.");
            }

                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).setClientCallback(nickname -> {
                        try {
                            setNickname(nickname);
                            out.writeObject(new StandardMessageClient(MessageType.SENDED_NAME, nickname, clientId));
                            out.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    ((GUI) virtualView).createNicknamescreen();

                } else {
                    nickname = virtualView.askNickname();

                    try {
                        out.writeObject(new StandardMessageClient(MessageType.SENDED_NAME, nickname, clientId));
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

            case NAME_ACCEPTED:
                int join_or_create;
                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).createjoingamecontroller();
                    join_or_create = virtualView.askCreateOrJoin();
                } else {
                    join_or_create = virtualView.askCreateOrJoin();
                }
                Message to_send;
                if (join_or_create == 1) {
                    int num;
                    if (virtualViewType == VirtualViewType.GUI) {
                        ((GUI) virtualView).createnumplayerscontroller();
                        num = virtualView.askNumPlayers();
                    } else {
                        num = virtualView.askNumPlayers();
                    }
                    if (num == -1) {
                        elaborate(new Message(MessageType.NAME_ACCEPTED, ""));
                        break;
                    }
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
                System.out.println("(testing)sono nella creazione della lobby");
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Errore nella creazione della lobby, riprovare\n");
                    elaborate(new Message(MessageType.NAME_ACCEPTED, ""));
                    break;
                } else {


                }
                break;

            case SEE_LOBBIES:
                System.out.println("(testing)ora guardo le lobby cge ci sono");
                AvaiableLobbiesMessage l_msg = (AvaiableLobbiesMessage) msg;

                if (!msg.getContent().isEmpty()) {
                    virtualView.showMessage("\n" + msg.getContent());
                }

                if (l_msg.getLobbies().size() == 0) {

                    virtualView.showMessage("\nNon ci sono Lobby disponibili!");
                    elaborate(new Message(MessageType.NAME_ACCEPTED, ""));
                    break;
                } else {
                    int lobby_index;
                    if (virtualViewType == VirtualViewType.GUI) {
                        ((GUI) virtualView).createselectlobbyscreen(l_msg.getLobbies());
                        lobby_index = virtualView.showLobbies(l_msg.getLobbies());
                        System.out.println("(testing) lobby scelta:" + lobby_index);
                    } else {
                        lobby_index = virtualView.showLobbies(l_msg.getLobbies());
                    }
                    out.writeObject(new StandardMessageClient(MessageType.SELECT_LOBBY, "" + lobby_index, clientId));

                }
                break;

            case SELECT_LOBBY:
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Lobby selezionata non disponinbile, riprovare");
                    elaborate(new Message(MessageType.SEE_LOBBIES, ""));
                    break;
                } else {

                    virtualView.showMessage("\nSei entrato nella lobby" + msg.getContent());

                }
                break;

            case GAME_STARTED:
                GameStartedMessage gs_msg = (GameStartedMessage) msg;
                if (gs_msg.getContent().isEmpty()) {
                    virtualView.showMessage("\nPartita avviata!");
                }
                Color c;
                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).createchoosecolorscreen(still_Available_colors);
                    c = virtualView.askColor(still_Available_colors);
                    out.writeObject(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                    System.out.println("(testing) color chosen:" + c);
                } else {
                    c = virtualView.askColor(gs_msg.getAvailableColors());
                    out.writeObject(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                }
                //out.writeObject(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                break;

            case BUILD_START:
                if (virtualViewType == VirtualViewType.GUI) {
                    try {
                        otherPlayersReady.get(); // Attende finché non è completa
                        ((GUI) virtualView).createbuildscreen();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                int deck_selected = virtualView.selectDeck();

                if (deck_selected == 1) {
                    out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, "", clientId));

                } else if (deck_selected == 2) {

                    if (facedUp_deck_local.isEmpty()) {
                        virtualView.showMessage("\nNon ci sono carte a faccia in alto!\n");
                        elaborate(new Message(MessageType.BUILD_START, ""));

                    } else {

                        int index = virtualView.askFacedUpCard(facedUp_deck_local);
                        if (index == -1) {
                            elaborate(new Message(MessageType.BUILD_START, ""));
                            break;
                        }
                        UUID selectedCardId = facedUp_deck_local.get(index).getCard_uuid();
                        out.writeObject(new StandardMessageClient(MessageType.ASK_CARD, selectedCardId.toString(), clientId));


                    }
                } else if (deck_selected == 3) {  //carte prenotate

                    if (player_local.getShip().getExtra_components().isEmpty()) {
                        virtualView.showMessage("\nNon ci sono carte prenotate!");
                        elaborate(new Message(MessageType.BUILD_START, ""));
                        break;
                    } else {


                        int index = virtualView.askSecuredCard(player_local.getShip().getExtra_components());
                        if (index == -1) {
                            elaborate(new Message(MessageType.BUILD_START, ""));
                        } else if (index >= 0 && index < player_local.getShip().getExtra_components().size()) {
                            elaborate(new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", clientId, player_local.getShip().getExtra_components().get(index)));
                        }

                    }


                } else if (deck_selected == 4) {
                    virtualView.showMessage("\nHai dichiarato di aver terminato l'assemblaggio!");
                    out.writeObject(new StandardMessageClient(MessageType.BUILD_PHASE_ENDED, "", clientId));
                }

                break;

            case CARD_UNAVAILABLE:
                virtualView.showMessage("\nLa carta richiesta non è più disponibile ! ");
                elaborate(new Message(MessageType.BUILD_START, ""));
                break;

            case UNAVAILABLE_PLACE:
                virtualView.showMessage("\nNon puoi posizionare la carta in questa fase del gioco ! ");
                break;

            case CARD_COMPONENT_RECEIVED:

                CardComponentMessage card_msg = (CardComponentMessage) msg;
                virtualView.showMessage("\nCarta disponibile");
                int sel;
                if (virtualViewType == VirtualViewType.GUI) {

                }
                sel = virtualView.showCard(card_msg.getCardComponent());

                if (sel == 1) {

                    CardComponent card = card_msg.getCardComponent();
                    card.rotate();

                    elaborate(new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", clientId, card));
                    return;
                }

                if (sel == 3) {

                    if (player_local.getShip().getExtra_components().contains(card_msg.getCardComponent())) {
                        virtualView.showMessage("\nCarta rimessa nelle carte prenotate");
                        elaborate(new Message(MessageType.BUILD_START, ""));

                        return;
                    }

                    out.writeObject(new CardComponentMessage(MessageType.DISMISSED_CARD, "", clientId, card_msg.getCardComponent()));
                    elaborate(new Message(MessageType.BUILD_START, ""));
                    break;
                }
                if (sel == 2) {

                    Pair<Integer, Integer> coords = virtualView.askCoords(player_local.getShip());
                    if (coords.getKey() == -1 || coords.getValue() == -1) {
                        elaborate(new Message(MessageType.BUILD_START, ""));
                        break;
                    } else {

                        out.writeObject(new CardComponentMessage(MessageType.PLACE_CARD, coords.getKey() + " " + coords.getValue(), clientId, card_msg.getCardComponent()));
                        player_local.addToShip(card_msg.getCardComponent(), coords.getKey(), coords.getValue());

                        elaborate(new Message(MessageType.BUILD_START, ""));
                        player_local.getShip().getExtra_components().remove(card_msg.getCardComponent());
                        break;

                    }

                }
                if (sel == 4) {

                    if (player_local.getShip().getExtra_components().size() == 2) {
                        virtualView.showMessage("\nSpazio esaurito nelle carte prenotate");

                    } else {
                        virtualView.showMessage("\nCarta aggiunta tra le carte prenotate");

                        player_local.getShip().getExtra_components().add(card_msg.getCardComponent());

                    }


                }

                elaborate(new Message(MessageType.BUILD_START, ""));
                break;


            case ADD_CREWMATES:
                inputQueue.clear();
                CardComponent[][] plance = player_local.getShip().getShip_board();
                Pair<Integer, Integer> coords;
                for (int i = 0; i < player_local.getShip().getROWS(); i++) {
                    for (int j = 0; j < player_local.getShip().getCOLS(); j++) {
                        CardComponent component = plance[i][j];
                        coords = new Pair<>(i, j);
                        if (component.getComponentType() == LivingUnit) {

                            CrewmateType type;
                            int select = virtualView.crewmateAction(coords);

                            if (select == 1) {
                                type = CrewmateType.Astronaut;
                                ((LivingUnit) component).addAstronauts();

                            } else if (select == 2) {

                                type = CrewmateType.PinkAlien;
                                ((LivingUnit) component).addAlien(CrewmateType.PinkAlien);


                            } else {

                                type = CrewmateType.BrownAlien;
                                ((LivingUnit) component).addAlien(CrewmateType.BrownAlien);


                            }
                            out.writeObject(new AddCrewmateMessage(MessageType.ADD_CREWMATES, "", clientId, coords, type));


                        }

                    }
                }
                System.out.println("HO " + player_local.getShip().getNumOfCrewmates());
                out.writeObject(new StandardMessageClient(MessageType.CHECK_SHIPS, "", clientId));


                break;

            case INVALID_CONNECTORS:
                InvalidConnectorsMessage icm = (InvalidConnectorsMessage) msg;
                if (icm.getInvalids().isEmpty()) {

                    virtualView.showMessage("\n Tutti i connettori sono disposti in maniera giusta, si passa al prossimo controllo");
                    out.writeObject(new ShipClientMessage(MessageType.FIXED_SHIP_CONNECTORS, "", clientId, player_local.copyPlayer()));

                } else {
                    player_local.setShip(virtualView.removeInvalidsConnections(player_local.getShip(), icm.getInvalids()));
                    out.writeObject(new ShipClientMessage(MessageType.FIXED_SHIP_CONNECTORS, "", clientId, player_local.copyPlayer()));

                }

                break;


            case SELECT_PIECE:

                ShipPiecesMessage spm = (ShipPiecesMessage) msg;
                List<List<Pair<Integer, Integer>>> pieces = spm.getPieces();
                int piece = virtualView.askPiece(pieces, player_local.getShip().getShipBoard());
                out.writeObject(new StandardMessageClient(MessageType.SELECT_PIECE, String.valueOf(piece), clientId));
                break;


            default:

                AdventureCardMessage adv = (AdventureCardMessage) msg;
                manageAdventure(adv.getAdventure());
                break;
        }


    }


    public static void handleNotification(Message msg) throws IOException {


        switch (msg.getType()) {


            case COLOR_SELECTED:
                gameState = GameState.BuildingPhase;
                String[] parts = msg.getContent().split(" ");
                try {
                    Color chosen = Color.valueOf(parts[1]);
                    still_Available_colors.remove(chosen);
                } catch (IllegalArgumentException e) {
                    System.out.println("Errore: colore non valido " + parts[1]);
                }
                if (parts[0].equals(nickname)) {
                    virtualView.showMessage("\nHai scelto il colore : " + parts[1]);
                    /*if(virtualViewType==VirtualViewType.GUI){
                        elaborate(new Message(MessageType.BUILD_START,""));
                    }*/
                } else {
                    // virtualView.showMessage("\nIl player " + parts[0] + " ha scelto il colore : " + parts[1]);
                    if (virtualViewType == VirtualViewType.GUI) {
                        virtualView.showMessage("\nIl player " + parts[0] + " ha scelto il colore : " + parts[1]);
                        ((GUI) virtualView).updateColors(still_Available_colors);
                        /* elaborate(new GameStartedMessage(MessageType.GAME_STARTED,"",still_Available_colors));*/
                        break;
                    }

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
                if (virtualViewType == VirtualViewType.GUI) {
                    if (!Client.otherPlayersReady.isDone()) {
                        Client.otherPlayersReady.complete(null);
                        System.out.println(">>> [DEBUG] CompletableFuture completata con altri giocatori: " + other_players_local);
                    }
                }
                if (virtualViewType == VirtualViewType.TUI) {
                    ((TUI) virtualView).setPlayer_local(player_local);
                    ((TUI) virtualView).setOther_players_local(other_players_local);
                    ((TUI) virtualView).setLocal_extra_components(player_local.getShip().getExtra_components());
                }
                break;


            case ADVENTURE_SKIP:
                virtualView.showMessage("\n----NESSUNO HA POTUTO PARTECIPARE A QUESTA AVVENTURA SI PASSA ALLA PROSSIMA----\n");
                break;
            case FACED_UP_CARD_UPDATED:
                CardComponentMessage cpm = (CardComponentMessage) msg;
                if (facedUp_deck_local.stream().noneMatch(c -> c.getCard_uuid().equals(cpm.getCardComponent().getCard_uuid()))) {
                    facedUp_deck_local.add(cpm.getCardComponent());


                } else {
                    facedUp_deck_local.remove(cpm.getCardComponent());


                }

                break;

            case FORCE_BUILD_PHASE_END:
                virtualView.showMessage("\n" + "Per favore dichiara di aver finito per continuare, qualunque carta piazzata/prenotata/scartata, verrà ignorata");

                elaborate(new Message(MessageType.BUILD_PHASE_ENDED, msg.getContent()));
                break;


            case DECK_CARD_ADVENTURE_UPDATED:

                CardAdventureDeckMessage adm = (CardAdventureDeckMessage) msg;
                local_adventure_deck = adm.getDeck();
                if (virtualViewType == VirtualViewType.TUI) {
                    ((TUI) virtualView).setLocal_adventure_deck(local_adventure_deck);
                }
                break;

            case TIME_UPDATE:

                virtualView.showMessage("\n" + msg.getContent());

                break;

            case BUILD_PHASE_ENDED:
                switch (msg.getContent()) {
                    case "0":
                        virtualView.showMessage("\nHai terminato la costruzione della nave per primo");
                        break;
                    case "1":
                        virtualView.showMessage("\nHai terminato la costruzione della nave per secondo");
                        break;
                    case "2":
                        virtualView.showMessage("\nHai terminato la costruzione della nave per terzo");
                        break;
                    case "3":
                        virtualView.showMessage("\nHai terminato la costruzione della nave per quarto");
                        break;
                }


                break;


            case WAITING_FLIGHT:
                virtualView.showMessage("\nHai completato la fase di controllo ora rimani in attesa degli altri giocatori.\n" +
                        "Questa è la tua nave.\n ");

                virtualView.printShip(player_local.getShip().getShipBoard());

                virtualView.showMessage("\n\n\tRIMANI IN ATTESA CHE GLI ALTRI PLAYER FINISCANO IL CONTROLLO !");
                break;


            case UPDATE_BOARD:
                BoardMessage bm = (BoardMessage) msg;
                local_board_laps = bm.getLaps();
                local_board_positions = bm.getPositions();

                if (!bm.getContent().isEmpty()) {
                    virtualView.showMessage(bm.getContent());
                    virtualView.showBoard(local_board_positions, local_board_laps);
                    System.out.println("\n\n");


                }


                if (virtualViewType == VirtualViewType.TUI) {
                    ((TUI) virtualView).setLocal_board_position(local_board_positions);
                    ((TUI) virtualView).setLocal_board_laps(local_board_positions);


                }
                break;


            case INVALID_SHIP:
                virtualView.showMessage("\nSEI STATO ESCLUSO DAL GIOCO, motivo : " + msg.getContent());
                System.exit(0);


                break;

            case START_FLIGHT:
                virtualView.showMessage("\n\n\n\n---------------   INIZIO FASE DI VOLO   ---------------");
                break;


            case NEW_ADVENTURE_DRAWN:
                AdventureCardMessage ad = (AdventureCardMessage) msg;
                virtualView.printCardAdventure(ad.getAdventure());
                System.out.println();
                break;


            case OPEN_SPACE, ABANDONED_STATION:
                AdventureCardMessage ac = (AdventureCardMessage) msg;
                manageAdventure(ac.getAdventure());
                break;


        }


    }


    public static void manageAdventure(CardAdventure adventure) throws IOException {

        switch (adventure.getType()) {


            case OpenSpace:
                OpenSpace openSpace = (OpenSpace) adventure;
                Ship ship = player_local.getShip();
                Map<CardComponent, Boolean> battery_usage = new HashMap<>();
                Pair<Integer, Integer> battery;
                for (int i = 0; i < ship.getROWS(); i++) {
                    for (int j = 0; j < ship.getCOLS(); j++) {
                        CardComponent card = ship.getComponent(i, j);

                        if (card.getComponentType() == DoubleEngine) {

                            battery = virtualView.askEngine(new Pair<>(i, j));
                            if (battery.getKey() == -1 || battery.getValue() == -1) {

                                battery_usage.put(card, false);

                            } else {


                                battery_usage.put(card, true);
                                //DA GESTIRE L UTILIZZO DI BATTERIE
                            }

                        }

                    }
                }

                int power = ship.calculateEnginePower(battery_usage);
                System.out.println("\n\n\nPOTENZA MOTORE " + power);

                out.writeObject(new StandardMessageClient(MessageType.ADVENTURE_COMPLETED, String.valueOf(power), clientId));

                break;


            case AbandonedStation:

                Boolean choice = virtualView.acceptAdventure();
                AbandonedStation a_s = (AbandonedStation) adventure;
                Pair<Pair<Integer, Integer>, Integer> new_position;

                List<Cargo> cargos = a_s.getCargo();

                if (choice) {


                    while (true) {

                        int scelta = virtualView.askCargo(cargos);

                        if (scelta == -1) {
                            break;
                        }

                        Cargo c = cargos.get(scelta);


                        new_position = virtualView.addCargo(player_local.getShip(), c);
                        if (new_position != null) {
                            ship = player_local.getShip();
                            cargos.remove(scelta);
                            Storage s = ((Storage) ship.getComponent(new_position.getKey().getKey(), new_position.getKey().getValue()));
                            s.addCargo(c, new_position.getValue());

                        }
                    }
                } else {

                    out.writeObject((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local)));

                }

                out.writeObject((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "adv done", clientId, player_local)));


                break;


            case AbandonedShip:

                 choice = virtualView.acceptAdventure();
                AbandonedShip ab_ship = (AbandonedShip) adventure;

                if (choice) {

                 int num_crew_mates =ab_ship.getCrewmates_loss();


                 while(num_crew_mates != 0){

                    Pair <Integer,Integer> lu = virtualView.chooseAstronautLosses(player_local.getShip());
                    if(lu.getValue() == -1 || lu.getKey() == -1) continue;
                    else {
                        num_crew_mates--;
                        virtualView.showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");
                    }


                    }




                } else {

                    out.writeObject((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local)));

                }

                out.writeObject((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "adv done", clientId, player_local)));



        }


    }


    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public String getNickname() {
        return nickname;
    }

    public static View getVirtualView() {
        return virtualView;
    }

    public UUID getClientId() {
        return clientId;
    }

    public BlockingQueue<Message> getInputQueue() {
        return inputQueue;
    }

    public BlockingQueue<Message> getNotificationQueue() {
        return notificationQueue;
    }

    public List<Player> getOther_players_local() {
        return other_players_local;
    }

    public Player getPlayer_local() {
        return player_local;
    }

    public List<CardComponent> getFacedUp_deck_local() {
        return facedUp_deck_local;
    }

    public void setVirtualViewType(VirtualViewType virtualViewType) {
        this.virtualViewType = virtualViewType;
    }

    public VirtualViewType getVirtualViewType() {
        return virtualViewType;
    }


}
