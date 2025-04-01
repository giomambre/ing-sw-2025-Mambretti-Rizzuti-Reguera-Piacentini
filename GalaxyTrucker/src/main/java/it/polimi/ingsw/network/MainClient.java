package it.polimi.ingsw.network;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class MainClient implements VirtualView{

    final VirtualServerRmi server;

    public MainClient(VirtualServerRmi server) throws RemoteException {
        super();
        this.server = server;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        Scanner scan = new Scanner(System.in);

        final String serverName = "AdderServer";

        Registry registry = LocateRegistry.getRegistry(args[0], 1234);

        VirtualServerRmi server = (VirtualServerRmi) registry.lookup(serverName);

        new MainClient(server).run();

        while(true) {
            System.out.println("Inserisci messaggio : ");
            String msg = scan.nextLine();
            server.sendMessage(msg);
        }

    }

    private void run() throws RemoteException {
        this.server.connect(this);

    }

    @Override
    public void showMessage(String msg) throws RemoteException {
        System.out.println(msg);

    }
}
