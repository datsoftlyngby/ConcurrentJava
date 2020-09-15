import java.io.IOException;
import java.util.Scanner;

public class WaitForInput implements Runnable {
    private final Scanner s;
    private String message;

    public WaitForInput(Scanner s) {
        this.s = s;
    }

    @Override
    public void run() {
        setMessage(s.nextLine());
    }

    public synchronized void setMessage(String message) {
        this.message = message;
        this.notify();
    }
    public synchronized String getMessage(long millies) throws InterruptedException {
        if (message == null) {
            this.wait(millies);
        }
        return message;
    }

    public static void main(String[] args) {
        WaitForInput w = new WaitForInput(new Scanner(System.in));

        Thread t = new Thread(w);

        try {
            System.out.println("? ");
            t.start();
            System.out.println(w.getMessage(10000));
            System.out.println("Closing down");
            t.interrupt();
            System.in.close();
            System.out.println("Iterrupt");
            t.join(1000);
            System.out.println("Done");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }


    }
}
