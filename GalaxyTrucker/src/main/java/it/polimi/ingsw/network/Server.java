package it.polimi.ingsw.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;

    public static void main(String[] args) {
        final Server server = new Server(2345);
        server.startServer();
    }

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (final IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                final Socket socket = serverSocket.accept();
                executor.submit(() -> {
                    try {
                        final Scanner in = new Scanner(socket.getInputStream());
                        final PrintWriter out = new PrintWriter(socket.getOutputStream());
                        while (true) {
                            final String line = in.nextLine();
                            if (line.equals("quit")) {
                                break;
                            } else {
                                out.println("Received: " + line);
                                out.flush();
                            }
                        }
                        in.close();
                        out.close();
                        socket.close();
                    } catch (final IOException e) {
                        System.err.println(e.getMessage());
                    }
                });
            } catch (final IOException e) {
                break;
            }
        }
        executor.shutdown();
    }
}

class EchoServerClientHandler implements Runnable {
    private final Socket socket;

    public EchoServerClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            final Scanner in = new Scanner(socket.getInputStream());
            final PrintWriter out = new PrintWriter(socket.getOutputStream());
            while (true) {
                final String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                } else {
                    out.println("Received: " + line);
                    out.flush();
                }
            }
            in.close();
            out.close();
            socket.close();
        } catch (final IOException e) {
            System.err.println(e.getMessage());
        }
    }
}