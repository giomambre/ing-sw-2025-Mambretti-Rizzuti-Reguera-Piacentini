package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class SocketAdapter implements NetworkAdapter {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String host;
    private final int port;

    public SocketAdapter(String host, int port) {
        this.host = host;
        this.port = port;
    }
    @Override
    public void connect(String host, int port) throws IOException {
        // Ignora host e port passati come argomenti se già impostati nel costruttore,
        // altrimenti usali. Per semplicità usiamo quelli del costruttore.
        this.socket = new Socket(this.host, this.port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        System.out.println("✅ SOCKET connesso a " + this.host + ":" + this.port);
    }

    @Override
    public void sendMessage(Message msg) throws IOException {
        if (out != null) {
            out.writeObject(msg);
            out.flush();
        } else {
            throw new IOException("Stream di output non inizializzato. Chiamare connect() prima.");
        }
    }

    @Override
    public Message readMessage() throws IOException, ClassNotFoundException {
        if (in != null) {
            return (Message) in.readObject();
        } else {
            throw new IOException("Stream di input non inizializzato. Chiamare connect() prima.");
        }
    }



}
