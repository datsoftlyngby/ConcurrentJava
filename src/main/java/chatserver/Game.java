package chatserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final String secretWord;
    private volatile int waiting;
    private volatile Client winner;
    private static final List<String> words =
            List.of("verysecret",
                    "ordblind",
                    "Stewerdesse",
                    "banan",
                    "ekstraordinær",
                    "kagemand",
                    "kaospilot",
                    "ferskensmag",
                    "StrengeSpil",
                    "speciallægepraksisplanlægningsstabiliseringsperiode"
                    );

    public Game(int capacity) {
        this.waiting = capacity;
        this.winner = null;
        this.secretWord = chooseSecretWord();
    }

    private String chooseSecretWord() {
        List<String> shuffledWords = new ArrayList<>(words);
        Collections.shuffle(shuffledWords);
        return shuffledWords.get(0);
    }

    public void play(GameParticipant participant) throws InterruptedException {
        await();
        participant.notifyGameStart(secretWord);
        while (true) {
            if (done() || participant.getAnswer().equals(secretWord)) {
                break;
            }
        }
        Client winner = getAndSetWinner(participant.getClient());
        participant.notifyWinner(winner);
    }

    public synchronized void await() throws InterruptedException {
        waiting -= 1;
        if (waiting == 0) {
            this.notifyAll();
        } else {
            while (waiting > 0) {
                this.wait();
            }
        }
    }

    public synchronized Client getAndSetWinner(Client client) {
        if (winner == null) {
            winner = client;
        }
        return winner;
    }

    public synchronized boolean done() {
        return winner != null;
    }

    public static interface GameParticipant {
        void notifyGameStart(String secretWord);
        void notifyWinner(Client client);
        String getAnswer();
        Client getClient();
    }
}
