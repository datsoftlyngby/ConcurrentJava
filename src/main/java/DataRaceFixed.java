import java.util.List;

public class DataRaceFixed implements Runnable {
    private static Object lock = new Object();
    private volatile static int count = 0;

    private final String name;

    public DataRaceFixed(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        synchronized (lock) {
            System.out.println(name);
            for (int i = 0; i < 10000; i++) {
                count += 1;
            }
        }
    }

    /// Example1 === Example2
    public synchronized void example1() {
        /// ...
    }

    public void example2() {
        synchronized (this) {
            /// ...
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DataRaceFixed d1 = new DataRaceFixed("D1");
        DataRaceFixed d2 = new DataRaceFixed("D2");

        List<Thread> threads = List.of(new Thread(d1), new Thread(d2));

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();

        System.out.println("Count: " + count);
    }

}
