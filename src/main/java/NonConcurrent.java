import java.util.ArrayList;
import java.util.List;

public class NonConcurrent implements Runnable {
    private final ArrayList<String> strings;

    public NonConcurrent(ArrayList<String> strings) {
        this.strings = strings;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            strings.add("Hello");
            for (String e: strings) {
                System.out.println(e);
            }
            strings.remove(2);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ArrayList<String> list = new ArrayList<>();
        NonConcurrent a = new NonConcurrent(list);
        NonConcurrent b = new NonConcurrent(list);
        List<Thread> threads = List.of(new Thread(a), new Thread(b));

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();

        System.out.println(list);
    }

}
