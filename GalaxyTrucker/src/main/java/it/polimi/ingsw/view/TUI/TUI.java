package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.components.Storage;
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
    private boolean isMenuOpen = false; // Variabile per tenere traccia dello stato del menu
    private String lastRequest = ""; // Variabile per memorizzare l'ultima richiesta

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
     * @return Il valore letto oppure -1 se allowExit è true e si vuole uscire
     */
    private int readValidInt(String prompt, int minVal, int maxVal, boolean allowExit) {
        int value;
        String fullPrompt = prompt + (allowExit ? " (-1 per uscire)" : "") + ": ";
        do {
            System.out.print(fullPrompt);
            String input = readLine().trim();

            if (allowExit && input.equalsIgnoreCase("-1")) {
                return -1;
            }

            try {
                value = Integer.parseInt(input);
                if (value < minVal || value > maxVal) {
                    System.out.println(RED + "Errore: il valore deve essere compreso tra " + minVal + " e " + maxVal + "." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Errore: inserisci un numero valido." + RESET);
                value = Integer.MIN_VALUE; // forza il ciclo a ripetere
            }
        } while (value < minVal || value > maxVal);

        return value;
    }

    public void setPlayer_local(Player player) {

        this.player_local = player;
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
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String input = scanner.nextLine();
                    if (isMenuOpen && !input.equalsIgnoreCase("/menu")) {

                        continue;
                    }
                    switch (input.toLowerCase()) {
                        case "/menu" -> showMenu();
                        default -> inputQueue.put(input);
                    }
                } catch (Exception e) {
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
        out.println("[3] Visualizza le Carte Avventura");
        out.println("[4] Visualizza le Carte Prenotate");

      int scelta = readValidInt("Scelta ",1,4,true);

        switch (scelta) {
            case 1 -> showPlayer(player_local);

            case 2 -> {
                out.println("\n👥 Giocatori avversari:");
                for (Player p : other_players_local) {
                    showPlayer(p);
                }
            }

            case 3 -> showAdventureDeck(local_adventure_deck);

            case 4 -> showExtraCard();

            case -1 -> out.println("🔙 Uscita dal menu.");
        }

        isMenuOpen = false;
        out.println();
        out.print(lastRequest);

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

            for (CardAdventure c : local_adventure_deck.get(d)) {
                out.println();
                printCardAdventure(c);
                out.println();
            }
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
    public void showMessage(String message) {


        out.println(message);
    }

    @Override
    public String askNickname() {
        out.println("Enter your nickname: ");
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
        lastRequest = "Inserisci il numero di player della lobby (2-4) , -1 per uscire: ";
        out.println(lastRequest);

        int resp = readValidInt("Scelta ",2,4,false);
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
            for (Color c : colors) {
                System.out.print("- " + c.name().toLowerCase() + "  ");
            }
            System.out.println();

            String input;
            do {
                input = readLine().trim();
            } while (input.isEmpty());

            try {
                Color selected = Color.valueOf(input.toUpperCase());
                if (colors.contains(selected)) {
                    return selected;
                } else {
                    System.out.println("Il colore scelto non è tra quelli disponibili. Riprova.");
                }
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
        }
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
        int choice = readValidInt("Scelta Troncone : ",0,pieces.size()-1,false);


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

    public void printCardAdventure(CardAdventure card) {

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
    }

    private void printCargo(List<Cargo> cargos) {
        System.out.println("Il carico contiene le seguenti merci:");
        int i=0;
        for (Cargo cargo : cargos) {
            System.out.println(i+": ");
            switch (cargo) {
                case Blue:
                    System.out.println("cargo blu");
                    break;
                case Yellow:
                    System.out.println("cargo giallo");
                    break;
                case Green:
                    System.out.println("cargo verde");
                    break;
                case Red:
                    System.out.println("cargo rosso");
                    break;
            }
        }
    }

    private void printMeteors(List<Pair<MeteorType, Direction>> meteors) {
        for (Pair<MeteorType, Direction> meteor : meteors) {
            switch (meteor.getKey()) {
                case LargeMeteor -> System.out.println("Meteora grossa");
                case SmallMeteor -> System.out.println("Meteora piccola");
                case HeavyCannonFire -> System.out.println("Cannonata pesante");
                case LightCannonFire -> System.out.println("Cannonata leggera");
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

            out.println("Premi :\n 1 per prendere una carta casuale\n 2 : per scegliere dal mazzo delle carte scoperte\n 3 : per usare una carta prenotata \n 4 : terminare l'assemblaggio\n");
        return readValidInt("Scelta" , 1 , 4, false);


    }

    @Override

    public int crewmateAction(Pair<Integer, Integer> coords) {
        CardComponent[][] ship = player_local.getShip().getShipBoard();
        CardComponent component = ship[coords.getKey()][coords.getValue()];
        List<CrewmateType> crewmateType = player_local.getShip().checkAlienSupport(component);

        System.out.println("STAI RIMEPIENDO LA LIVING UNIT IN RIGA: " +
                coords.getKey() + " COLONNA: " + coords.getValue());

        String prompt;
        int choice;

        if (crewmateType.contains(CrewmateType.BrownAlien) && crewmateType.contains(CrewmateType.PinkAlien)) {
            prompt = """
                Premi:
                 1: per aggiungere un astronauta
                 2: per aggiungere un alieno rosa
                 3: per aggiungere un alieno marrone""";
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
        System.out.println();

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
    public Map<CardComponent,Integer> chooseAstronautLosses(Ship ship, int astronautLoss){
        Map<CardComponent,Integer> astronaut_losses = new HashMap<>();
        int totalAstronautsLosses = 0;

        int choice=0;
        int num;
        while(totalAstronautsLosses == astronautLoss){
            System.out.println("Devi rimuovere ancora "+astronaut_losses+"membri dell'equipaggiamento");
            for(int i = 0 ; i<ship.getROWS(); i++){
                for(int j = 0 ; j<ship.getCOLS(); j++) {
                    if (ship.getComponent(i,j).getComponentType() == ComponentType.LivingUnit && ((LivingUnit)ship.getComponent(i,j)).getNum_crewmates() > 0) {
                        System.out.println("LIVING UNIT alle coordinate: x: "+i+" y: "+j+"con "+((LivingUnit)ship.getComponent(i,j)).getNum_crewmates()+ " membri" );
                        System.out.println("1. Per rimuovere membri da questa living unit \n 2. Per passare alla prossima living unit\n");
                        while(choice != 1 && choice != 2){
                            choice = readInt();
                            if (choice != 1 && choice != 2) {
                                System.out.println("Opzione non disponibile, reinserire:");
                            } else if (choice == 1) {
                                System.out.println("Inserisci il numero di membri dell'equipaggio che vuoi rimuovere: ");
                                num = readInt();
                                while ((num!=1 && num!=2) || num>astronautLoss ){

                                    if((num==1 || num==2) && num>astronautLoss ){
                                        System.out.println("Stai cercando di rimuovere più membri di quanti dovresti, reinserire: ");
                                    }
                                    else{
                                        System.out.println("Valore non disponibile, reinserire: ");
                                    }
                                    num = readInt();
                                }
                                astronaut_losses.put(ship.getComponent(i,j), num);
                                totalAstronautsLosses+=num;
                                astronautLoss-=num;
                            }
                            else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return astronaut_losses;
    }

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
    public void showBoard(Map<Integer, Player> positions, Map<Integer, Player> laps) {
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
