package chatserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread implements Closeable {
    private final Server server;
    private final Socket socket;
    private String name;
    private final ClientHandler handler;
    private final BlockingQueue<String> messageQueue;

    public Client(Server server, Socket socket, String name) throws IOException {
        this.server = server;
        this.socket = socket;
        this.handler = new ClientHandler(
                socket.getInputStream(),
                new PrintWriter(socket.getOutputStream(), true));
        this.name = name;
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    public String getClientName() {
        return name;
    }


    @Override
    public void run() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    handler.showPrompt();
                    String line = handler.waitForLine();
                    if (line.startsWith("!rename")) {
                        String previousName = name;
                        name = handler.fetchName();
                        server.announceName(this, previousName);

                    } else if (line.startsWith("!play")) {
                        Client superThis = this;
                        Game game = server.getActiveGame();
                        game.play(new Game.GameParticipant() {
                            @Override
                            public void notifyGameStart(String secretWord) {
                                handler.printMessage("Game started, write: " + secretWord);
                            }

                            @Override
                            public void notifyWinner(Client winner) {
                                if (winner.equals(superThis)) {
                                    handler.printMessage("Yes you won");
                                } else {
                                    handler.printMessage("Dammm, you lost to " + winner.getClientName());
                                }
                            }

                            @Override
                            public String getAnswer() {
                                return handler.waitForLine();
                            }

                            @Override
                            public Client getClient() {
                                return superThis;
                            }
                        });

                    } else if (line.startsWith("!exit")) {
                        socket.close();
                        break;
                    } else{
                        server.broadcast(this, line);
                    }
                }
            } catch (InterruptedException | IOException e) { }
        });
        try {
            String previousName = name;
            name = handler.fetchName();
            server.announceName(this, previousName);
            t.start();

            while (true) {
                String inbound = messageQueue.take();
                handler.printMessage(inbound);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " exited with: " + e.getMessage());
        } finally {
            try { close(); } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException e) {
                t.stop();
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

        private String prompt() {
            showPrompt();
            return waitForLine();
        }

        private String fetchName() {
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

        public void printMessage(String message) {
            out.println(message);
            out.flush();
        }
    }
}
