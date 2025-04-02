package it.polimi.ingsw.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;

public class Client {
    private Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    /** It starts the socket connection on the given ip and port.
     * @param ip address of the connection.
     * @param port chosen port for the connection. */
    public void connection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        //Directed communication between client and server
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /** It listens to the messages received by via socket, and it calls the elaborate method. */
    public static void listenSocket() throws IOException, ParseException, InterruptedException {
        while(true) {
            String message = in.readLine();
            System.out.println(message);
        }
    }
}
