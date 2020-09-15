import java.util.List;
import java.util.Objects;

public class Deadlock implements Runnable {
    private final String name;
    private final Object lockA;
    private final Object lockB;

    public Deadlock(String name, Object lockA, Object lockB) {
        this.name = name;
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        System.out.println(name + " is waiting for " + lockA);
        synchronized (lockA) {
           System.out.println(name + " is waiting for " + lockB);
           synchronized (lockB) {
               System.out.println(name + " can do stuff");
               System.out.println(name + " released " + lockB);
           }
           System.out.println(name + " released " + lockA);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object lockA = new Object(), lockB = new Object();
        Deadlock a = new Deadlock("A", lockA, lockB);
        Deadlock b = new Deadlock("B", lockB, lockA);

        List<Thread> threads = List.of(new Thread(a), new Thread(b));

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();

        System.out.println("Both done");
    }
}
