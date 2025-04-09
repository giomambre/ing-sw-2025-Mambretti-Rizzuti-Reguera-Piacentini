package it.polimi.ingsw.model.view;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Lobby;
import it.polimi.ingsw.model.components.CardComponent;
import it.polimi.ingsw.model.enumerates.Color;
import it.polimi.ingsw.model.enumerates.ConnectorType;
import it.polimi.ingsw.model.enumerates.Direction;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class TUI implements View {
    private final PrintStream out;
    Scanner input = new Scanner(System.in);
    // Codici ANSI
    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";
    final String YELLOW = "\u001B[33m";
    final String BLUE = "\u001B[34m";
    final String PURPLE = "\u001B[35m";
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

        out.print("\r\033[2K"); // pulisci la riga
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
    public void showShip(String nickname){
        GameController game= new GameController(new Lobby("giustoperfarelaprova", 3));
        CardComponent[][] ship_board = game.getShipPlance(nickname);

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 7; col++) {




            }
        }

    }

    private String printConnector(ConnectorType connector, Direction direction) {
        switch (connector) {
            case Smooth: {
                return "";
            }

            case Single: {
                switch (direction) {
                    case South,North: return "|";
                    case East, West: return "-";
                }
            }

            case Double: {
                switch (direction) {
                    case South, North: return "||";
                    case East, West: return "=";
                }
            }

            case Universal:{
                switch (direction) {
                    case South, North: return "|||";
                    case East, West: return "≡";
                }
            }

        }
    }
}
