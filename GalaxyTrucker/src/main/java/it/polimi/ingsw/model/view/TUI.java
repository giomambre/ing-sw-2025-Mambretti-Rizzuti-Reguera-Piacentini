package it.polimi.ingsw.model.view;

import java.io.PrintStream;
import java.util.Scanner;

public class TUI implements View {
    private final PrintStream out;
    Scanner input = new Scanner(System.in);

    public TUI() {
        this.out = System.out;

    }

    @Override
    public String chooseConnection() {
        out.println("\u001b[34mWelcome to MyShelfie!\u001b");
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
        out.println("ecco il : ");
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

        if(resp<2 || resp>4){
           return this.askNumPlayers();
        }
        return resp;
    }
}
