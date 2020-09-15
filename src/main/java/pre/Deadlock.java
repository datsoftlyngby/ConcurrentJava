package pre;

import java.util.List;

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
        synchronized (lockA) {
            System.out.println(name + ": got Lock A");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lockB) {
                System.out.println(name + ": YEAH!");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object lockA = new Object(), lockB = new Object();
        Deadlock a = new Deadlock("A", lockA, lockB);
        Deadlock b = new Deadlock("B", lockB, lockA);

        List<Thread> threads =
                List.of(new Thread(a), new Thread(b));

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

    }

}
