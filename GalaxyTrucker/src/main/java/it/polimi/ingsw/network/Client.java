package it.polimi.ingsw.network;

import it.polimi.ingsw.model.view.TUI;
import it.polimi.ingsw.model.view.View;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.io.*;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                Message serverMessage = (Message) in.readObject();
                if (serverMessage.getType() == MessageType.REQUEST_NAME) {
                    System.out.println(serverMessage.getContent());
                    String name = scanner.nextLine();

                    out.writeObject(new Message(MessageType.REQUEST_NAME, name));
                    out.flush();
                } else if (serverMessage.getType() == MessageType.NAME_ACCEPTED) {
                    System.out.println("✅ " + serverMessage.getContent());
                    break;
                } else if (serverMessage.getType() == MessageType.NAME_REJECTED) {
                    System.out.println("❌ " + serverMessage.getContent());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
