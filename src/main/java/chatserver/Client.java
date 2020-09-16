package chatserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread implements Closeable {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler handler;

    public Client(Server server, Socket socket, String name) throws IOException {
        this.server = server;
        this.socket = socket;
        this.handler = new ClientHandler(
                new Scanner(socket.getInputStream()),
                new PrintWriter(socket.getOutputStream()));
        this.name = name;
    }

    @Override
    public void run() {
        try {
            name = handler.fetchName();
            while (true) {
                String msg = handler.prompt();
                server.broadcast(this, msg);
            }
        } finally {
            try { close(); } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "socket=" + socket +
                '}';
    }

    @Override
    public void close() throws IOException {
        server.removeClient(this);
        socket.close();
    }

    public void sendMessage(String s) {
    }

    public class ClientHandler {
        private final Scanner in;
        private final PrintWriter out;

        public ClientHandler(Scanner in, PrintWriter out) {
            this.in = in;
            this.out = out;
        };

        private String prompt() {
            out.print("> ");
            out.flush();
            return in.nextLine();
        }

        private String fetchName() {
            out.println("What's your name, man?");
            return prompt();
        }


    }
}
