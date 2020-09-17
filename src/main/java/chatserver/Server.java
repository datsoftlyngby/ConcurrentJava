package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Server extends Thread {
    private final ServerSocket socket;
    private final List<Client> clients;

    public Server(ServerSocket socket, List<Client> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public Server(ServerSocket socket) {
        this(socket, new ArrayList<>());
    }

    @Override
    public void run() {
        System.out.println("Listing for clients at: " + socket);
        try {
            while (true) {
                Client client = new Client(
                        this,
                        socket.accept(),
                        "anonymous");
                addClient(client);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(2424));
        server.start();

        System.out.println("Server Started!");
    }

    public synchronized void addClient(Client client) {
        System.out.println("Accepted client: " + client);
        clients.add(client);
    }

    public synchronized void removeClient(Client client) {
        System.out.println("Closed client: " + client);
        clients.remove(client);
    }

    public synchronized void broadcast(Client from, String msg) {
        for (Client c : clients) {
            if (c.equals(from)) continue;
            c.sendMessage(from.getClientName() + ": " +  msg);
        }
    }

    public void announceName(Client from, String previous) {
        System.out.println(from.getClientName() + " joined the chat!");
        for (Client c : clients) {
            if (c.equals(from)) continue;
            c.sendMessage(from.getClientName() + " joined the chat!");
        }
    }

    private volatile Game game;
    public synchronized Game getActiveGame () {
        if (game == null || game.done()) {
            game = new Game(3);
        }
        return game;
    }

}
