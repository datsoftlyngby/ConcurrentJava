import java.util.List;
import java.util.Scanner;

public class WaitNotify implements Runnable {
    private volatile String message;

    public synchronized void setMessage(String message) {
        this.message = message;
        this.notify();
    }

    @Override
    public synchronized void run() {
        while(message == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.out.println("Did not print message, exiting gracefully!");
                return;
            }
        }
        System.out.println(message);
    }

    public static void main(String[] args) {
        WaitNotify n = new WaitNotify();
        Thread t = new Thread(n);

        t.start();
        try {
            Thread.sleep(100);
            n.setMessage("Hello, World!");
            t.join(1000);
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
