package it.polimi.ingsw.network;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.Battery;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.view.GUI.Buildcontroller;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.GUI.GuiApplication;
import it.polimi.ingsw.view.TUI.TUI;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.network.messages.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.util.Pair;
import it.polimi.ingsw.model.enumerates.CrewmateType;
import java.io.IOException;
import java.net.Socket;

import java.io.*;

import java.rmi.NotBoundException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Direction.South;

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
    private static CountDownLatch adventureLatch = new CountDownLatch(1);
    private static CountDownLatch boardLatch = new CountDownLatch(1);


    private static Map<Integer, Player> local_board_positions;
    private static Map<Integer, Player> local_board_laps;

    private static Player player_local;
    private static List<CardComponent> facedUp_deck_local = new ArrayList<>();
    private static Map<Direction, List<CardAdventure>> local_adventure_deck = new HashMap<>();
    private static List<Color> still_Available_colors = new ArrayList<>();
    private static CompletableFuture<Void> otherPlayersReady = new CompletableFuture<>();
    private static NetworkAdapter networkAdapter = null;


    public static void setOtherPlayersLocal(List<Player> players) {
        other_players_local = players;
        otherPlayersReady.complete(null); // Sblocca l’attesa
    }

    public static void setNickname(String nickname) {
        Client.nickname = nickname;
    }

    public static void setClientId(java.util.UUID id) {
        clientId = id;
    }


    public static void main(String[] args) {
        try {

            Scanner scanner = new Scanner(System.in);
            String host = "localhost";
            int socketPort = 12345;
            int rmiPort = 1099;
            int choice = -1;
            do {
                System.out.println("Scegli il tipo di connessione:");
                System.out.println("1. Socket");
                System.out.println("2. RMI");
                System.out.print("Inserisci la tua scelta: ");
                String input = scanner.nextLine();

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Input non valido. Inserisci un numero.");
                    choice = 0;
                }
            } while (choice != 1 && choice != 2);

            try {
                if (choice == 1) {
                    networkAdapter = new SocketAdapter(host, socketPort);
                    System.out.println("Connessione tramite Socket...");
                } else {
                    networkAdapter = new RmiAdapter(host, rmiPort);
                    System.out.println("Connessione tramite RMI...");
                }
                networkAdapter.connect(host, choice == 1 ? socketPort : rmiPort);


            } catch (IOException | NotBoundException NotBoundException ) {
                System.err.println("Impossibile connettersi al server.");
            }




            Message response = networkAdapter.readMessage();
            if (response.getType() == MessageType.ASSIGN_UUID) {
                clientId = ((StandardMessageClient) response).getId_client();
                System.out.println("✅ Connesso con UUID: " + clientId);
            }


            do {
                System.out.println("Inserisci 1 per la TUI 2 per la GUI : ");
                String input = scanner.nextLine();

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Input non valido. Inserisci un numero.");
                    choice = 0;
                }

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

                        Message msg = (Message) networkAdapter.readMessage();


                        switch (msg.getType()) {

                                case  REQUEST_NAME, NAME_REJECTED,
                                      NAME_ACCEPTED, CREATE_LOBBY, SEE_LOBBIES, SELECT_LOBBY, GAME_STARTED, BUILD_START,
                                      CARD_COMPONENT_RECEIVED, CARD_UNAVAILABLE, UNAVAILABLE_PLACE, ADD_CREWMATES,
                                      INVALID_CONNECTORS, SELECT_PIECE:
                                    inputQueue.put(msg);
                                    break;

                                case ENGINE_POWER, END_FLIGHT, NEW_ADVENTURE_DRAWN, UPDATE_BOARD, WAITING_FLIGHT,
                                     INVALID_SHIP,
                                     START_FLIGHT, FORCE_BUILD_PHASE_END, COLOR_SELECTED, DISMISSED_CARD,
                                     FACED_UP_CARD_UPDATED, UPDATED_SHIPS, DECK_CARD_ADVENTURE_UPDATED, TIME_UPDATE,
                                     BUILD_PHASE_ENDED:
                                    notificationQueue.put(msg);
                                    break;

                            default:

                                notificationQueue.put(msg);
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


                        elaborate(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            new Thread(() -> {
                try {
                    while (true) {
                        Message msg = notificationQueue.take();
                        handleNotification(msg);
                        if (msg.getType() == MessageType.NEW_ADVENTURE_DRAWN) {
                            adventureLatch = new CountDownLatch(1);
                            Thread.sleep(50);

                            adventureLatch.countDown();
                        }
                        if (msg.getType() == MessageType.UPDATE_BOARD && !msg.getContent().isEmpty()) {

                            boardLatch = new CountDownLatch(1);


                        }


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


    public static void elaborate(Message msg) throws IOException, ExecutionException, InterruptedException {


        switch (msg.getType()) {

            case REQUEST_NAME, NAME_REJECTED:  //send the nickname request to the server with his UUID

                if (msg.getType() == MessageType.NAME_REJECTED) {
                    virtualView.showMessage(msg.getContent());
                }


                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).setClientCallback(nickname -> {
                        try {
                            setNickname(nickname);
                            networkAdapter.sendMessage(new StandardMessageClient(MessageType.SENDED_NAME, nickname, clientId));

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    ((GUI) virtualView).createNicknamescreen();

                } else {
                    nickname = virtualView.askNickname();



                    try {
                        networkAdapter.sendMessage(new StandardMessageClient(MessageType.SENDED_NAME, nickname, clientId));

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
                        networkAdapter.sendMessage(to_send);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    to_send = new StandardMessageClient(MessageType.SEE_LOBBIES, "", clientId);
                    try {
                        networkAdapter.sendMessage(to_send);

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
                    } else {
                        lobby_index = virtualView.showLobbies(l_msg.getLobbies());
                    }
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.SELECT_LOBBY, "" + lobby_index, clientId));

                }
                break;

            case SELECT_LOBBY:
                if (msg.getContent().isEmpty()) {
                    virtualView.showGenericError("Lobby selezionata non disponinbile, riprovare");
                    elaborate(new Message(MessageType.SEE_LOBBIES, ""));
                    break;
                } else {
                    if (virtualViewType == VirtualViewType.TUI) {
                        virtualView.showMessage("\nSei entrato nella lobby" + msg.getContent());
                    }

                }
                break;

            case GAME_STARTED:
                GameStartedMessage gs_msg = (GameStartedMessage) msg;
                if (gs_msg.getContent().isEmpty()) {
                    if (virtualViewType == VirtualViewType.TUI) {
                        virtualView.showMessage ("\n----- PARTITA AVVIATA -----");
                    }

                } else {

                    virtualView.showMessage("\n" + gs_msg.getContent());

                }

                Color c;

                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).createchoosecolorscreen(still_Available_colors);
                    c = virtualView.askColor(still_Available_colors);
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                } else {
                    c = virtualView.askColor(gs_msg.getAvailableColors());
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.COLOR_SELECTED, "" + c, clientId));
                }
                break;

            case BUILD_START:
                int deck_selected;

                if (virtualViewType == VirtualViewType.GUI) {

                    try {

                        otherPlayersReady.get();
                        ((GUI) virtualView).createbuildscreen();
                        if(msg instanceof ShipClientMessage){

                            player_local = ((ShipClientMessage) msg).getPlayer();
                            ((GUI) virtualView).getBuildcontroller().printShipImage(player_local.getShip().getShip_board());

                        }
                        deck_selected = virtualView.selectDeck();


                    } catch (InterruptedException | ExecutionException e) {
                        return;
                    }
                } else {
                    deck_selected = virtualView.selectDeck();
                }


                if (deck_selected == 1) {
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.ASK_CARD, "", clientId));

                } else if (deck_selected == 2) {
                    if (virtualViewType == VirtualViewType.GUI) {
                        CompletableFuture<Integer> futureIndex = ((GUI) virtualView).getBuildcontroller().getFaceupCardIndexFuture();
                        int index = futureIndex.get();
                        System.out.println("clientttt indice carta scartata" + index);
                        ((GUI) virtualView).getBuildcontroller().resetfaceupCardIndex(); // opzionale
                        if (index == -1) {
                            elaborate(new Message(MessageType.BUILD_START, ""));
                            break;
                        }
                        if (index >= facedUp_deck_local.size()) {
                            virtualView.showMessage("La carta selezionata non è più disponibile.");
                            elaborate(new Message(MessageType.BUILD_START, ""));
                            break;
                        }
                        UUID selectedCardId = facedUp_deck_local.get(index).getCard_uuid();
                        networkAdapter.sendMessage(new StandardMessageClient(MessageType.ASK_CARD, selectedCardId.toString(), clientId));

                    } else {

                        if (facedUp_deck_local.isEmpty()) {
                            virtualView.showMessage("\nNon ci sono carte a faccia in alto!\n");
                            elaborate(new Message(MessageType.BUILD_START, ""));

                        } else {

                            int index = virtualView.askFacedUpCard(facedUp_deck_local);
                            if (index == -1) {
                                elaborate(new Message(MessageType.BUILD_START, ""));
                                break;
                            }
                            if (index >= facedUp_deck_local.size()) {
                                virtualView.showMessage("La carta selezionata non è più disponibile.");
                                elaborate(new Message(MessageType.BUILD_START, ""));
                                break;
                            }
                            UUID selectedCardId = facedUp_deck_local.get(index).getCard_uuid();
                            networkAdapter.sendMessage(new StandardMessageClient(MessageType.ASK_CARD, selectedCardId.toString(), clientId));
                        }

                    }
                } else if (deck_selected == 3) {  //carte prenotate

                    if (virtualViewType == VirtualViewType.GUI) {
                        CompletableFuture<Integer> futureIndex = ((GUI) virtualView).getBuildcontroller().getReservedCardIndexFuture();
                        int index = futureIndex.get();
                        ((GUI) virtualView).getBuildcontroller().resetReservedCardIndex(); // opzionale
                        if (index == -1) {
                            elaborate(new Message(MessageType.BUILD_START, ""));
                        } else if (index >= 0 && index < player_local.getShip().getExtra_components().size()) {
                            elaborate(new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", clientId, player_local.getShip().getExtra_components().get(index)));
                        }

                    } else {

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
                    }

                } else if (deck_selected == 4) {
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.BUILD_PHASE_ENDED, "", clientId));
                }

                break;

            case CARD_UNAVAILABLE:
                virtualView.showMessage("\nLa carta richiesta non è più disponibile ! ");
                elaborate(new Message(MessageType.BUILD_START, ""));
                break;

            case UNAVAILABLE_PLACE:
                virtualView.showMessage("\nNon puoi posizionare la carta in questa fase del gioco ! ");
                break;

            case CARD_COMPONENT_RECEIVED: //sono nel pannello che apre quando prendo una carta random

                CardComponentMessage card_msg = (CardComponentMessage) msg;
                //virtualView.showMessage("\nCarta disponibile");
                int sel;
                if (virtualViewType == VirtualViewType.GUI) {
                    ((GUI) virtualView).createrandomcardcontroller(card_msg.getCardComponent());
                    sel = virtualView.showCard(card_msg.getCardComponent());
                } else {
                    sel = virtualView.showCard(card_msg.getCardComponent());
                }

                if (sel == 1) {
                    CardComponent card = card_msg.getCardComponent();
                    card.rotate();

                    if (virtualViewType == VirtualViewType.GUI) {
                        ((GUI) virtualView).setActualcard(card);
                    }

                    elaborate(new CardComponentMessage(MessageType.CARD_COMPONENT_RECEIVED, "", clientId, card));
                    return;
                }


                if (sel == 3) {

                    if (player_local.getShip().getExtra_components().contains(card_msg.getCardComponent())) {
                        virtualView.showMessage("\nCarta rimessa nelle carte prenotate");
                        elaborate(new Message(MessageType.BUILD_START, ""));

                        return;
                    }

                    networkAdapter.sendMessage(new CardComponentMessage(MessageType.DISMISSED_CARD, "", clientId, card_msg.getCardComponent()));
                    elaborate(new Message(MessageType.BUILD_START, ""));
                    break;
                }
                if (sel == 2) {

                    Pair<Integer, Integer> coords = virtualView.askCoords(player_local.getShip());


                    if (coords.getKey() == -1 || coords.getValue() == -1) {
                        elaborate(card_msg);
                        break;
                    } else {


                        networkAdapter.sendMessage(new CardComponentMessage(MessageType.PLACE_CARD, coords.getKey() + " " + coords.getValue(), clientId, card_msg.getCardComponent()));

                        player_local.addToShip(card_msg.getCardComponent(), coords.getKey(), coords.getValue());

                        elaborate(new Message(MessageType.BUILD_START, ""));
                        player_local.getShip().getExtra_components().remove(card_msg.getCardComponent());
                        break;

                    }

                }
                if (sel == 4) {

                    if (player_local.getShip().getExtra_components().size() == 2) {
                        virtualView.showMessage("\nSpazio esaurito nelle carte prenotate");
                        networkAdapter.sendMessage(new CardComponentMessage(MessageType.DISMISSED_CARD, "", clientId, card_msg.getCardComponent()));
                        elaborate(new Message(MessageType.BUILD_START, ""));

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
                            ((LivingUnit) component).setNum_crewmates(0);
                            ((LivingUnit) component).setCrewmate_type(CrewmateType.None);

                            int select;
                            if (virtualViewType == VirtualViewType.GUI) {
                                List<CrewmateType> crewmates = ((GUI) virtualView).getCrewmates(coords);
                                ((GUI) virtualView).getBuildcontroller().highlightCell(coords);
                                ((GUI) virtualView).createCrewmateSelectionController(coords, crewmates);
                                select = virtualView.crewmateAction(coords);
                                ((GUI) virtualView).getBuildcontroller().resetHighlights(coords);

                            } else {
                                select = virtualView.crewmateAction(coords);
                            }
                            CrewmateType type;


                            if (select == 1) {
                                type = CrewmateType.Astronaut;
                                ((LivingUnit) component).addAstronauts();
                                final int x = coords.getKey();
                                final int y = coords.getValue();

                                if (virtualViewType == VirtualViewType.GUI) {
                                    Platform.runLater(() -> {
                                        ((GUI) virtualView).getBuildcontroller().addObject(y, x, "Astronaut");
                                    });
                                }


                            } else if (select == 2) {

                                type = CrewmateType.PinkAlien;
                                ((LivingUnit) component).addAlien(CrewmateType.PinkAlien);
                                final int x = coords.getKey();
                                final int y = coords.getValue();

                                if (virtualViewType == VirtualViewType.GUI) {
                                    Platform.runLater(() -> {
                                        ((GUI) virtualView).getBuildcontroller().addObject(y, x, "PinkAlien");
                                    });
                                }


                            } else {

                                type = CrewmateType.BrownAlien;
                                ((LivingUnit) component).addAlien(CrewmateType.BrownAlien);

                                final int x = coords.getKey();
                                final int y = coords.getValue();

                                if (virtualViewType == VirtualViewType.GUI) {
                                    Platform.runLater(() -> {
                                        ((GUI) virtualView).getBuildcontroller().addObject(y, x, "BrownAlien");
                                    });
                                }

                            }
                            networkAdapter.sendMessage(new AddCrewmateMessage(MessageType.ADD_CREWMATES, "", clientId, coords, type));
                        } else if (component.getComponentType() == Battery) {
                            Battery batteryComponent = (Battery) component;


                            int batteryCount = batteryComponent.getStored();

                            final int x = coords.getKey();
                            final int y = coords.getValue();

                            if (virtualViewType == VirtualViewType.GUI) {
                                Platform.runLater(() -> {
                                    ((GUI) virtualView).getBuildcontroller().addBattery(y, x, "Battery", batteryCount);
                                });
                            }

                        } else if (component.getComponentType() == MainUnitBlue ||
                                component.getComponentType() == MainUnitRed ||
                                component.getComponentType() == MainUnitGreen ||
                                component.getComponentType() == MainUnitYellow) {

                            final int x = coords.getKey();
                            final int y = coords.getValue();

                            if (virtualViewType == VirtualViewType.GUI) {
                                Platform.runLater(() -> {
                                    ((GUI) virtualView).getBuildcontroller().addObject(y, x, "Astronaut");
                                });
                            }
                        }
                    }
                }
                networkAdapter.sendMessage(new StandardMessageClient(MessageType.CHECK_SHIPS, "", clientId));

                break;

            case INVALID_CONNECTORS:
                InvalidConnectorsMessage icm = (InvalidConnectorsMessage) msg;
                if (icm.getInvalids().isEmpty()) {
                    virtualView.showMessage("\n Tutti i connettori sono disposti in maniera giusta, si passa al prossimo controllo");
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.FIXED_SHIP_CONNECTORS, "", clientId, player_local.copyPlayer()));
                } else {
                    if(virtualViewType == VirtualViewType.GUI) {

                        ((GUI)virtualView).getBuildcontroller().printInvalidsConnector(player_local.getShip(), icm.getInvalids());

                        try {

                            Ship updatedShip = ((GUI)virtualView).getBuildcontroller().getUpdatedShip().get();
                            networkAdapter.sendMessage(new ShipClientMessage(MessageType.FIXED_SHIP_CONNECTORS, "", clientId, player_local.copyPlayer()));
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                    } else {

                        player_local.setShip(virtualView.removeInvalidsConnections(player_local.getShip(), icm.getInvalids()));
                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.FIXED_SHIP_CONNECTORS, "", clientId, player_local.copyPlayer()));
                    }
                }
                break;

            case SELECT_PIECE:

                ShipPiecesMessage spm = (ShipPiecesMessage) msg;
                List<List<Pair<Integer, Integer>>> pieces = spm.getPieces();
                int piece = virtualView.askPiece(pieces, player_local.getShip().getShipBoard());
                networkAdapter.sendMessage(new StandardMessageClient(MessageType.SELECT_PIECE, String.valueOf(piece), clientId));
                break;


            case ASTRONAUT_LOSS:

                int num_crew_mates = 2;

                while (num_crew_mates != 0) {

                    Pair<Integer, Integer> lu = virtualView.chooseAstronautLosses(player_local.getShip());
                    if (lu.getValue() == -1 || lu.getKey() == -1) continue;
                    else {
                        LivingUnit l = (LivingUnit) player_local.getShip().getComponent(lu.getKey(), lu.getValue());
                        num_crew_mates--;
                        virtualView.showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");
                    }

                }
                networkAdapter.sendMessage(new StandardMessageClient(MessageType.ASTRONAUT_LOSS, "", clientId));
                break;


            case END_FLIGHT:
                Message end_msg = msg;
                virtualView.showMessage("\n Purtroppo non puoi più continuare la tua fase di volo");
                virtualView.earlyEndFlightResume(player_local);
                // da riveder messa qua giusto per avere un idea
                break;


        }


    }


    public static void handleNotification(Message msg) throws IOException, ExecutionException, InterruptedException {


        switch (msg.getType()) {


            case GAME_FINISHED:
            PlayersShipsMessage pm = (PlayersShipsMessage) msg;
            virtualView.showMessage("\n\n\n\n ---------- IL GIOCO é FINITO ----------\n\n");
            virtualView.printFinalRanks(pm.getPlayers());
            break;



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
                    if (virtualViewType==VirtualViewType.TUI){
                        virtualView.showMessage("\nHai scelto il colore : " + parts[1]);
                    }
                } else {
                    // virtualView.showMessage("\nIl player " + parts[0] + " ha scelto il colore : " + parts[1]);
                    if (virtualViewType == VirtualViewType.GUI) {
                        //virtualView.showMessage("\nIl player " + parts[0] + " ha scelto il colore : " + parts[1]);
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
                    virtualView.updateLocalPlayer(player_local);
                    if (!Client.otherPlayersReady.isDone()) {
                        Client.otherPlayersReady.complete(null);
                        System.out.println(">>> [DEBUG] CompletableFuture completata con altri giocatori: " + other_players_local);
                    }
                }
                if (virtualViewType == VirtualViewType.TUI) {
                    virtualView.updateLocalPlayer(player_local);
                    virtualView.updateOtherPlayers(other_players_local);
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
                    if (virtualViewType == VirtualViewType.GUI) {
                        if (((GUI) virtualView).getBuildcontroller() != null) {
                            ((GUI) virtualView).getBuildcontroller().updateFaceUpCardsDisplay();

                        }
                    }

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
                System.out.println("SONO QUI PER MODIF TIMER CIAO");
                if(virtualViewType == VirtualViewType.GUI){
                    if(msg.getContent().equals("⏳ 90s rimanenti") || msg.getContent().equals("🔔 Un giocatore ha finito! ")){
                        ((GUI)virtualView).getBuildcontroller().starttimer(90);
                    }
                }
                if(virtualViewType==VirtualViewType.TUI) {
                    virtualView.showMessage("\n" + msg.getContent());
                }

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
                virtualView.showMessage("""
                        
                        Hai completato la fase di controllo ora rimani in attesa degli altri giocatori.
                        Questa è la tua nave.
                        \s""");

                virtualView.printShip(player_local.getShip().getShipBoard());

                virtualView.showMessage("\n\n\tRIMANI IN ATTESA CHE GLI ALTRI PLAYER FINISCANO IL CONTROLLO !");
                break;


            case UPDATE_BOARD:
                BoardMessage bm = (BoardMessage) msg;
                local_board_laps = bm.getLaps();
                local_board_positions = bm.getPositions();

                if (!bm.getContent().isEmpty()) {
                    System.out.println();
                    virtualView.showMessage(bm.getContent());
                    virtualView.showBasicBoard(local_board_positions, local_board_laps);

                } else {
                    System.out.println();
                    if (virtualViewType == VirtualViewType.GUI) {
                        System.out.print("sono qui");
                        ((GUI) virtualView).createFlyghtScreen(local_board_positions, local_board_laps);
                    }else {
                        virtualView.showBasicBoard(local_board_positions, local_board_laps);
                    }
                }

                if (virtualViewType == VirtualViewType.TUI) {
                    ((TUI) virtualView).setLocal_board_position(local_board_positions);
                    ((TUI) virtualView).setLocal_board_laps(local_board_positions);

                }
                int dummy = virtualView.nextMeteor();
                boardLatch.countDown();


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
                if(virtualViewType == VirtualViewType.GUI) {
                    ((GUI)virtualView).getFlyghtController().addAdventureCard(ad.getAdventure());
                }
                virtualView.printCardAdventure(ad.getAdventure());
                dummy = virtualView.nextMeteor();

                break;


            case NOTIFICATION:
                NotificationMessage nm = (NotificationMessage) msg;

                String curr_nick = nm.getNickname();
                if(!curr_nick.equals(nickname)){

                    virtualView.showMessage("\n"+nm.getContent());
                    break;

                }
                break;

            case OPEN_SPACE, ABANDONED_STATION, ABANDONED_SHIP, METEOR_SWARM, COMBAT_ZONE, PLANETS:
                AdventureCardMessage ac = (AdventureCardMessage) msg;
                manageAdventure(ac.getAdventure(), ac.getContent());
                break;

            case ENGINE_POWER:
                String[] enginePower = msg.getContent().split("\\s+");
                if (!enginePower[0].equals(nickname)) {

                    virtualView.showMessage("IL PLAYER " + enginePower[0] + " ha una potenza MOTORE di : " + enginePower[1]);


                }
                break;

            case CANNON_POWER:
                String[] canonPower = msg.getContent().split("\\s+");
                if (!canonPower[0].equals(nickname)) {

                    virtualView.showMessage("IL PLAYER " + canonPower[0] + " ha una potenza CANNONI di : " + canonPower[1]);


                }
                break;

            case ENGINE_POWER_RANK:
                RankingMessage rank = (RankingMessage) msg;

                String less_engine = rank.getWeakerPlayer();

                System.out.println(rank.getWeakerPlayer());

                virtualView.ShowRanking(rank.getRanks(), "POTENZA MOTORI ");
                String combat_zone_id = rank.getContent();


                if (less_engine.equals(nickname)) {

                    if (combat_zone_id.equals("1")) {
                        elaborate(new Message(MessageType.ASTRONAUT_LOSS, ""));
                        break;
                    } else {

                        handleNotification(new Message(MessageType.CARGO_LOSS, "2"));
                        break;
                    }

                }   else {

                    virtualView.showMessage("\n --- il PLAYER " + less_engine + " sta pagando la penitenza ---\n");

                }


                break;

            case CANNON_POWER_RANK:
                rank = (RankingMessage) msg;

                String less_cannon = rank.getWeakerPlayer();

                virtualView.ShowRanking(rank.getRanks(), "POTENZA MOTORI ");

                if (less_cannon.equals(nickname)) {

                    if(msg.getContent().equals("1")) {
                        StringBuilder coords_m = new StringBuilder();
                        for (int i = 0; i < 2; i++) {

                            coords_m.append(throwDice()).append(" ");

                        }

                        manageAdventure(
                                new MeteorSwarm(2, 0, CardAdventureType.MeteorSwarm,
                                        List.of(
                                                new Pair<>(MeteorType.LightCannonFire, South),
                                                new Pair<>(MeteorType.HeavyCannonFire, South)
                                        ),""
                                ), coords_m.toString());


                    }else{
                        virtualView.showMessage("HAI PERSO 4 GIORNI DI VOLO : ");

                        break;
                    }


                    break;
                } else {
                    if(msg.getContent().equals("1")) {

                        virtualView.showMessage("\n --- il PLAYER " + less_cannon + " sta pagando la penitenza di 2 CANNONATE ---\n");
                    }
                }

                break;



            case CARGO_LOSS:

                int num_cargo_loss = Integer.parseInt(msg.getContent());

                while(num_cargo_loss > 0){



                    virtualView.removeCargo(player_local.getShip());

                    num_cargo_loss--;

                }
                networkAdapter.sendMessage(new StandardMessageClient(MessageType.CARGO_LOSS,"cz",clientId));

                break;



            default:

                AdventureCardMessage adv = (AdventureCardMessage) msg;
                manageAdventure(adv.getAdventure(), adv.getContent());
                break;
        }


    }


    public static void manageAdventure(CardAdventure adventure, String content) throws IOException {
        if (virtualViewType==VirtualViewType.GUI){
            ((GUI) virtualView).getFlyghtController().updatePlayerPositions(local_board_positions,local_board_laps );
            ((GUI) virtualView).getFlyghtController().updatePlayerShip();
        }
        virtualView.updateLocalPlayer(player_local);

        switch (adventure.getType()) {


            case OpenSpace:
                OpenSpace openSpace = (OpenSpace) adventure;
                Ship ship = player_local.getShip();
                Map<Pair<Integer,Integer>, Boolean> battery_usage_os = new HashMap<>();
                Pair<Integer, Integer> battery=new Pair<>(-1,-1);
                Battery card_battery = null;


                for (int i = 0; i < ship.getROWS(); i++) {
                    for (int j = 0; j < ship.getCOLS(); j++) {
                        CardComponent card = ship.getComponent(i, j);

                        if (card.getComponentType() == DoubleEngine) {

                                battery = virtualView.askEngine(new Pair<>(i, j));
                                if (battery.getKey() == -1 || battery.getValue() == -1) {

                                    battery_usage_os.put(new Pair<>(i, j), false);

                                } else {
                                    battery_usage_os.put(new Pair<>(i, j), true);
                                    card_battery = (Battery) ship.getComponent(battery.getKey(), battery.getValue());
                                    card_battery.removeBattery();


                                }


                        }

                    }
                }

                double power_m = ship.calculateEnginePower(battery_usage_os);
                virtualView.showMessage("\n\nPOTENZA MOTORE : " + power_m);
                networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, String.valueOf(power_m), clientId, player_local));

                break;


            case AbandonedStation:

                Boolean choice = virtualView.acceptAdventure("ACCETTI L'AVVENTURA?");
                AbandonedStation a_s = (AbandonedStation) adventure;
                Pair<Pair<Integer, Integer>, Integer> new_position;

                List<Cargo> cargos = new ArrayList<>(a_s.getCargo());

                if (choice) {


                    cargoAction(cargos);
                } else {

                    networkAdapter.sendMessage((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local)));
                    break;
                }

                networkAdapter.sendMessage((new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "adv done", clientId, player_local)));


                break;


            case AbandonedShip:

                AbandonedShip ab_ship = (AbandonedShip) adventure;


                choice = virtualView.acceptAdventure("ACCETTI L'AVVENTURA?");

                if (choice) {

                    int num_crew_mates = ab_ship.getCrewmates_loss();
                    player_local.setCredits(player_local.getCredits() + ab_ship.getGiven_credits());
                    while (num_crew_mates != 0) {
                        if(virtualViewType == VirtualViewType.GUI) {
                            virtualView.showMessage("Mancano"+num_crew_mates+" crewmates da rimuovere");
                            ((GUI) virtualView).getFlyghtController().showCrewmates(player_local.getShip());
                            try {
                                Pair<Integer, Integer> lu =  ((GUI) virtualView).coordsCrewmate();
                                if (lu.getValue() == -1 || lu.getKey() == -1) continue;
                                else {
                                    LivingUnit l = (LivingUnit) player_local.getShip().getComponent(lu.getKey(), lu.getValue());
                                    num_crew_mates--;
                                    virtualView.showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");
                                }
                            } catch (Exception e) {
                                System.err.println("Errore durante la selezione del crewmate: " + e.getMessage());
                            }
                        }

                        if(virtualViewType==VirtualViewType.TUI) {
                            Pair<Integer, Integer> lu = virtualView.chooseAstronautLosses(player_local.getShip());
                            if (lu.getValue() == -1 || lu.getKey() == -1) continue;
                            else {
                                LivingUnit l = (LivingUnit) player_local.getShip().getComponent(lu.getKey(), lu.getValue());
                                num_crew_mates--;
                                virtualView.showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");
                            }
                        }

                    }
                    if(virtualViewType == VirtualViewType.GUI){
                        ((GUI)virtualView).getFlyghtController().updateCreditLabel(player_local.getCredits());
                    }
                } else {
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local));
                    break;
                }
                networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "adv done", clientId, player_local));

                break;



            case MeteorSwarm:
                MeteorSwarm meteor = (MeteorSwarm) adventure;
                List<Pair<MeteorType, Direction>> meteors = meteor.getMeteors();
                String[] meteor_coords = content.split("\\s+");
                List<Integer> coordList = new ArrayList<>();
                Pair<Integer, Integer> pair;
                for (int i = 0; i < meteor_coords.length; i++) {
                    coordList.add(Integer.parseInt(meteor_coords[i]));
                }


                int i = 0;
                for (Pair<MeteorType, Direction> m : meteors) {

                    virtualView.printMeteor(m, coordList.get(i));


                    if (m.getValue() == Direction.North || m.getValue() == South) {
                        if (coordList.get(i) < 4 || coordList.get(i) >= 11) {
                            virtualView.showMessage("\nMETEORITE NON HA BECCATO LA NAVE!!\n");

                            continue;
                        }

                    } else {
                        if (coordList.get(i) < 5 || coordList.get(i) >= 10) {

                            virtualView.showMessage("\nMETEORITE NON HA BECCATO LA NAVE!!\n");

                            continue;

                        }
                    }

                    pair = player_local.getShip().getFirstComponent(m.getValue(), coordList.get(i));

                    if (pair.getKey() == 0 && pair.getValue() == 0) {

                        virtualView.showMessage("\nMETEORITE NON HA BECCATO LA NAVE!!\n");

                        continue;


                    }


                    CardComponent hitted = player_local.getShip().getComponent(pair.getKey(), pair.getValue());

                    virtualView.showHittedCard(hitted, m.getValue());

                    switch (m.getKey()) {


                        case SmallMeteor:

                            if (hitted.getConnector(m.getValue()) == ConnectorType.Smooth) {


                                virtualView.showMessage("\nMeteorite rimbalza sul lato liscio \n");

                                continue;

                            } else checkProtection(pair, m);
                            break;

                        case LargeMeteor:


                            if (hitted.getConnector(m.getValue()) == ConnectorType.Cannon_Connector) {

                                if (hitted.getComponentType() == DoubleCannon) {
                                    Pair<Integer, Integer> b = virtualView.useBattery(player_local.getShip());

                                    if (b.getKey() == -1 || b.getValue() == -1) {

                                        removeComp(pair);


                                    } else {
                                        card_battery=(Battery) player_local.getShip().getComponent(b.getKey(), b.getValue());
                                        card_battery.removeBattery();
                                    }
                                } else {

                                    virtualView.showMessage("\n\nMeteorite DISTRUTTO con cannone singolo!! (non serve batteria)");

                                }

                            } else {
                                removeComp(pair);
                            }


                            break;






                        case LightCannonFire:


                            checkProtection(pair, m);
                            break;


                        case HeavyCannonFire:

                            player_local.getShip().removeComponent(pair.getKey(), pair.getValue());

                            virtualView.showMessage("\n !!!!! COMPONENTE DISTRUTTO  !!! \n");
                            virtualView.printShip(player_local.getShip().getShipBoard());


                            List<List<Pair<Integer, Integer>>> pieces = player_local.getShip().findShipPieces();


                            if (pieces.isEmpty()) {
                                virtualView.showMessage(" ---- NON PUOI PIU CONTINUARE IL VOLO, NON HAI UNA NAVE VALIDA ! ---- ");
                                networkAdapter.sendMessage(new StandardMessageClient(MessageType.END_FLIGHT, "", clientId));
                                System.exit(0);
                            } else if (pieces.size() > 1) {
                                int piece = virtualView.askPiece(pieces, player_local.getShip().getShipBoard());
                                player_local.getShip().choosePiece(piece);
                            }
                            break;


                    }
                    i++;
            int dummy = virtualView.nextMeteor();

                }


                virtualView.showMessage("\n--- RIMANI IN ATTESA CHE ANCHE GLI ALTRI GIOCATORI FINISCANO L'AVVENTURA ---");

                if(adventure.getLevel() != -1) {
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local));
                }
                break;



            case Epidemic:
                Epidemic epidemic = (Epidemic) adventure;

                virtualView.executeEpidemic((player_local.getShip()));

                virtualView.nextMeteor();

                break;

            case Stardust:
                Stardust stardust = (Stardust) adventure;


            case Planets:
                Planets planets = (Planets) adventure;
                List<List<Cargo>> planet_list = planets.getCargo_reward();
                String[] parts;
                Set<Integer> planets_taken = new HashSet<>();
                if (!content.isEmpty()) {

                    parts = content.trim().split("\\s+");
                    for (String part : parts) {
                        try {
                            Integer id = Integer.parseInt(part);
                            planets_taken.add(id);
                        } catch (NumberFormatException _) {
                        }
                    }
                }
                int planet = virtualView.askPlanet(planet_list, planets_taken);
                if (planet == -1) {
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "", clientId, player_local));

                    virtualView.showMessage("\n--- HAI DECISO DI NON PRENDERE NESSUN PIANETA,  RIMANI IN ATTESA CHE ANCHE GLI ALTRI GIOCATORI FINISCANO L'AVVENTURA ---");

                    break;
                }
                List<Cargo> planet_cargos = new ArrayList<>(planet_list.get(planet));
                cargoAction(planet_cargos);
                networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, String.valueOf(planet), clientId, player_local));
                virtualView.showMessage("\n--- AVVENTURA COMPLETATA, RIMANI IN ATTESA CHE ANCHE GLI ALTRI GIOCATORI FINISCANO L'AVVENTURA ---");

                break;


            case CombatZone:


                switch (content) {


                    case "engine":

                        virtualView.showMessage("\n DEVI DICHIARARE LA TUA POTENZA MOTORE PER LA CARTA ZONA DI GUERRA : \n");


                        double power = enginePower(0);

                        virtualView.showMessage("\n ----- POTENZA MOTORE TOTALE :  " + power + " -----\n");

                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "eng " + String.valueOf(power), clientId, player_local));
                        break;


                    case "cannon":
                        double power_c =  cannonPower(0);


                        virtualView.showMessage("\n ----- POTENZA CANNONI TOTALE :  " + power_c + " -----\n");

                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "can " + String.valueOf(power_c), clientId, player_local));
                        break;

                }


                break;


            case Smugglers:

                Smugglers smugglers = (Smugglers) adventure;
                virtualView.showMessage("\n DEVI DICHIARARE LA TUA POTENZA CANNONE , POTENZA NEMICO =  " +smugglers.getCannons_strenght() +  " \n");

                double power_c =  cannonPower(smugglers.getCannons_strenght());


                virtualView.showMessage("\n ----- POTENZA CANNONI TOTALE :  " + power_c + " -----\n");


                if(power_c < smugglers.getCannons_strenght()) {

                    int cargo_loss = smugglers.getCargo_loss();

                    while(cargo_loss > 0) {

                        virtualView.removeCargo(player_local.getShip());
                        cargo_loss--;

                    }

                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "l", clientId, player_local));
                    break;

                }else if(power_c > smugglers.getCannons_strenght()) {

                    choice = virtualView.acceptAdventure("HAI SCONFITTO IL NEMICO, VUOI PRENDERE RICOMPENSA (e quindi perdere i giorni di volo)?");
                    if(choice ){
                        cargoAction(smugglers.getCargo_rewards());

                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "ww", clientId, player_local));
                        break;
                    }

                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "w", clientId, player_local));


                }else if(power_c == smugglers.getCannons_strenght()) {


                    virtualView.showMessage("\nHAI PAREGGIATO, IL NEMICO NON è SCONFITTO, MA NON PAGHI NULLA!");
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "d", clientId, player_local));

                }
                if(virtualViewType == VirtualViewType.GUI){
                    ((GUI)virtualView).getFlyghtController().updateCreditLabel(player_local.getCredits());
                }

                break;

            case Pirates:
                Pirates pirates = (Pirates) adventure;
                virtualView.showMessage("\n DEVI DICHIARARE LA TUA POTENZA CANNONE , POTENZA NEMICO =  " +pirates.getCannons_strenght() +  " \n");
                power_c =  cannonPower(pirates.getCannons_strenght());
                virtualView.showMessage("\n ----- POTENZA CANNONI TOTALE :  " + power_c + " -----\n");

                StringBuilder coords_m = new StringBuilder();

                if(power_c < pirates.getCannons_strenght()) {


                    virtualView.showMessage("\n ----- HAI PERSO RICEVI DELLE CANNONATE ---- ");

                    manageAdventure(
                            new MeteorSwarm(-1, 0, CardAdventureType.MeteorSwarm,
                                    List.of(
                                            new Pair<>(MeteorType.LightCannonFire, South),
                                            new Pair<>(MeteorType.HeavyCannonFire, South)
                                    ),""
                            ), content);


                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "l", clientId, player_local));
                    break;
                } else if(power_c == pirates.getCannons_strenght() ){


                    virtualView.showMessage("\nHAI PAREGGIATO LA POTENZA DEI NEMICI, non ti succede nulla, ma il nemico non è sconfitto");
                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "d", clientId, player_local));


                }else if(power_c > pirates.getCannons_strenght()) {


                    choice = virtualView.acceptAdventure("\nCOMPLIMENTI HAI SCONFITTO I PIRATI, vuoi prendere " + pirates.getCredits()+ "crediti e perdere "+ pirates.getCost_of_days() +" giorni di volo?" );

                    if(choice){
                        player_local.setCredits( player_local .getCredits() + pirates.getCredits());
                        if(virtualViewType == VirtualViewType.GUI){
                            ((GUI)virtualView).getFlyghtController().updateCreditLabel(player_local.getCredits());
                        }
                        if(virtualViewType == VirtualViewType.TUI) {
                            virtualView.showMessage("\nHAI GUADAGNATO " + pirates.getCredits() + " crediti , ora ne hai " + player_local.getCredits());
                        }
                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "ww", clientId, player_local));

                    }else{

                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "w", clientId, player_local));

                    }

                    if(virtualViewType == VirtualViewType.GUI){
                        ((GUI)virtualView).getFlyghtController().updateCreditLabel(player_local.getCredits());
                    }

                    break;

                }


            case Slavers:
                Slavers slavers = (Slavers) adventure;
                virtualView.showMessage("\n DEVI DICHIARARE LA TUA POTENZA CANNONE , POTENZA NEMICO =  " +slavers.getCannons_strenght() +  " \n");
                power_c =  cannonPower(slavers.getCannons_strenght());
                virtualView.showMessage("\n ----- POTENZA CANNONI TOTALE :  " + power_c + " -----\n");



                if(power_c < slavers.getCannons_strenght()) {


                    virtualView.showMessage("\n ----- HAI PERSO e PERDI " + slavers.getAstronaut_loss() +" membri dell' EQUIPUAGGIO ---- ");
                    int num_crew_mates = slavers.getAstronaut_loss();
                    while (num_crew_mates != 0) {

                        Pair<Integer, Integer> lu = virtualView.chooseAstronautLosses(player_local.getShip());
                        if (player_local.getShip().getNumOfCrewmates() == 0){
                            virtualView.showMessage(" ---- NON PUOI PIU CONTINUARE IL VOLO! (NON HAI PIU EQUIPAGGIO) ---- ");

                            networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "l", clientId, player_local));
                            networkAdapter.sendMessage(new StandardMessageClient(MessageType.END_FLIGHT,"",clientId));
                            System.exit(0);

                        }
                        else {
                            LivingUnit l = (LivingUnit) player_local.getShip().getComponent(lu.getKey(), lu.getValue());
                            num_crew_mates--;
                            virtualView.showMessage("\nRIMOZIONE AVVENUTA CON SUCCESSO ! \n");
                        }

                    }

                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "l", clientId, player_local));
                    break;
                } else if(power_c == slavers.getCannons_strenght() ){

                    virtualView.showMessage("\nHAI PAREGGIATO LA POTENZA DEI NEMICI, non ti succede nulla, ma il nemico non è sconfitto");

                    networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "d", clientId, player_local));


                }else if(power_c > slavers.getCannons_strenght()) {


                    choice = virtualView.acceptAdventure("\nCOMPLIMENTI HAI SCONFITTO I PIRATI, vuoi prendere " + slavers.getCredits() + "crediti e perdere " + slavers.getCost_of_days() + " giorni di volo?");

                    if (choice) {
                        player_local.setCredits( player_local .getCredits() + slavers.getCredits());
                        if(virtualViewType == VirtualViewType.GUI){
                            ((GUI)virtualView).getFlyghtController().updateCreditLabel(player_local.getCredits());
                        }
                        if(virtualViewType == VirtualViewType.TUI) {
                            virtualView.showMessage("HAI GUADAGNATO " + slavers.getCredits() + " crediti , ora ne hai " + player_local.getCredits());
                        }
                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "ww", clientId, player_local));

                    } else {

                        networkAdapter.sendMessage(new ShipClientMessage(MessageType.ADVENTURE_COMPLETED, "w", clientId, player_local));

                    }

                    break;
                }
        break;
        }


    }

    public static void checkProtection(Pair<Integer, Integer> pair, Pair<MeteorType, Direction> m) throws IOException {
        Battery card_battery;
        if (player_local.getShip().isProtected(m.getValue())) {

            Pair<Integer, Integer> b = virtualView.useBattery(player_local.getShip());

            if (b.getKey() == -1 || b.getValue() == -1) {

                player_local.getShip().removeComponent(pair.getKey(), pair.getValue());

                virtualView.showMessage("\n !!!!! COMPONENTE DISTRUTTO  !!! \n");
                virtualView.printShip(player_local.getShip().getShipBoard());


                List<List<Pair<Integer, Integer>>> pieces = player_local.getShip().findShipPieces();


                if (pieces.isEmpty()) {
                    virtualView.showMessage(" ---- NON PUOI PIU CONTINUARE IL VOLO, NON HAI UNA NAVE VALIDA ! ---- ");
                    networkAdapter.sendMessage(new StandardMessageClient(MessageType.END_FLIGHT, "", clientId));
                    System.exit(0);
                } else if (pieces.size() > 1) {
                    int piece = virtualView.askPiece(pieces, player_local.getShip().getShipBoard());
                    player_local.getShip().choosePiece(piece);
                }


            } else {
                card_battery = (Battery) player_local.getShip().getComponent(b.getKey(), b.getValue());
                card_battery.removeBattery();
            }


        } else {

            removeComp(pair);
        }
    }

    public static void removeComp(Pair<Integer, Integer> pair) throws IOException {
        player_local.getShip().removeComponent(pair.getKey(), pair.getValue());
        virtualView.showMessage("\n !!!!! COMPONENTE DISTRUTTO  !!! \n");
        virtualView.printShip(player_local.getShip().getShipBoard());

        List<List<Pair<Integer, Integer>>> pieces = player_local.getShip().findShipPieces();
        if (pieces.isEmpty()) {
            virtualView.showMessage(" ---- NON PUOI PIU CONTINUARE IL VOLO, NON HAI UNA NAVE VALIDA ! ---- ");
            networkAdapter.sendMessage(new StandardMessageClient(MessageType.END_FLIGHT, "", clientId));
            System.exit(0);
        } else if (pieces.size() > 1) {
            int piece = virtualView.askPiece(pieces, player_local.getShip().getShipBoard());
            player_local.getShip().choosePiece(piece);
        }
    }

    public static void cargoAction(List<Cargo> planet_cargos) {
        Pair<Pair<Integer, Integer>, Integer> new_position;
        Ship ship;
        while (true) {

            int scelta = virtualView.askCargo(planet_cargos);

            if (scelta == -1) {
                break;
            }
            System.out.println("debug scelgo" +scelta);

            Cargo c = planet_cargos.get(scelta);
            new_position = virtualView.addCargo(player_local.getShip(), c);

            if (new_position != null) {
                ship = player_local.getShip();
                planet_cargos.remove(scelta);
                Storage s = ((Storage) ship.getComponent(new_position.getKey().getKey(), new_position.getKey().getValue()));
                s.addCargo(c, new_position.getValue());

            }
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

    public static UUID getClientId() {
        return clientId;
    }

    public static BlockingQueue<Message> getInputQueue() {
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

    public static int throwDice() {
        Random dice1 = new Random();
        Random dice2 = new Random();

        return (dice1.nextInt(6) + 1) + (dice2.nextInt(6) + 1);
    }


    public static double cannonPower(int val){

        Map<Pair<Integer,Integer>,Boolean> battery_usage_c = new HashMap<>();
        Pair<Integer,Integer> battery = new Pair<>(-1, -1);
        Ship ship = player_local.getShip();

        for (int k = 0; k < ship.getROWS(); k++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(k, j);

                if (card.getComponentType() == DoubleCannon) {


                    if(virtualViewType == VirtualViewType.TUI){

                        System.out.println("\nPOTENZA ATTUALE  :" + player_local.getShip().calculateCannonPower(battery_usage_c) );
                        if(val!=0) {
                            System.out.println("\n POTENZA NEMICO : " + val);
                        }
                    }


                    battery = virtualView.askCannon(new Pair<>(k, j));
                    if (battery.getKey() == -1 || battery.getValue() == -1) {

                        battery_usage_c.put(new Pair<>(k, j), false);

                    } else {

                        battery_usage_c.put(new Pair<>(k, j), true);
                        Battery card_battery  = (Battery) ship.getComponent(battery.getKey(), battery.getValue());

                        card_battery.removeBattery();

                    }

                }

            }
        }

        return  ship.calculateCannonPower(battery_usage_c);


    }

    public static double enginePower(int val){


        Map<Pair<Integer,Integer>,Boolean> battery_usage_eng = new HashMap<>();
        Pair<Integer, Integer> battery;
        Battery card_battery;
        Ship ship = player_local.getShip();

        for (int k = 0; k < ship.getROWS(); k++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(k, j);

                if (card.getComponentType() == DoubleEngine) {

                    System.out.print("\nPOTENZA ATTUALE  :" + player_local.getShip().calculateEnginePower(battery_usage_eng) );



                    battery = virtualView.askEngine(new Pair<>(k, j));
                    if (battery.getKey() == -1 || battery.getValue() == -1) {

                        battery_usage_eng.put(new Pair<>(k, j), false);

                    } else {

                        battery_usage_eng.put(new Pair<>(k, j), true);
                        card_battery = (Battery) ship.getComponent(battery.getKey(), battery.getValue());

                        card_battery.removeBattery();

                    }

                }

            }
        }

        return ship.calculateEnginePower(battery_usage_eng);


    }

}