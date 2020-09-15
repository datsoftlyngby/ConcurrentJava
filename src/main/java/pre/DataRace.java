package pre;

import java.util.List;

public class DataRace implements Runnable {
    public static volatile int counter = 0;

    @Override
    public void run() {
        for (int i = 0; i <= 10000; i++) {
            counter += 1;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads =
                List.of(new Thread(new DataRace ()), new Thread(new DataRace()));

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println(counter);
    }

}
