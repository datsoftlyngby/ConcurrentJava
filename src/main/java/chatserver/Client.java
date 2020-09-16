package chatserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.*;

public class Client extends Thread implements Closeable {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler handler;
    private final ConcurrentLinkedQueue<String> messageQueue;

    public Client(Server server, Socket socket, String name) throws IOException {
        this.server = server;
        this.socket = socket;
        this.handler = new ClientHandler(
                socket.getInputStream(),
                new PrintWriter(socket.getOutputStream()));
        this.name = name;
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    public String getClientName() {
        return name;
    }


    @Override
    public void run() {
        try {
            name = handler.fetchName();
            while (true) {
                handler.showPrompt();
                server.broadcast(this, handler.waitForLine());
                String inbound;
                while ((inbound = messageQueue.poll()) != null) {
                    handler.out.println(inbound);
                    handler.out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        messageQueue.add(s);
    }

    public class ClientHandler {
        private final InputStream in;
        private final PrintWriter out;

        public ClientHandler(InputStream in, PrintWriter out) {
            this.in = in;
            this.out = out;
        };

        private void showPrompt() {
            out.print("> ");
            out.flush();
        }

        private String prompt() throws IOException {
            showPrompt();
            return waitForLine();
        }

        private String fetchName() throws IOException {
            out.println("What's your name, man?");
            return prompt();
        }

        private String waitForLine() {
            return new Scanner(in).nextLine();
        }

        public boolean hasInput() throws IOException {
            in.readAllBytes();
            return in.available() > 0;
        }
    }
}
