package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.adventures.*;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.components.LivingUnit;
import it.polimi.ingsw.model.enumerates.*;
import it.polimi.ingsw.view.View;
import javafx.util.Pair;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
        out.println("\n🎮 SCEGLI UN'AZIONE:");
        out.println("[1] Mostra stato nave");
        out.println("[2] Mostra tutti i giocatori");
        out.println("[3] Visualizza le Carte Avventura");
        out.println("[4] Visualizza le Carte Prenotate");
        out.println("[5] Esci dal menu");

        int scelta;
        do {
            out.print("Inserisci il numero dell'azione: ");
            while (!input.hasNextInt()) {
                input.next(); // scarta input errato
                out.print("⚠️ Inserisci un numero valido: ");
            }
            scelta = input.nextInt();
        } while (scelta < 1 || scelta > 5);

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

            case 5 -> out.println("🔙 Uscita dal menu.");
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
        int resp = readInt();
        if (resp != 1 && resp != 2) {
            this.askCreateOrJoin();
        }

        return resp;

    }

    @Override
    public int askNumPlayers() {
        lastRequest = "Inserisci il numero di player della lobby (2-4) , -1 per uscire: ";
        out.println(lastRequest);

        int resp = readInt();
        if (resp == -1) return -1;
        if (resp < 2 || resp > 4) {
            return this.askNumPlayers();
        }
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
            System.out.println("Risposta non valilda,riprova : ");
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
    public void showShip(String nickname) {
        GameController game = new GameController(new Lobby("giustoperfarelaprova", 3));
        CardComponent[][] ship_board = game.getShipPlance(nickname);
        printShip(ship_board);
    }


    @Override
    public void printShip(CardComponent[][] ship) {
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
        int choice = 1;
        do{
            if(choice < 0 || choice >= pieces.size())
                System.out.println("\nHai inserito un valore non esistente!");
            System.out.println("\nInserisci il numero del troncone che vuoi tenere");
           choice = input.nextInt();

        }while(choice < 0 || choice >= pieces.size());

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
        for (Cargo cargo : cargos) {
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
        int selected = -1;
        do {
            out.println("Premi :\n 1 per prendere una carta casuale\n 2 : per scegliere dal mazzo delle carte scoperte\n 3 : per usare una carta prenotata \n 4 : terminare l'assemblaggio\n");
            selected = readInt();
        } while (selected != 1 && selected != 2 && selected != 3 && selected != 4);
        return selected;


    }

    @Override
    public int crewmateAction(Pair<Integer,Integer> coords) {
        int selected = -1;
        System.out.println();
        CardComponent[][] ship = player_local.getShip().getShipBoard();
        CardComponent component = ship[coords.getKey()][coords.getValue()];
        List<CrewmateType> crewmateType;
        crewmateType = player_local.getShip().checkAlienSupport(component);
        System.out.println("STAI RIMEPIENDO LA LIVING UNIT IN RIGA : " + player_local.getShip().getCoords(component).getKey() +  " COLONNA : " + player_local.getShip().getCoords(component).getValue());

        if (crewmateType.contains(CrewmateType.BrownAlien) && crewmateType.contains(CrewmateType.PinkAlien) ) {
            lastRequest = ("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno rosa\n 3 : per aggiungere un alieno marrone\n ");
            do {
                out.println("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno rosa\n 3 : per aggiungere un alieno marrone\n");
                selected = readInt();
            } while (selected != 1 && selected != 2 && selected != 3 && selected != 4);
        }

        else if(crewmateType.contains(CrewmateType.BrownAlien)){
            lastRequest = ("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno marrone\n ");
            do {
                out.println("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno marrone\n");
                selected = readInt();
            } while (selected != 1 && selected != 2 );
            if(selected == 2) selected++;
        }
        else if(crewmateType.contains(CrewmateType.PinkAlien)){
            lastRequest = ("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno rosa\n ");
            do {
                out.println("Premi :\n 1 per aggiungere un astronauta\n 2 : per aggiungere un alieno rosa\n");
                selected = readInt();
            } while (selected != 1 && selected != 2 );
        }
else if(crewmateType.isEmpty()){ System.out.println("Nessuna alien unit, 2 Astronauti aggiunti in automatico ");
selected = 1;}


        return selected;
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
        lastRequest = "Premi :\n 1 per ruotare la carta in senso orario\n 2 : per inserirla \n 3 : per scartarla \n 4 : per prenotarla ";
        out.println("Premi :\n 1 per ruotare la carta in senso orario\n 2 : per inserirla \n 3 : per scartarla \n 4 : per prenotarla ");

        while (true) {
            System.out.print("Scelta: ");
            int selected = readInt();

            if (selected == 1) {
                out.println("\nCarta ruotata");
                card.rotate();
                printCard(card);

            } else if (selected == 2 || selected == 3 || selected == 4) {

                return selected;
            } else {
                System.out.println("Indice non valido. Riprova.");
            }
        }
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
