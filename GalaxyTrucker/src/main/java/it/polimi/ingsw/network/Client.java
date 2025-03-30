package it.polimi.ingsw.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import it.polimi.ingsw.network.Message;

public class Client {
    private final String ip;
    private final int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] args) {
        final Client client = new Client("127.0.0.1", 2345);
        try {
            client.startClient();
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void startClient() throws IOException {
        final Socket socket = new Socket(ip, port);
        System.out.println("Connection established");
        final ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
        final ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
        final Scanner stdin = new Scanner(System.in);
        try {
            while (true) {
                final String inputLine = stdin.nextLine();
                final Message message = new Message(inputLine, inputLine);
                socketOut.writeObject(message);
                socketOut.flush();
                final Message reply = (Message) socketIn.readObject();
                System.out.println(reply);
            }
        } catch (final NoSuchElementException | ClassNotFoundException e) {
            System.out.println("Connection closed");
        } finally {
            stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }
}