package it.polimi.ingsw.model.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Ship;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ComponentType;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;
import javafx.util.Pair;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.model.enumerates.ComponentType.Empty;
import static it.polimi.ingsw.model.enumerates.ComponentType.NotAccessible;
import static it.polimi.ingsw.model.enumerates.Direction.*;

public class TUI implements View {
    private final PrintStream out;
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


    public TUI() {
        this.out = System.out;
        out.println(PURPLE + banner + RESET);

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
        return input.nextLine();

    }

    @Override
    public String getInput() {
        return input.nextLine();
    }


    @Override
    public int askCreateOrJoin() {
        out.println("PREMERE: \n 1 PER CREARE UNA LOBBY \n 2 PER ENTRARE IN UNA LOBBY ");
        int resp = input.nextInt();
        if (resp != 1 && resp != 2) {
            this.askCreateOrJoin();
        }
        return resp;

    }

    @Override
    public int askNumPlayers() {
        out.println("INSERISCI IL NUMERO DI PLAYER DELLA LOBBY (2-4): ");

        int resp = input.nextInt();

        if (resp < 2 || resp > 4) {
            return this.askNumPlayers();
        }
        return resp;
    }

    @Override
    public void showGenericError(String error) {
        System.out.println();
        out.println(RED + error + RESET);
    }

    @Override
    public int showLobbies(List<Integer> lobbies) {
        System.out.println("Inserisci il numero della lobby , -1 altrimenti:");
        for (Integer lobby : lobbies) {

            System.out.println("Lobby n : " + lobby);

        }
        int resp = input.nextInt();

        while (resp != -1 && !lobbies.contains(resp)) {
            System.out.println("Risposta non valilda,riprova : ");
            resp = input.nextInt();

        }

        return resp;

    }


    @Override
    public Color askColor(List<Color> colors) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Scegli il colore che preferisci tra i seguenti disponibili:");
            for (Color c : colors) {
                System.out.print("- " + c.name().toLowerCase() + "  ");
            }
            System.out.println();

            String input;
            do {
                input = scanner.nextLine().trim();
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

        for (int r = 0; r < rows; r++) {
            StringBuilder top = new StringBuilder();
            StringBuilder mid = new StringBuilder();
            StringBuilder bot = new StringBuilder();

            for (int c = 0; c < cols; c++) {
                CardComponent card = ship[r][c];

                if (card.getComponentType() == Empty || card.getComponentType() == NotAccessible) {
                    top.append(" ".repeat(CELL_WIDTH));
                    mid.append(" ".repeat(CELL_WIDTH));
                    bot.append(" ".repeat(CELL_WIDTH));
                } else {
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

            System.out.println(top);
            System.out.println(mid);
            System.out.println(bot);
            System.out.println("-".repeat(cols * CELL_WIDTH));
        }
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

            default:
                return "    ";
        }
    }

    @Override
    public int selectDeck() {

        int selected = -1;
        do {
            out.println("Premi : 1 per prendere una carta casuale\n 2 : per scegliere dal mazzo delle carte scoperte\n");
            selected = input.nextInt();
        } while (selected != 1 && selected != 2);
        return selected;


    }


    @Override
    public int askFacedUpCard(List<CardComponent> cards) {
        System.out.println("Premi l'indice della carta che vuoi prendere (-1 per uscire)");

        for (int i = 0; i < cards.size(); i++) {
            System.out.println(" " + i + " : " + cards.get(i));
        }

        while (true) {
            System.out.print("Scelta: ");
            int selected = input.nextInt();

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
        out.println(card.toString());

        out.println("Premi : 1 per ruotare la carta in senso orario\n 2 : per inserirla 3 : per scartarla ");

        while (true) {
            System.out.print("Scelta: ");
            int selected = input.nextInt();

            if (selected == 1) {
                out.println("Carta ruotata");
                card.rotate();
                out.println(card.toString());

            } else if (selected == 2 || selected == 3) {
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

            out.println("Inserire la coordinata X (tra 0 e " + (ship.getROWS() - 1) + " oppure -1 per uscire): ");
            x = input.nextInt();
            if (x==-1)         return new Pair<>(x, y);


            out.println("Inserire la coordinata Y (tra 0 e " + (ship.getCOLS() - 1) + " oppure -1 per uscire): ");
            y = input.nextInt();
            if (y==-1)         return new Pair<>(x, y);

            if (x < 0 || x >= ship.getROWS() || y < 0 || y >= ship.getCOLS()) {
                out.println("Errore: le coordinate sono fuori dai limiti. Riprova.");
            } else {
                // Verifica se la posizione nella nave contiene un componente
                CardComponent component = ship.getComponent(x, y);
                if (component.getComponentType() == Empty) {
                    out.println("Coordinate valide ! " );  // Mostra il componente trovato
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
    public void showPlayer(Player player) {


            System.out.println("\n====== 👤 INFO GIOCATORE ======");
            System.out.println("🆔 Nickname: " + player.getNickname());
            System.out.println("🎨 Colore: " + player.getColor());
            System.out.println("🚀 Lap completati: " + player.getNum_laps());
            System.out.println("💰 Crediti: " + player.getCredits());
            System.out.println("🧩 Connettori esposti: " + player.getExposed_connectors());

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
            System.out.println(player.getShip()); // Se hai un toString dettagliato nella Ship, qui funziona

            System.out.println("===============================\n");




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
