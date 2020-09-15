import java.util.List;

public class DataRace implements Runnable {
    private volatile static int count = 0;

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            count += 1;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DataRace d1 = new DataRace();
        DataRace d2 = new DataRace();

        List<Thread> threads = List.of(new Thread(d1), new Thread(d2));

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();

        System.out.println("Count: " + count);
    }

}
