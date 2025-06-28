package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.view.View;
import javafx.util.Pair;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static it.polimi.ingsw.model.enumerates.ComponentType.*;
import static it.polimi.ingsw.model.enumerates.Direction.*;

public class TUI implements View {
    private final PrintStream out;
    static BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private List<Player> other_players_local = new ArrayList<>();
    Map<Direction, List<CardAdventure>> local_adventure_deck;
    private Player player_local;
    private List<CardComponent> local_extra_components;

    private Map<Integer,Player> local_board_positions;
    private Map<Integer,Player> local_board_laps;

    private boolean isMenuOpen = false; // Variabile per tenere traccia dello stato del menu
    private String lastRequest = ""; // Variabile per memorizzare l'ultima richiesta
    private Board local_board;
    Scanner input = new Scanner(System.in);
    // Codici ANSI
    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";
    final String YELLOW = "\u001B[33m";
    final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    String banner = """
                          ✦   ✷       ⋆      ✧     .    ✨       ⋆       ✶     ✦    ✧
                       .       .     ⋆   ✦        ✨       ✦    ✧  .    ✧    ✶    ⋆    .
                          ✧     ✨   ✶     ⋆       ✷        ⋆       ✧      ✦        ✷    
            ✧     ✨   ✶     ⋆       ✷     ✦    ⋆       ✧       ✦      ✧          ✦✧
                        _______      ___       __          ___      ___   ___ ____    ____        \s
               ✷       /  _____|    /   \\  .  |  |     .  /   \\     \\  \\ /  / \\   \\  /   /      ✧  \s
                      |  |  __     /  ^  \\    |  |   .   /  ^  \\     \\  V  /   \\   \\/   /     ✧    \s
                ✧     |  | |_ |   /  /_\\  \\   |  |      /  /_\\  \\     >   <     \\_    _/     ✷     \s
                      |  |__| |  /  _____  \\  |  `----./  _____  \\   /  .  \\      |  |            \s
             ✷         \\______| /__/     \\__\\ |_______/__/     \\__\\ /__/ \\__\\     |__|   ✷        ✷ \s
                      ✧     ✨   ✶     ⋆       ✷    .    ⋆       ✧    .     .   .      ✧                                                               \s
                 ✧    .___________..______       __    __    ______  __  ___  _______ .______     \s
                      |           ||   _  \\  .   |  |  |  |  /      ||  |/  / |   ____||   _  \\  .  \s ✷
              .       `---|  |----`|  |_)  |    |  |  |  | |  ,----'|  '  /  |  |__   |  |_)  |   \s
                    ✦     |  |     |      /     |  |  |  | |  |     |    <   |   __|  |      /    \s  .    .
               .          |  |  ✦  |  |\\  \\----.|  `--'  | |  `----.|  .  \\  |  |____ |  |\\  \\----.   .✷   .
             ✦   .  .     |__|     | _| `._____| \\______/   \\______||__|\\__\\ |_______|| _| `._____|  ✷ .
                            ✧     ✨   ✶     ⋆    .   ✷        ⋆       ✧       ✷     .    ✧     ✨                                                            \s
              .           ✦   ⋆      ✷    ✧     .     ⋆     ✶       ✧     ✷       ⋆        ✷
                       .     ✶     ⋆     .       ✧       ⋆      ✨        ✶      ⋆         ✧ 
            """;


    public void setOther_players_local(List<Player> players) {
        this.other_players_local = players;
    }

    /**
     * Funzione generica per leggere un intero con validazione.
     *
     * @param prompt Messaggio da mostrare all'utente
     * @param minVal Valore minimo accettato (incluso)
     * @param maxVal Valore massimo accettato (incluso)
     * @param allowExit Se true, permette di inserire -1 come valore di uscita
     * @param excludedValues Valori che non si possono selezionare
     * @return Il valore letto oppure -1 se allowExit è true e si vuole uscire
     */
    private int readValidInt(String prompt, int minVal, int maxVal, boolean allowExit, Set<Integer> excludedValues) {
        int value = 0;
        String fullPrompt = prompt + (allowExit ? " (-1 per uscire)" : "") + ": ";

        // Memorizza il prompt per quando il menu si chiude
        if (!isMenuOpen) {
            lastRequest = fullPrompt;
        }

        do {
            out.print(fullPrompt);
            String inputLine = readLine();

            if (inputLine == null) {
                out.println(RED + "Lettura input interrotta." + RESET);
                return Integer.MIN_VALUE;
            }

            String trimmedInput = inputLine.trim();

            if (trimmedInput.equalsIgnoreCase("/menu")) {
                if (isMenuOpen) {
                    out.println(YELLOW + "Menu già attivo. Scegli un'opzione valida o esci con -1." + RESET);
                    fullPrompt = "Scelta (-1 per uscire): ";
                    continue;
                } else {
                    showMenu();

                    if (!isMenuOpen) {
                        out.print(lastRequest);
                    }
                    value = Integer.MIN_VALUE;
                    continue;
                }
            }


            if (allowExit && trimmedInput.equalsIgnoreCase("-1")) {
                return -1;
            }

            try {
                value = Integer.parseInt(trimmedInput);
                if (value < minVal || value > maxVal) {
                    out.println(RED + "Errore: il valore deve essere compreso tra " + minVal + " e " + maxVal + "." + RESET);
                    value = Integer.MIN_VALUE;
                } else if (excludedValues.contains(value)) {
                    out.println(RED + "Errore: il valore " + value + " non è selezionabile." + RESET);
                    value = Integer.MIN_VALUE;
                }
            } catch (NumberFormatException e) {
                out.println(RED + "Errore: inserisci un numero valido." + RESET);
                value = Integer.MIN_VALUE;
            }

        } while (value == Integer.MIN_VALUE);

        return value;
    }

    private int readValidInt(String prompt, int minVal, int maxVal, boolean allowExit) {
        return readValidInt(prompt, minVal, maxVal, allowExit, Collections.emptySet());
    }


    public void setPlayer_local(Player player) {

        this.player_local = player;
    }

    public void setLocal_board_position(Map<Integer,Player> local_board) {
        this.local_board_positions = local_board;
    }

    public void setLocal_board_laps(Map<Integer,Player> local_board_laps) {
        this.local_board_laps = local_board_laps;
    }


    public void setLocal_extra_components(List<CardComponent> extraComponents) {
        this.local_extra_components = extraComponents;
    }

    public void setLocal_adventure_deck(Map<Direction, List<CardAdventure>> local_adventure_deck) {
        this.local_adventure_deck = local_adventure_deck;
    }

    public TUI() {
        this.out = System.out;
        out.println(PURPLE + banner + RESET);


        new Thread(() -> {
            Scanner dedicatedScanner = new Scanner(System.in); // Scanner dedicato per questo thread
            while (true) {
                try {
                    if (!dedicatedScanner.hasNextLine()) {
                        break;
                    }
                    String userInput = dedicatedScanner.nextLine();
      inputQueue.put(userInput);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    out.println(RED + "Thread di input interrotto." + RESET);
                    break;
                } catch (NoSuchElementException e) {
                    out.println(RED + "Stream di input chiuso (EOF)." + RESET);
                    break;
                } catch (Exception e) {
                    out.println(RED + "Errore imprevisto nel thread di input: " + e.getMessage() + RESET);
                    e.printStackTrace();
                }
            }

        }).start();


    }

    public void showMenu() {
        isMenuOpen = true;

        out.println("\n=== MENU ===");
// Sezione comandi
        out.println("[1] Mostra stato nave");
        out.println("[2] Mostra tutti i giocatori");
        out.println("[3] Mostra il Tabellone");
        out.println("[4] Visualizza le Carte Avventura");
        out.println("[5] Visualizza le Carte Prenotate");

      int scelta = readValidInt("Scelta ",1,5,true);

        switch (scelta) {
            case 1 -> showPlayer(player_local);

            case 2 -> {
                out.println("\n👥 Giocatori avversari:");
                for (Player p : other_players_local) {
                    showPlayer(p);
                }
            }

            case 3-> showBoard(local_board_positions,local_board_laps);

            case 4 -> showAdventureDeck(local_adventure_deck);

            case 5 -> showExtraCard();

            case -1 -> out.println("🔙 Uscita dal menu.");
        }

        isMenuOpen = false;
        out.println();

    }

    private void showExtraCard(){
        if (local_extra_components == null || local_extra_components.isEmpty()) {
            out.println("\n=== MAZZO EXTRA VUOTO ===");
            return;
        }
        System.out.println("\n=== EXTRA CARD ===");
        for(CardComponent c : local_extra_components) {
            out.println();
            printCard(c);
            out.println();
        }
    }

    private void showAdventureDeck(Map<Direction, List<CardAdventure>> local_adventure_deck) {

        if (local_adventure_deck == null || local_adventure_deck.isEmpty()) {
            out.println("\n=== MAZZO AVVENTURA VUOTO ===");
            return;
        }
        for (Direction d : local_adventure_deck.keySet()) {
            out.println("\n=== ADVENTURE DELLA PILA " + d.toString().toUpperCase() + " ===");

                out.println();
                printCardAdventure(local_adventure_deck.get(d).getFirst());
                out.println();

        }

    }


    private int readInt() {
        while (true) {
            String in = readLine();

            if (in.equalsIgnoreCase("/menu")) {
                out.println("Input non valido, inserisci un numero:");
                continue;
            }

            try {
                return Integer.parseInt(in);
            } catch (NumberFormatException e) {
                out.println("Input non valido, inserisci un numero:");
            }
        }
    }


    private String readLine() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ""; // fallback
        }
    }


    @Override
    public String chooseConnection() {
        String reply;
        do {
            System.out.println("""
                    Choose connection type and ip address:\s
                    For local host just the type of connection
                    [1]: for Socket
                    [2]: for RMI""");
            Scanner input = new Scanner(System.in);
            reply = input.next();
        } while (!(reply.equals("1") || reply.equals("2")));
        if (reply.equals("1")) {
            System.out.println("Socket connection chosen");
        } else {
            System.out.println("RMI connection chosen");
        }
        return reply;
    }


    @Override
    public void updateLocalPlayer(Player localPlayer) {
        this.player_local = localPlayer;
    }

    @Override
    public void updateOtherPlayers(List<Player> otherPlayers) {
        this.other_players_local = otherPlayers;
    }

    @Override
    public void updateAdventureDeck(Map<Direction, List<CardAdventure>> adventureDeck) {

    }

    @Override
    public void updateFacedUpCards(List<CardComponent> facedUpDeck) {

    }

    @Override
    public void showMessage(String message) {


        out.println(message);
    }

    @Override
    public String askNickname() {
        out.println("INSERISCI IL TUO NICKNAME : ");
        return readLine();

    }

    @Override
    public String getInput() {
        return readLine();
    }


    @Override
    public int askCreateOrJoin() {
        lastRequest = "PREMERE: \n 1 PER CREARE UNA LOBBY \n 2 PER ENTRARE IN UNA LOBBY ";
        out.println(lastRequest);
        int resp = readValidInt("Scelta ",1,2,false);

        return resp;

    }

    @Override
    public int askNumPlayers() {
        lastRequest = "Inserisci il numero di player della lobby (2-4): ";
        out.println(lastRequest);

        int resp = readValidInt("Scelta ",2,4,true);
        out.println("Lobby creata rimani in attesa che altri player entrino!");
        return resp;
    }

    @Override
    public void showGenericError(String error) {
        System.out.println();
        out.println(RED + error + RESET);
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        lastRequest = "Inserisci il numero della lobby , -1 altrimenti:";
        System.out.println(lastRequest);
        for (Integer lobby : lobbies) {

            System.out.println("Lobby n : " + lobby);

        }
        int resp = readInt();

        while (resp != -1 && !lobbies.contains(resp)) {
            System.out.println("Risposta non Valida,riprova : ");
            resp = readInt();

        }

        return resp;

    }


    @Override
    public Color askColor(List<Color> colors) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            lastRequest = "\nScegli il colore : ";
            System.out.println("\nScegli il colore che preferisci tra i seguenti disponibili:\n");
            int i = 0;
            for (Color c : colors) {
                System.out.println( " " +  i+ " - " + c.name().toLowerCase() + "  ");
                i++;
            }
            System.out.println();

            int scelta = readValidInt("Scelta ",0,3,false);
            try {
                Color selected;

                switch (scelta){

                    case 1: selected = Color.GREEN; break;
                    case 2: selected = Color.YELLOW; break;
                    case 3: selected = Color.BLUE; break;
                    default: selected = Color.RED; break;

                }


                    return selected;

            } catch (IllegalArgumentException e) {
                System.out.println("Input non valido. Assicurati di scrivere correttamente il nome di un colore.");
            }
        }
    }





    @Override
    public void printShip(CardComponent[][] ship){
        int CELL_WIDTH = 11;
        int rows = ship.length;
        int cols = ship[0].length;

        // Stampa intestazione colonne centrata
        StringBuilder colNumbers = new StringBuilder(" ".repeat(CELL_WIDTH));
        for (int c = 0; c < cols; c++) {
            colNumbers.append(center(String.valueOf(c), CELL_WIDTH));
        }
        System.out.println(colNumbers);

        for (int r = 0; r < rows; r++) {
            StringBuilder top = new StringBuilder();
            StringBuilder mid = new StringBuilder();
            StringBuilder bot = new StringBuilder();

            for (int c = 0; c < cols; c++) {
                CardComponent card = ship[r][c];

                if(card == null){
                    top.append(" ".repeat(CELL_WIDTH));
                    mid.append(" ".repeat(CELL_WIDTH));
                    bot.append(" ".repeat(CELL_WIDTH));
                }else

                if (card.getComponentType() == NotAccessible) {
                    String redX = center(" ⛔️ ", CELL_WIDTH);
                    top.append(" ".repeat(CELL_WIDTH));
                    mid.append(redX);
                    bot.append(" ".repeat(CELL_WIDTH));
                }
                else if (card.getComponentType() == Empty) {
                    top.append(" ".repeat(CELL_WIDTH));
                    mid.append(" ".repeat(CELL_WIDTH));
                    bot.append(" ".repeat(CELL_WIDTH));
                }
                else {
                    String topStr = printConnector(card.getConnector_type(North), North);
                    String midStr = printConnector(card.getConnector_type(West), West)
                            + printCard(card.getComponentType())
                            + printConnector(card.getConnector_type(East), East);
                    String botStr = printConnector(card.getConnector_type(South), South);

                    top.append(center(topStr, CELL_WIDTH));
                    mid.append(center(midStr, CELL_WIDTH));
                    bot.append(center(botStr, CELL_WIDTH));
                }
            }

            // Stampa le tre righe con il numero di riga SOLO nella mid
            System.out.println(" ".repeat(CELL_WIDTH) + top);
            System.out.println(center(String.valueOf(r), CELL_WIDTH) + mid);
            System.out.println(" ".repeat(CELL_WIDTH) + bot);
            System.out.println(" ".repeat(CELL_WIDTH) + "-".repeat(cols * CELL_WIDTH));
            System.out.println("\n");
        }
    }

    @Override
    public void earlyEndFlightResume(Player player){
        System.out.println("Ecco un resoconto della tua fase di volo: ");
        System.out.println("Crediti: " +player.getCredits());

    }







    @Override
    public void printShipPieces(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        System.out.println("\nEcco i pezzi rimasti della tua nave: ");
        int i = 0;

        for (List<Pair<Integer, Integer>> piece : pieces) {
            CardComponent[][] ship_board = new CardComponent[5][7];

            System.out.println(i + ": \n");

            for (Pair<Integer, Integer> pair : piece) {
                ship_board[pair.getKey()][pair.getValue()] = ship[pair.getKey()][pair.getValue()];
            }

            printShip(ship_board);
            i++;
            System.out.println();

        }

    }

    @Override
    public int askPiece(List<List<Pair<Integer, Integer>>> pieces, CardComponent[][] ship) {
        printShipPieces(pieces, ship);
        int choice = readValidInt("\nScelta Troncone ",0,pieces.size()-1,false);


        return choice;

    }

    private static String printConnector(ConnectorType connector, Direction direction) {
        switch (connector) {
            case Smooth: {
                return direction == North || direction == South ? "   " : "  ";
            }

            case Single: {
                switch (direction) {
                    case South, North:
                        return " | ";
                    case East, West:
                        return " -- ";
                }
            }

            case Double: {
                switch (direction) {
                    case South, North:
                        return " ||";
                    case East, West:
                        return " == ";
                }
            }

            case Universal: {
                switch (direction) {
                    case South, North:
                        return "|||";
                    case East, West:
                        return " ≡≡ ";
                }
            }

            case Engine_Connector: {
                return " 🚀 ";
            }

            case Cannon_Connector:
                return " 🔫 ";

            default:
                return "    ";
        }
    }

    @Override
    public void printCardAdventure(CardAdventure card) {

        System.out.println("\n----------------------------------------------------------------------\n");

        switch (card.getType()) {

            case OpenSpace: {
                System.out.println("\n SPAZIO APERTO: \n");
                System.out.println("Avanzi di tanti passi quanto la tua potenza motrice");
                break;
            }

            case AbandonedShip: {
                System.out.println("\n NAVE ABBANDONATA: \n");
                System.out.println("Perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Rinunci a " + ((AbandonedShip) card).getCrewmates_loss() + " membri dell'equipaggio");
                System.out.println("Guadagni " + ((AbandonedShip) card).getGiven_credits() + " crediti");
                break;
            }
            case AbandonedStation: {
                System.out.println("\n STAZIONE ABBANDONATA: \n");
                System.out.println("Hai almeno " + ((AbandonedStation) card).getNeeded_crewmates() + " membri dell'equipaggio?");
                System.out.println("Perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Guadagni un carico merci");
                printCargo(((AbandonedStation) card).getCargo());
                break;
            }
            case Planets: {
                System.out.println("\n PIANETI: \n");
                System.out.println("Perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Puoi posizionarti sui pianeti con i seguenti carichi merci");
                for (int i = 0; i < ((Planets) card).getCargo_reward().size(); i++) {
                    System.out.println("Pianeta " + i + ":");
                    printCargo(((Planets) card).getCargos(i));
                }

                break;
            }
            case Smugglers: {
                System.out.println("\n CONTRABBANDIERI: \n");
                System.out.println("Hai piu di " + ((Smugglers) card).getCannons_strenght() + " potenza di fuoco?");
                System.out.println("Puoi vincere un carico merci");
                printCargo(((Smugglers) card).getCargo_rewards());
                System.out.println("Ma perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Hai meno potenza di fuoco");
                System.out.println("Perdi il seguente numero di merci " + ((Smugglers) card).getCargo_loss());
                break;
            }
            case CombatZone: {
                System.out.println("\n ZONA DI GUERRA: \n");
                if (((CombatZone) card).getId() == 1) {
                    System.out.println("Il giocatore con meno membri dell'equipaggio perde " + card.getCost_of_days() + " giorni di volo");
                    System.out.println("Il giocatore con meno forza motrice perde " + ((CombatZone) card).getCrewmates_loss() + " membri dell'equipaggio");
                    System.out.println("Il giocatore con meno potenza di fuoco prende la seguente scarica di meteoriti:");
                    printMeteors(((CombatZone) card).getMeteors());
                } else {
                    System.out.println("Il giocatore con meno potenza di fuoco perde " + card.getCost_of_days() + " giorni di volo");
                    System.out.println("Il giocatore con meno forza motrice perde" + ((CombatZone) card).getCargo_loss() + " merci");
                    System.out.println("Il giocatore con meno membri dell'equipaggio prende la seguente scarica di meteoriti:");
                    printMeteors(((CombatZone) card).getMeteors());
                }
                break;
            }
            case Epidemic: {
                System.out.println("\n EPIDEMIA: \n");
                System.out.println("Ogni giocatore perde 1 astronauta per ogni Living Unit adiacente ");

                break;
            }
            case MeteorSwarm: {
                System.out.println("\n PIOGGIA DI METEORITI: \n");
                System.out.println("In arrivo la seguente scarica di meteoriti:");
                printMeteors(((MeteorSwarm) card).getMeteors());
                break;
            }
            case Pirates: {
                System.out.println("\n PIRATI: \n");
                System.out.println("Hai una potenza di fuoco maggiore di " + ((Pirates) card).getCannons_strenght() + "?");
                System.out.println("Puoi vincere " + ((Pirates) card).getCredits() + " crediti");
                System.out.println("Ma perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Hai meno potenza di fuoco? prendi la seguente scarica di meteoriti:");
                printMeteors(((Pirates) card).getMeteors());
                break;
            }
            case Slavers: {
                System.out.println("\n SCHIAVISTI: \n");
                System.out.println("Hai una potenza di fuoco maggiore di " + ((Slavers) card).getCannons_strenght() + "?");
                System.out.println("Puoi vincere " + ((Slavers) card).getCredits() + " crediti");
                System.out.println("Ma perdi " + card.getCost_of_days() + " giorni di volo");
                System.out.println("Hai meno potenza di fuoco? perdi " + ((Slavers) card).getAstronaut_loss() + " membri dell'equipaggio");
                break;
            }
            case Stardust: {
                System.out.println("\n POLVERE STELLARE: \n");
                System.out.println("Perdi 1 giorno di prova per ogni connettore esposto!");
                break;
            }


        }
        System.out.println("\n----------------------------------------------------------------------\n");

    }


    @Override
    public int askPlanet(List<List<Cargo>> planets, Set<Integer> planets_taken) {
        System.out.println("\n====== LISTA PIANETI DISPONIBILI ======\n");
        for (int i = 0; i < planets.size(); i++) {
            System.out.print("Pianeta " + i + " : ");
            if(planets_taken.contains(i)){
                System.out.println(RED + " PIANETA PRESO " + RESET);
            }else {
                System.out.println(GREEN + " PIANETA DISPONIBILE " + RESET);
            }
            printCargo(planets.get(i));
        }
        return readValidInt("Seleziona il numero del pianeta", 0, planets.size() - 1, true, planets_taken);
    }



    @Override
    public Boolean acceptAdventure(String prompt) {
        System.out.println();
        System.out.println(prompt);
        System.out.println("\t 1 : ACCETTA\n\t 2 : RIFIUTA");
        int choice = readValidInt("Scelta ", 1,2,false);
        return choice == 1;
    }

    @Override
    public int askCargo(List<Cargo> cargos) {

        System.out.println("\nScegli quale cargo vuoi Posizionare : \n");

        printCargo(cargos);
        return readValidInt("Scelta ", 0,cargos.size()-1,true);

    }

    public void printNonEmptyCargos(List<Cargo> cargos){
        // Intestazione compatta
        System.out.println("\n  Posizione   Colore");
        System.out.println("  ---------  --------------");
        for (int i = 0; i < cargos.size(); i++) {
            String label;
            if(cargos.get(i)!=Cargo.Empty){
            switch (cargos.get(i)) {
                case Blue:   label = "🔵  Blu";   break;
                case Yellow: label = "\uD83D\uDFE1  Giallo"; break;
                case Green:  label = "\uD83D\uDFE2  Verde";  break;
                case Red:    label = "🔴  Rosso";  break;
                default:     label = "⚪ Vuoto";        break;
            }
            System.out.printf("    %2d       %s%n", i, label);
        }
        }
        System.out.println();
    }

    public void printCargo(List<Cargo> cargos) {
        // Intestazione compatta
        System.out.println("\n  Posizione   Colore");
        System.out.println("  ---------  --------------");
        for (int i = 0; i < cargos.size(); i++) {
            String label;
            switch (cargos.get(i)) {
                case Blue:   label = "🔵  Blu";   break;
                case Yellow: label = "\uD83D\uDFE1  Giallo"; break;
                case Green:  label = "\uD83D\uDFE2  Verde";  break;
                case Red:    label = "🔴  Rosso";  break;
                default:     label = "⚪ Vuoto";        break;
            }
            System.out.printf("    %2d       %s%n", i, label);
        }
        System.out.println();
    }

    @Override
    public void printMeteor(Pair<MeteorType, Direction> meteor, int coord) {
        StringBuilder meteorStr = new StringBuilder();
        switch (meteor.getKey()) {
            case LargeMeteor -> meteorStr.append(" - METEORA GROSSA " );
            case SmallMeteor -> meteorStr.append(" - METEORA PICCOLA ");
            case HeavyCannonFire -> meteorStr.append(" - CANNONATA PESANTE ");
            case LightCannonFire -> meteorStr.append(" - CANNONATA LEGGERA ");
        }
        meteorStr.append(" in arrivo ");
        switch (meteor.getValue()) {
            case South -> meteorStr.append("da SUD");
            case East -> meteorStr.append("da EST");
            case West -> meteorStr.append("da OVEST");
            case North -> meteorStr.append("da NORD");
        }


        meteorStr.append(" alla coordinata : ").append(coord);
        String content = meteorStr.toString();
        int length = content.length();
        String border = "═".repeat(length + 4);

        System.out.println();
        printBorder(content);
        System.out.println();


    }

    public void printBorder(String content) {

        String contentWithoutAnsi = content.replaceAll("\u001B\\[[;\\d]*m", "");

        String[] lines = contentWithoutAnsi.split("\\R");
        int maxLength = 0;

        for (String line : lines) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }

        String border = "═".repeat(maxLength + 4);
        System.out.println("╔" + border + "╗");


        String[] originalLines = content.split("\\R");
        for (String line : originalLines) {

            System.out.printf("║  %-" + maxLength + "s  ║%n", line);
        }

        System.out.println("╚" + border + "╝");
    }



    @Override
    public int nextMeteor() {

        System.out.println("\n PREMERE : \n" +
                " 1 - per continuare / rimanere in attesa \n");
        int choice = readValidInt("Scelta ", 1,1,false);
            return choice;
    }

    @Override
    public void showHittedCard(CardComponent card, Direction direction) {
        int CELL_WIDTH = 11;

        System.out.println("CARTA COLPITA : ");

        // Componenti centrali
        String topStr = center(printConnector(card.getConnector_type(North), North), CELL_WIDTH);
        String cardStr = printCard(card.getComponentType());
        String midStr = center(
                printConnector(card.getConnector_type(West), West)
                        + cardStr +
                        printConnector(card.getConnector_type(East), East),
                CELL_WIDTH
        );
        String botStr = center(printConnector(card.getConnector_type(South), South), CELL_WIDTH);

        // Riga extra sopra/sotto per freccia verticale
        String arrowTop = " ".repeat(CELL_WIDTH);
        String arrowBot = " ".repeat(CELL_WIDTH);

        // Frecce laterali
        String sideLeft = "  ";
        String sideRight = "  ";

        switch (direction) {
            case North:
                arrowTop = center("↓", CELL_WIDTH);
                break;
            case South:
                arrowBot = center("↑", CELL_WIDTH);
                break;
            case West:
                sideLeft = "→ ";
                break;
            case East:
                sideRight = " ←";
                break;
        }

        // Stampa finale
        System.out.println();
        System.out.println("TIPO : " + card.getComponentType());
        System.out.println(arrowTop);
        System.out.println("  " + topStr);
        System.out.println(sideLeft + midStr + sideRight);
        System.out.println("  " + botStr);
        System.out.println(arrowBot);

        if(card.getComponentType() == BlueStorage || card.getComponentType() == RedStorage){

            System.out.println("\n LISTA DI CARGO : \n");
            printCargo(((Storage)card).getCarried_cargos());
            System.out.println();


        }

        if(card.getComponentType() == Battery ){

            System.out.println("\n BATTERIE : \n");
            System.out.println(" Dim : " + ((Battery)card).getSize() + " Immagazzinate : "+ ((Battery)card).getStored());

            System.out.println();


        }

        if(card.getComponentType() == LivingUnit || card.getComponentType() == MainUnitGreen || card.getComponentType() == MainUnitBlue || card.getComponentType() == MainUnitRed || card.getComponentType() == MainUnitYellow  ){

            System.out.println("\n EQUIPAGGIO : \n");
            System.out.println(" Tipo : " +   " Numero : "+ ((LivingUnit)card).getNum_crewmates());
            System.out.println();


        }



    }


    @Override
    public void printMeteors(List<Pair<MeteorType, Direction>> meteors) {
        for (Pair<MeteorType, Direction> meteor : meteors) {
            switch (meteor.getKey()) {
                case LargeMeteor -> System.out.print(" - Meteora grossa ");
                case SmallMeteor -> System.out.print(" - Meteora piccola ");
                case HeavyCannonFire -> System.out.print(" - Cannonata pesante ");
                case LightCannonFire -> System.out.print(" - Cannonata leggera ");
            }
            switch (meteor.getValue()) {
                case South -> System.out.println("da sud");
                case East -> System.out.println("da est");
                case West -> System.out.println("da ovest");
                case North -> System.out.println("da nord");
            }
        }
    }


    @Override
    public int selectDeck() {

        if(player_local != null) {
        System.out.println("\nECCO LA TUA NAVE : \n");

        printShip(player_local.getShip().getShipBoard());
        System.out.println("\n");
}
        lastRequest = ("Premi :\n 1 : per prendere una carta casuale\n 2 : per scegliere dal mazzo delle carte scoperte\n 3 : per usare una carta prenotata \n 4 : terminare l'assemblaggio\n");

            out.println("Premi :\n 1 : per prendere una carta casuale\n 2 : per scegliere dal mazzo delle carte scoperte\n 3 : per usare una carta prenotata \n 4 : terminare l'assemblaggio\n");
        return readValidInt("Scelta" , 1 , 4, false);


    }

    @Override

    public int crewmateAction(Pair<Integer, Integer> coords) {
        CardComponent[][] ship = player_local.getShip().getShipBoard();
        CardComponent component = ship[coords.getKey()][coords.getValue()];
        List<CrewmateType> crewmateType = player_local.getShip().checkAlienSupport(component);

        System.out.println("\nSTAI RIMEPIENDO LA LIVING UNIT IN RIGA: " +
                coords.getKey() + " COLONNA: " + coords.getValue() + "\n");

        String prompt;
        int choice;

        if (crewmateType.contains(CrewmateType.BrownAlien) && crewmateType.contains(CrewmateType.PinkAlien)) {
            prompt = """
                \nPremi:
                 1: per aggiungere un astronauta
                 2: per aggiungere un alieno rosa
                 3: per aggiungere un alieno marrone\n""";
            System.out.println(prompt);
            choice = readValidInt("Scelta", 1, 3,false);

        } else if (crewmateType.contains(CrewmateType.BrownAlien)) {
            prompt = """
                Premi:
                 1: per aggiungere un astronauta
                 2: per aggiungere un alieno marrone""";
            System.out.println(prompt);
            choice = readValidInt("Scelta", 1, 2,false);

        } else if (crewmateType.contains(CrewmateType.PinkAlien)) {
            prompt = """
                Premi:
                 1: per aggiungere un astronauta
                 2: per aggiungere un alieno rosa""";
            System.out.println(prompt);
            choice = readValidInt("Scelta", 1, 2,false);

        } else {
            prompt = """
                Premi:
                 1: per aggiungere un astronauta""";
            System.out.println(prompt);
            choice = readValidInt("Scelta", 1, 1,false);
        }

        return choice;
    }


    public void printCard(CardComponent card) {

        int CELL_WIDTH = 11;
        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();
        String topStr = printConnector(card.getConnector_type(North), North);
        String midStr = printConnector(card.getConnector_type(West), West)
                + printCard(card.getComponentType())
                + printConnector(card.getConnector_type(East), East);
        String botStr = printConnector(card.getConnector_type(South), South);
        top.append(center(topStr, CELL_WIDTH));
        mid.append(center(midStr, CELL_WIDTH));
        bot.append(center(botStr, CELL_WIDTH));

        System.out.println("TIPO : " + card.getComponentType());

        System.out.println(top);
        System.out.println(mid);
        System.out.println(bot);

    }


    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        lastRequest = "Premi l'indice della carta che vuoi prendere (-1 per uscire)\n";
        out.println("Premi l'indice della carta che vuoi prendere (-1 per uscire)\n");

        for (int i = 0; i < cards.size(); i++) {
            System.out.println("NUMERO : " + i);
            printCard(cards.get(i));
        }

        while (true) {
            System.out.print("Scelta: ");
            int selected = readInt();

            if (selected == -1) {
                return -1;
            }

            if (selected >= 0 && selected < cards.size()) {
                return selected;
            }

            System.out.println("Indice non valido. Riprova.");
        }
    }


    @Override
    public int askSecuredCard(List<CardComponent> cards) {
        lastRequest = "Premi l'indice della carta che vuoi prendere (-1 per uscire)\n";
        System.out.println("Premi l'indice della carta che vuoi prendere (-1 per uscire)\n");


        for (int i = 0; i < cards.size(); i++) {
            System.out.println("NUMERO : " + i);
            printCard(cards.get(i));
        }


        while (true) {
            System.out.print("Scelta: ");
            int selected = readInt();

            if (selected == -1) {
                return -1;
            }

            if (selected >= 0 && selected < cards.size()) {
                return selected;
            }

            System.out.println("Indice non valido. Riprova.");
        }
    }

    @Override
    public void printFinalRanks(List<Player> finalRanks) {
        StringBuilder sb = new StringBuilder();

        sb.append("CLASSIFICA FINALE \n");
        if (finalRanks.isEmpty()) {
            sb.append("Nessun giocatore da mostrare.");
        } else {
            finalRanks.sort((p1, p2) -> Integer.compare(p2.getCredits(), p1.getCredits()));

            for (int i = 0; i < finalRanks.size(); i++) {
                Player player = finalRanks.get(i);
                sb.append(String.format("%d. %s - Crediti: %d%n",
                        (i + 1), player.getNickname(), player.getCredits()));
            }
        }

        printBorder(sb.toString());
    }


    @Override
    public void ShowRanking(Map<String, Double> rank, String type) {
        System.out.println("\n Lista di " + type + ":\n");

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(rank.entrySet());

        sorted.sort(Comparator.comparing(Map.Entry<String, Double>::getValue).reversed());

        Double minValue = sorted.stream()
                .map(Map.Entry::getValue)
                .min(Double::compare)
                .orElse(Double.MIN_VALUE);

        String firstMinKey = null;
        for (Map.Entry<String, Double> entry : rank.entrySet()) {
            if (entry.getValue() == minValue) {
                firstMinKey = entry.getKey();
                break;
            }
        }

        for (Map.Entry<String, Double> entry : sorted) {
            String name = entry.getKey();
            Double value = entry.getValue();
            String color = name.equals(firstMinKey) && value == minValue ? RED : "";
            String symbol = name.equals(firstMinKey) && value == minValue ? "  <- DEVE PAGARE LA PENITENZA" : "";
            System.out.println(color + "- " + name + ": " + value +  symbol +  RESET );
        }
    }


    @Override
    public Pair<Integer, Integer> askLivingUnit(Ship ship) {

        CardComponent[][] plance = player_local.getShip().getShipBoard();
        List<CardComponent> list_lu = new ArrayList<>();
        for(int i = 0 ; i < ship.getROWS() ; i++) {


            for(int j = 0 ; j < ship.getCOLS() ; j++) {

                CardComponent card = plance[i][j];

                if(card.getComponentType() == MainUnitGreen || card.getComponentType() == MainUnitRed
                || card.getComponentType() == MainUnitBlue || card.getComponentType() == MainUnitYellow || card.getComponentType() == LivingUnit) {
                    LivingUnit lu = (LivingUnit) card;
                    if(lu.getNum_crewmates() > 0)  list_lu.add(card);

                }


            }


        }
        System.out.println("SCEGLI DOVE VUOI TOGLIERE 1 CREWMATE : ");
        int i = 0;
        for( CardComponent card : list_lu ) {
            Pair<Integer,Integer> coord = ship.getCoords(card);
            System.out.println(" " + i + " -  RIGA " +coord.getKey() + " COLONNA " + coord.getValue() );

        }

        int scelta = readValidInt("\nSCELTA " ,0,list_lu.size()-1,false  );
        return ship.getCoords(list_lu.get(scelta));


    }

    @Override
    public int showCard(CardComponent card) {



        printCard(card);
        lastRequest = "Premi :\n 1 : per ruotare la carta in senso orario\n 2 : per inserirla \n 3 : per scartarla \n 4 : per prenotarla ";
        out.println("Premi :\n 1 : per ruotare la carta in senso orario\n 2 : per inserirla \n 3 : per scartarla \n 4 : per prenotarla ");
            return readValidInt("Scelta", 1, 4,false);
    }


    @Override
    public Pair<Integer, Integer> askCoords(Ship ship) {
        int x = -1, y = -1;
        boolean validInput = false;

        while (!validInput) {
            lastRequest = "Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1) + " oppure -1 per uscire): ";
            out.println("Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1) + " oppure -1 per uscire): ");
            x = readInt();
            if (x == -1) return new Pair<>(x, y);

            lastRequest = "Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1) + " oppure -1 per uscire): ";
            out.println("Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1) + " oppure -1 per uscire): ");
            y = readInt();
            if (y == -1) return new Pair<>(x, y);

            if (x < 0 || x >= ship.getROWS() || y < 0 || y >= ship.getCOLS()) {
                out.println("Errore: le coordinate sono fuori dai limiti. Riprova.");
            } else {
                // Verifica se la posizione nella nave contiene un componente
                CardComponent component = ship.getComponent(x, y);
                if (component.getComponentType() == Empty) {
                    out.println("Coordinate valide ! ");

                    validInput = true;  // Esci dal ciclo se la posizione è valida
                } else {
                    out.println("Errore: c'è già una carta in questa poszione.");
                }
            }
        }

        // Restituisci le coordinate valide
        return new Pair<>(x, y);
    }



    @Override
    public Pair<Integer, Integer> askCoordsCrewmate(Ship ship) {
        System.out.println("TUTTI HANNO FINITO, comincia la fase di supply delle Living Unit");
        int x = -1, y = -1;
        boolean validInput = false;
        System.out.println("Premi 1 per continuare oppure premi -1 per uscire e passare alla fase successiva :");

        if(x==-1) return new Pair<>(99, 99);
        while (!validInput) {
            lastRequest = "Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1) + " oppure -1 per uscire): ";
            out.println("Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1) + " oppure -1 per uscire): ");
            x = readInt();
            if (x == -1) return new Pair<>(x, y);

            lastRequest = "Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1) + " oppure -1 per uscire): ";
            out.println("Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1) + " oppure -1 per uscire): ");
            y = readInt();
            if (y == -1) return new Pair<>(x, y);

            if (x < 0 || x >= ship.getROWS() || y < 0 || y >= ship.getCOLS()) {
                out.println("Errore: le coordinate sono fuori dai limiti. Riprova.");
            } else {
                // Verifica se la posizione nella nave contiene un componente
                CardComponent component = ship.getComponent(x, y);
                if (component.getComponentType() == LivingUnit || ((LivingUnit)component).getNum_crewmates()>0) {
                    out.println("Sono gia presenti astronauti / alieni qui ! ");
                    if( ((LivingUnit)component).getNum_crewmates()==0) {// Mostra il componente trovato
                        validInput = true;
                    }// Esci dal ciclo se la posizione è valida
                } else {
                    out.println("Errore: non c'è una carta living unit in questa poszione.");
                }
            }
        }

        // Restituisci le coordinate valide
        return new Pair<>(x, y);
    }


    @Override
    public void showPlayer(Player player) {

        if (player == null) {
            System.out.println("Nessun dato disponibile !");
            return;
        }
        System.out.println("\n====== 👤 INFO GIOCATORE ======");
        System.out.println(" Nickname: " + player.getNickname());
        System.out.println(" Colore: " + player.getColor());
        System.out.println(" Lap completati: " + player.getNum_laps());
        System.out.println(" Crediti: " + player.getCredits());
        System.out.println(" Connettori esposti: " + player.getExposed_connectors());

          /*  if (player.getGame().getType().toString().equals("StandardGame")) {
                List<CardComponent> extra = player.getShip().getExtra_components();
                if (extra.isEmpty()) {
                    System.out.println("🗂️ Componenti extra: Nessuna");
                } else {
                    System.out.println("🗂️ Componenti extra:");
                    for (CardComponent comp : extra) {
                        System.out.println("   - " + comp);
                    }
                }
            }*/

        System.out.println("\n🛠️  Stato Nave:");
        printShip(player.getShip().getShipBoard()); // Se hai un toString dettagliato nella Ship, qui funziona
        out.println();

    }


    @Override
    public Ship removeInvalidsConnections(Ship ship, List<Pair<Integer, Integer>> connectors) {
        System.out.println("La tua nave presenta questi connettori esposti : ");
        for (Pair<Integer, Integer> connector : connectors) {
            System.out.println("RIGA :  " + connector.getKey() + " --- COLONNA :  " + connector.getValue());
        }
        System.out.println("\n");
        printShip(ship.getShipBoard());
        System.out.println("\n");

        System.out.println("\n Ora inserisci le coordinate del componente che vuoi rimuovere (qualunque eccetto Main unit)");

        while (!ship.checkShipConnections().isEmpty()){
            askCoordsToRemove(ship);

    }
        System.out.println("\n\nCONNETTORI SISTEMATI\n\n");
        return ship;
}


    public void askCoordsToRemove(Ship ship) {
        int x = -1, y = -1;

        boolean validInput = false;
        lastRequest = "\n RIMOZIONE COMPONENTE\n";
        System.out.println("\n RIMOZIONE COMPONENTE\n");

        while (!validInput) {
            lastRequest = "Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1 +")") ;
            out.println("Inserire la coordinata RIGA (tra 0 e " + (ship.getROWS() - 1) +")" );
            x = readInt();


            lastRequest = "Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1+")");
            out.println("Inserire la coordinata COLONNA (tra 0 e " + (ship.getCOLS() - 1) +")" );
            y = readInt();


            if (x < 0 || x >= ship.getROWS() || y < 0 || y >= ship.getCOLS()) {
                out.println("Errore: le coordinate sono fuori dai limiti. Riprova.");
            } else {
                // Verifica se la posizione nella nave contiene un componente
                CardComponent component = ship.getComponent(x, y);
                if (component.getComponentType() != MainUnitGreen && component.getComponentType() != MainUnitRed && component.getComponentType() != MainUnitBlue && component.getComponentType() != MainUnitYellow) {
                    out.println("RIMOZIONE VALIDA ed EFFETTUATA.");  // Mostra il componente trovato
                    validInput = true;  // Esci dal ciclo se la posizione è valida
                } else {
                    out.println("Errore: c'è una carta main living unit in questa posizione.");
                }
            }
        }

        // Restituisci le coordinate valide
        ship.removeComponent(x, y);
    }


    private static String printCard(ComponentType card) {
        return switch (card) {
            case Engine -> " E ";
            case DoubleEngine -> "DE ";
            case Battery -> " B ";
            case BlueStorage -> "BS ";
            case RedStorage -> "RS ";
            case Cannon -> " C ";
            case DoubleCannon -> "DC ";
            case BrownAlienUnit -> "BAU";
            case PinkAlienUnit -> "PAU";
            case LivingUnit -> "LU ";
            case MainUnitRed -> "MUR";
            case MainUnitBlue -> "MUB";
            case MainUnitGreen -> "MUG";
            case MainUnitYellow -> "MUY";
            case Tubes -> " T ";
            case Shield -> " S ";
            case Empty, NotAccessible -> "   ";
            default -> "   ";
        };
    }

    @Override
    public Pair<Integer,Integer> chooseAstronautLosses(Ship ship){

        List<Pair<Integer, Integer>> livingUnits = new ArrayList<>();

        System.out.println("\nScegli la living Unit dove perdere 1 membro dell'equipaggio : ");

        int choice=0;
        int num;
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component != null && component.getComponentType() == ComponentType.LivingUnit ||  component.getComponentType() == MainUnitRed
                || component.getComponentType() == MainUnitYellow || component.getComponentType() == MainUnitGreen || component.getComponentType() == MainUnitBlue) {
                    LivingUnit unit = (LivingUnit) component;
                    if (unit.getNum_crewmates() > 0) {
                        livingUnits.add(new Pair<>(i, j));
                    }
                }
            }
        }

        for (int idx = 0; idx < livingUnits.size(); idx++) {
            Pair<Integer, Integer> pos = livingUnits.get(idx);
            LivingUnit unit = (LivingUnit) ship.getComponent(pos.getKey(), pos.getValue());
            System.out.println("\t" + (idx) + ". Living Unit in (" + pos.getKey() + ", " + pos.getValue() + ") - Membri: " + unit.getNum_crewmates());
        }
        int scelta = readValidInt("\nScelta ", 0, livingUnits.size()-1,false);
        if (scelta == -1 ) return  new Pair<>(-1,-1);

        LivingUnit unit = (LivingUnit) ship.getComponent(livingUnits.get(scelta).getKey(),livingUnits.get(scelta).getValue());

        unit.removeCrewmates(1);

        return livingUnits.get(scelta);

    }

    @Override
    public Pair<Integer, Integer> askEngine(Pair<Integer, Integer> engine) {


        System.out.println( GREEN + "\nMOTORE DOPPIO" + RESET + " trovato a RIGA : " + engine.getKey()  + " COLONNA : " + engine.getValue());

        System.out.println("Premere :\n1 : usare batteria\n2 : NON usare batteria");
        int choice = readValidInt("Scelta " , 1, 2, false);

        if(choice == 2)return new Pair<>(-1,-1);

        else return  useBattery(player_local.getShip());


    }

    @Override
    public Pair<Integer, Integer> askCannon(Pair<Integer, Integer> cannon) {
        System.out.println( GREEN + "\nCANNONE DOPPIO" + RESET + " trovato a RIGA : " + cannon.getKey()  + " COLONNA : " + cannon.getValue());

        System.out.println("\nPremere :\n1 : usare batteria\n2 : NON usare batteria");
        int choice = readValidInt("Scelta " , 1, 2, false);

        if(choice == 2)return new Pair<>(-1,-1);

        else return  useBattery(player_local.getShip());
    }

    /*
    @Override
    public List<Pair<Integer,Integer>> askCannon() {
        Ship ship = player_local.getShip();
        List<Pair<Integer,Integer>> cannons = new ArrayList<>();
        int total = 0 ;
        for(int i = 0 ; i<ship.getROWS(); i++){
            for(int j = 0 ; j<ship.getCOLS(); j++) {
                if(ship.getComponent(i,j).getComponentType() == DoubleCannon){
                    System.out.println("\nDOUBLE CANNON a Riga : " + i + " Colonna" + j);

                    if(ship.getTotalBattery() >0){


                    System.out.println("\nPremere :\t1 : per usarlo come DOPPIO \t2 : per usarlo come SINGOLO   ");
                    int choice = readValidInt("Scelta ", 1 ,2 , false);
                    if(choice == 1){}

                }
            }




        }
return total;
    }
*/




    @Override
    public Map<CardComponent, Map<Cargo, Integer>> manageCargo(Ship ship){
        Map<CardComponent, Map<Cargo, Integer>> cargos = new HashMap<>();
        Map<Cargo, Integer> cargo = new HashMap<>();

        System.out.println("Stai per ricevere nuova merce! Premi:\n 1. per rimuovere cargo \n 2. lasciare le tue merci invariate");
        int choice = readInt();
        int n = 0;
        while(choice != 1 && choice != 2){
            if (choice == 1) {
                for(int i = 0 ; i<ship.getROWS(); i++){
                    for(int j = 0 ; j<ship.getCOLS(); j++) {
                        if ((ship.getComponent(i,j).getComponentType() == BlueStorage || ship.getComponent(i,j).getComponentType() == RedStorage) && ((Storage)ship.getComponent(i,j)).getCargoCount() > 0){
                            if(ship.getComponent(i,j).getComponentType() == BlueStorage)
                                System.out.println("BLU STORAGE alle coordinate x: "+i+" y:" +j);
                            else
                                System.out.println("RED STORAGE alle coordinate x: "+i+" y:" +j);
                            System.out.println("Capienza: " +((Storage)ship.getComponent(i,j)).getSize());
                            printCargo(((Storage)ship.getComponent(i,j)).getCarried_cargos());
                            System.out.println("Premi: \n 1. per rimuovere cargo \n 2. passare al prossimo storage ");
                            int choice1 = readInt();
                            while(choice1 != 1 && choice1 != 2 ){
                                System.out.println("Opzione non disponibile, reinserire:");
                                choice1 = readInt();
                            }
                            if (choice1 == 1){
                                int remove=0;
                                while(remove != -1){
                                    System.out.println("Premi: \n -1. per passare al prossimo cargo \n altrimenti il numero del cargo che vuoi rimuovere: ");
                                    printCargo(((Storage)ship.getComponent(i,j)).getCarried_cargos());
                                    System.out.println("");
                                    remove=readInt();

                                    while (remove<-1 || remove>((Storage)ship.getComponent(i,j)).getSize()){
                                        System.out.println("Opzione non disponibile, reinserire:");
                                        remove = readInt();
                                    }
                                    if(remove>=0 && remove <= ((Storage)ship.getComponent(i,j)).getSize()){
                                        cargo.put(((Storage)ship.getComponent(i,j)).getCargo(remove),1);
                                        cargos.put(ship.getComponent(i,j), cargo);
                                    }
                                }

                            }
                            else{
                                break;
                            }


                        }
                        else{
                            System.out.println("Non hai merci presenti sulla tua nave ");
                        }
                    }
                }

            }
            else if (choice == 2) {
                break;
            }
            else {
                System.out.println("Opzione non disponibile, reinserire:");
                choice = readInt();
            }
        }

        return cargos;
    }

    @Override
    public void removeCargo(Ship ship) {
        List<Pair<Integer,Integer>> storage_with_red = new ArrayList<>();
        List<Pair<Integer,Integer>> other_storage = new ArrayList<>();
        List<Pair<Integer,Integer>> batteries = new ArrayList<>();
        Pair<Integer,Integer> choice_final;



        for(int i = 0 ; i<ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent card = ship.getComponent(i, j);

                if (card.getComponentType() == RedStorage && ((Storage)card).containsCargo(Cargo.Red)  ) {

                    storage_with_red.add(new Pair<>(i,j));
                    other_storage.add(new Pair<>(i, j));


                } else if ((card.getComponentType() == BlueStorage || card.getComponentType() == RedStorage) && (((Storage)card).getCargoCount() > 0 )) {

                    other_storage.add(new Pair<>(i, j));
                } else if (card.getComponentType() == Battery && ((Battery)card).getStored() > 0) {

                    batteries.add(new Pair<>(i, j));

                }
            }
        }


        if(!storage_with_red.isEmpty()){
            for (Pair<Integer, Integer> s : storage_with_red) {
                CardComponent card = ship.getComponent(s.getKey(), s.getValue());

                if(((Storage)card).removeCargo(Cargo.Red)){

                    printBorder("HAI PERSO UN CARGO "+RED+" ROSSO" +RESET + " a RIGA : " + s.getKey()+ " COLONNA : " + s.getValue());



                    System.out.println();
                    return;
                }


            }


        } else if (!other_storage.isEmpty()) {


            for (Pair<Integer, Integer> s : other_storage) {
                CardComponent card = ship.getComponent(s.getKey(), s.getValue());

                if(((Storage)card).removeCargo(Cargo.Yellow)){

                    printBorder("HAI PERSO UN CARGO "+YELLOW+" GIALLO" +RESET + " a RIGA : " + s.getKey()+ " COLONNA : " + s.getValue() );
                    System.out.println();
                    return;
                }


            }


            for (Pair<Integer, Integer> s : other_storage) {
                CardComponent card = ship.getComponent(s.getKey(), s.getValue());

                if(((Storage)card).removeCargo(Cargo.Green)){

                    printBorder("HAI PERSO UN CARGO "+GREEN+" VERDE" +RESET + " a RIGA : " + s.getKey()+ " COLONNA : " + s.getValue() );
                    System.out.println();
                    return;
                }




            }


            for (Pair<Integer, Integer> s : other_storage) {
                CardComponent card = ship.getComponent(s.getKey(), s.getValue());

                if(((Storage)card).removeCargo(Cargo.Blue)){

                    printBorder("HAI PERSO UN CARGO "+BLUE+" BLU" +RESET + " a RIGA : " + s.getKey()+ " COLONNA : " + s.getValue() );
                    System.out.println();
                    return;
                }




            }



        }else{


            for (Pair<Integer, Integer> s : batteries) {
                CardComponent card = ship.getComponent(s.getKey(), s.getValue());

                if(((Battery)card).getStored() > 0) {

                    printBorder("HAI PERSO UNA "+GREEN+" BATTERIA" +RESET + " a RIGA : " + s.getKey()+ " COLONNA : " + s.getValue() );
                    ((Battery)card).removeBattery();
                    System.out.println();
                    return;
                }




            }


        }


        printBorder("NON HAI PERSO NIENTE!!! non avevi cargo o batterie! " );
        System.out.println();


    }

    @Override
    public Pair<Pair<Integer,Integer>, Integer> addCargo(Ship ship, Cargo cargo){
        List<Pair<Integer,Integer>> red_storage = new ArrayList<>();
        List<Pair<Integer,Integer>> other_storage = new ArrayList<>();
        Pair<Pair<Integer,Integer>,Integer> final_value = null;
        Pair<Integer,Integer> tmp_storage;

        for(int i = 0 ; i<ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {

                if (ship.getComponent(i, j).getComponentType() == RedStorage) {

                    red_storage.add(new Pair<>(i, j));
                    other_storage.add(new Pair<>(i, j));


                } else if (ship.getComponent(i, j).getComponentType() == BlueStorage) {

                    other_storage.add(new Pair<>(i, j));
                }
            }
        }
int i = 0;

            if(cargo == Cargo.Red) {

                for (Pair<Integer, Integer> s : red_storage) {
                    CardComponent card = ship.getComponent(s.getKey(), s.getValue());


                    System.out.println("\n-> " + i + " RED STORAGE in ( " + (s.getKey()) + " : " + s.getValue() + " )"
                            + " contiene : ");
                    printCargo(((Storage) card).getCarried_cargos());
                    i++;
                }

                if(red_storage.isEmpty()){
                    System.out.println("\nNON CI SONO RED STORAGE");
                    return null;
                }

                int storage_scelto = readValidInt("Scelta ", 0, red_storage.size()-1, true);

                if(storage_scelto == -1 ) return null;
                tmp_storage = red_storage.get(storage_scelto); //coordinate storage scelto
                Storage s =(Storage) ship.getComponent(tmp_storage.getKey(), tmp_storage.getValue());
                int scelta = askCargo(s.getCarried_cargos());

                if (scelta != -1) {
                    final_value = new Pair<>(tmp_storage,scelta);
                }

            }else{
                for (Pair<Integer, Integer> s : other_storage) {

                    CardComponent card = ship.getComponent(s.getKey(), s.getValue());
                    String tmp = "";
                    switch (card.getComponentType()){

                        case BlueStorage -> tmp = "BLUE STORAGE";
                        case RedStorage -> tmp = "RED STORAGE";
                    }


                    System.out.println("\n-> " + i + " " + tmp + " in ( " + (s.getKey()) + " : " + s.getValue() + " )"
                            + " contiene :  ");
                    printCargo(((Storage) card).getCarried_cargos());

                    i++;
                }

                if(other_storage.isEmpty()){
                    System.out.println("\nNON CI SONO STORAGE");
                    return null;
                }
                int storage_scelto = readValidInt("Scelta ", 0, other_storage.size()-1, true);
                if(storage_scelto == -1 ) return null;
                tmp_storage = other_storage.get(storage_scelto); //coordinate storage scelto
                Storage s =(Storage) ship.getComponent(tmp_storage.getKey(), tmp_storage.getValue());
                int scelta = askCargo(s.getCarried_cargos());

                if (scelta != -1) {
                    final_value = new Pair<>(tmp_storage,scelta);
                }
            }


        return final_value;
    }

    @Override
    public Pair<Integer, Integer> useBattery(Ship ship) {
        List<Pair<Integer, Integer>> batteryPositions = new ArrayList<>();

        // Trova tutte le batterie con energia > 0
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component.getComponentType() == ComponentType.Battery && ((Battery) component).getStored() > 0) {
                    batteryPositions.add(new Pair<>(i, j));
                }
            }
        }

        // Se non ci sono batterie disponibili
        if (batteryPositions.isEmpty()) {
            System.out.println("Nessuna batteria disponibile.");
            return new Pair<>(-1, -1);
        }

        // Stampa l'elenco numerato delle batterie
        System.out.println("\nSeleziona una batteria (Riga ; Colonna) , (Dimensione ; Batterie Rimaste) :");
        for (int idx = 0; idx < batteryPositions.size(); idx++) {
            Pair<Integer, Integer> pos = batteryPositions.get(idx);
            Battery battery = (Battery) ship.getComponent( pos.getKey(), pos.getValue());

            System.out.println("\n\t"+(idx + 1) + ". Batteria in  ( " + pos.getKey() + " ; " + pos.getValue() + " )  , " + " ( " + battery.getSize() + " ; " + battery.getStored() + " )" );
        }

        // Chiedi all'utente di scegliere un'opzione
        int choice = readValidInt("\nInserisci il numero della batteria da utilizzare", 1, batteryPositions.size(), true);
        if (choice == -1)  return new Pair<>(-1, -1);

        // Restituisce la posizione della batteria selezionata
        return batteryPositions.get(choice - 1);
    }

    @Override
    public double declareCannonPower(Ship ship) {
       /* Map<CardComponent, Boolean> battery_usage = new HashMap<>();
        List<Pair<Integer, Integer>> doubleCannonPositions = new ArrayList<>();

        // Trova tutti i doppi cannoni
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component.getComponentType() == ComponentType.DoubleCannon) {
                    doubleCannonPositions.add(new Pair<>(i, j));
                }
            }
        }

        // Se non ci sono doppi cannoni disponibili
        if (doubleCannonPositions.isEmpty()) {
            return ship.calculateCannonPower(battery_usage);
        }

        int choice = 0;
        // Usa AND (&&) invece di OR (||) per terminare quando una delle condizioni è falsa
        while (choice != -1 && !doubleCannonPositions.isEmpty()) {
            // Stampa l'elenco aggiornato dei doppi cannoni
            System.out.println("\nSeleziona un doppio cannone (Riga ; Colonna) :");
            for (int idx = 0; idx < doubleCannonPositions.size(); idx++) {
                Pair<Integer, Integer> pos = doubleCannonPositions.get(idx);
                System.out.println("\n\t" + (idx + 1) + ". Doppio cannone in  ( " + pos.getKey() + " ; " + pos.getValue() + " ) ");
            }

            // Chiedi all'utente di scegliere un'opzione
            choice = readValidInt("\nInserisci il numero del doppio cannone da attivare", 1, doubleCannonPositions.size(), true);

            if (choice != -1) {
                Pair<Integer, Integer> b = useBattery(ship);
                Battery battery = (Battery) ship.getComponent(b.getKey(), b.getValue());
                battery.removeBattery();

                Pair<Integer, Integer> p = doubleCannonPositions.get(choice - 1);
                battery_usage.put(ship.getComponent(p.getKey(), p.getValue()), true);
                doubleCannonPositions.remove(choice - 1);
            }
        }

        return ship.calculateCannonPower(battery_usage);*/
        return 2.0;
    }

    @Override
    public double declareEnginePower(Ship ship) {
       /* Map<CardComponent, Boolean> battery_usage = new HashMap<>();
        List<Pair<Integer, Integer>> doubleEnginePositions = new ArrayList<>();

        // Trova tutti i doppi motori
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                CardComponent component = ship.getComponent(i, j);
                if (component.getComponentType() == ComponentType.DoubleEngine) {
                    doubleEnginePositions.add(new Pair<>(i, j));
                }
            }
        }

        // Se non ci sono doppi motori disponibili
        if (doubleEnginePositions.isEmpty()) {
            return ship.calculateEnginePower(battery_usage);
        }

        int choice = 0;
        // Usa AND (&&) invece di OR (||) per terminare quando una delle condizioni è falsa
        while (choice != -1 && !doubleEnginePositions.isEmpty()) {
            // Stampa l'elenco aggiornato dei doppi motori
            System.out.println("\nSeleziona un doppio motore (Riga ; Colonna) :");
            for (int idx = 0; idx < doubleEnginePositions.size(); idx++) {
                Pair<Integer, Integer> pos = doubleEnginePositions.get(idx);
                System.out.println("\n\t" + (idx + 1) + ". Doppio motore in  ( " + pos.getKey() + " ; " + pos.getValue() + " ) ");
            }

            // Chiedi all'utente di scegliere un'opzione
            choice = readValidInt("\nInserisci il numero del doppio motore da attivare", 1, doubleEnginePositions.size(), true);

            if (choice != -1) {
                Pair<Integer, Integer> b = useBattery(ship);
                Battery battery = (Battery) ship.getComponent(b.getKey(), b.getValue());
                battery.removeBattery();

                Pair<Integer, Integer> p = doubleEnginePositions.get(choice - 1);
                battery_usage.put(ship.getComponent(p.getKey(), p.getValue()), true);
                doubleEnginePositions.remove(choice - 1);
            }
        }

        return ship.calculateEnginePower(battery_usage);*/
        return 2.0;
    }


    @Override
    public Map<CardComponent, Boolean> batteryUsage(Ship ship) {
        Map<CardComponent, Boolean> battery_usage = new HashMap<>();
        int choose;
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                if (ship.getComponent(i, j).getComponentType() == Battery && ((Battery)ship.getComponent(i, j)).getStored()>0) {
                    System.out.println("Batteria alle coordinate x: " + i + " y: " + j+ "contiene "+((Battery)ship.getComponent(i, j)).getStored()+ "batterie/a");
                    if (((Battery)ship.getComponent(i, j)).getStored()==2) {
                        System.out.println("Premi: \n 0. per usare una batteria \n 1. per usarle entrambi \n 2. per passare alla prossima");
                         choose = readValidInt("Scelta: ", 0, 2, false);
                         if (choose == 0) {
                             battery_usage.put(ship.getComponent(i, j), true);
                         } else if (choose == 1) {
                             battery_usage.put(ship.getComponent(i, j), true);
                             battery_usage.put(ship.getComponent(i, j), true);
                         }

                    }
                    else {
                        System.out.println("Premi: \n 0. per usare la batteria \n 1. per passare alla prossima \n");
                        choose = readValidInt("Scelta: ", 0, 1, false);
                        if (choose == 0) {
                            battery_usage.put(ship.getComponent(i, j), true);
                        }
                    }
                }
            }
        }
        return battery_usage;
    }

    @Override
    public boolean useShield(Ship ship) {
        for (int i = 0; i < ship.getROWS(); i++) {
            for (int j = 0; j < ship.getCOLS(); j++) {
                if (ship.getComponent(i, j).getComponentType() == Shield){
                    System.out.println("Scudo alle coordinate x: " + i + " y: " + j);
                    System.out.println("Premi: \n 0. per usarlo \n 1. per passare al prossimo");
                    int choose = readValidInt("Scelta ", 0, 1, false);
                    if (choose == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void executeEpidemic(Ship ship) {

        Epidemic epidemic = new Epidemic(1,0,CardAdventureType.Epidemic,"");

        epidemic.execute(ship);

        StringBuilder outputContent = new StringBuilder();
        List<CardComponent> list_lu = new ArrayList<>();

        outputContent.append("sono stati RIMOSSI 1 ASTRONAUTA per ogni Living Unit ADIACENTE");
        CardComponent[][] plance = ship.getShipBoard();



        for(int i = 0 ; i < ship.getROWS() ; i++) {
            for(int j = 0 ; j < ship.getCOLS() ; j++) {
                CardComponent card = plance[i][j];

                if(card.getComponentType().equals(MainUnitGreen) ||
                        card.getComponentType().equals(MainUnitRed) ||
                        card.getComponentType().equals(MainUnitBlue) ||
                        card.getComponentType().equals(MainUnitYellow) ||
                        card.getComponentType().equals(LivingUnit)) {
                    LivingUnit lu = (LivingUnit) card;
                    if(lu.getNum_crewmates() > 0) {
                        list_lu.add(card);
                    }
                }
            }
        }

        outputContent.append("\nLISTA AGGIORNATA :\n");
        int i = 0;
        for( CardComponent card : list_lu ) {
            Pair<Integer,Integer> coord = ship.getCoords(card);
            LivingUnit lu = (LivingUnit) card;
            outputContent.append(String.format(" %d -  RIGA %d COLONNA %d  n. astronauti : %d%n", i, coord.getKey(), coord.getValue(),lu.getNum_crewmates()));
            i++;
        }

        printBorder(outputContent.toString());
    }




    @Override
public void showBasicBoard(Map<Integer, Player> positions, Map<Integer, Player> laps) {
    final int BOARD_SIZE = 24;
    StringBuilder boardContent = new StringBuilder();

    for (Map.Entry<Integer, Player> entry : positions.entrySet()) {
        int pos = entry.getKey() % BOARD_SIZE;
        Player p = entry.getValue();

        String name = p.getNickname();
        if (name.length() > 6) {
            name = name.substring(0, 6);
        }

        int lap = 0;
        for (Map.Entry<Integer, Player> lapEntry : laps.entrySet()) {
            if (lapEntry.getValue().equals(p)) {
                lap = lapEntry.getValue().getNum_laps();
                break;
            }
        }
        boardContent.append(String.format("Pos %02d: %s (Lap %d)%n", pos, name, lap));
    }

    printBorder(boardContent.toString());
}


    @Override
    public void showBoard(Map<Integer, Player> positions, Map<Integer, Player> laps){

        final int BOARD_SIZE = 24;
        final int ROW_WIDTH = 6;
        final int CELL_WIDTH = 10; // Larghezza di ogni cella

        String[][][] board = new String[BOARD_SIZE][4][1];

        // Inizializza ogni blocco
        for (int i = 0; i < BOARD_SIZE; i++) {
            board[i][0][0] = "+--------+";
            board[i][1][0] = String.format("| Pos %02d |", i);
            board[i][2][0] = "|        |";
            board[i][3][0] = "+--------+";
        }

        // Inserisci i dati dei giocatori
        for (Map.Entry<Integer, Player> entry : positions.entrySet()) {
            int pos = entry.getKey();
            if (pos == 24) {
                pos = 0;
            }
            Player p = entry.getValue();
            String nome = p.getNickname().length() > 6 ?
                    p.getNickname().substring(0, 6) :
                    String.format("%-6s", p.getNickname());
            board[pos][2][0] = "| " + nome + " |";

            // Trova il numero di giri
            int lap = 0;
            for (Map.Entry<Integer, Player> lapEntry : laps.entrySet()) {
                if (lapEntry.getValue().equals(p)) {
                    lap = lapEntry.getValue().getNum_laps();
                    break;
                }
            }
            board[pos][3][0] = String.format("| Lap %2d |", lap);
        }

        // --- TOP ROW (00-05) ---
        printRow(0, ROW_WIDTH, board);

        // --- SIDES (LEFT 23-18, RIGHT 06-11) ---
        int leftStart = 23;
        int rightStart = 6;

        for (int i = 0; i < ROW_WIDTH; i++) {
            // Calcola spaziatura per allineare la colonna destra
            int spacing = (ROW_WIDTH * (CELL_WIDTH + 1)) - CELL_WIDTH - 2;

            for (int row = 0; row < 4; row++) {
                System.out.print(board[leftStart - i][row][0]);
                System.out.print(" ".repeat(spacing));
                System.out.println(board[rightStart + i][row][0]);
            }
        }

        // --- BOTTOM ROW (17-12) ---
        printRow(17, -1, board); // Stampa in ordine inverso
    }

    private void printRow(int start, int direction, String[][][] board) {

        for (int row = 0; row < 4; row++) {
            System.out.print("\t");
            for (int i = 0; i < 6; i++) {
                int pos = direction > 0 ? start + i : start - i;
                System.out.print(board[pos][row][0] + " ");
            }
            System.out.println();
        }
    }
    // Centra una stringa su una larghezza fissa
    private static String center(String str, int width) {
        if (str.length() >= width) {
            return str; // se è troppo lunga, non la tocco
        }
        int padding = width - str.length();
        int padLeft = padding / 2;
        int padRight = padding - padLeft;
        return " ".repeat(padLeft) + str + " ".repeat(padRight);
    }





}
