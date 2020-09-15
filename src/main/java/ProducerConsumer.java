import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumer implements Runnable {

    private final BlockingQueue<String> queue;

    public ProducerConsumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }


    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                String msg = queue.take();
                System.out.println(msg);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        Thread t = new Thread(new ProducerConsumer(queue));
        t.start();
        queue.put("Hello");
        queue.put("World");
        queue.put("World");
        queue.put("World");
        queue.put("World");
        queue.put("World");
        queue.put("World");
        System.out.println(queue.size());
        queue.put("World");
        System.out.println(queue.size());
        System.out.println("Waiting");
        System.out.println(queue.size());
        queue.put("World");
        System.out.println(queue.size());
        System.out.println("Waiting");
        queue.put("World");
        System.out.println(queue.size());
        System.out.println("Waiting");

        t.join(10000);
        t.interrupt();
        t.join();

    }

}
