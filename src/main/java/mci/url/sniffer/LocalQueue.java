package mci.url.sniffer;


import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LocalQueue {
    private static LocalQueue localQueue;
    private final BlockingQueue<String> sniffedDomains = new LinkedBlockingDeque<>();

    private static final Object LOCK = new Object();

    private LocalQueue() {
    }

    public static LocalQueue getInstance() {
        synchronized (LOCK) {
            if (localQueue == null) localQueue = new LocalQueue();
        }
        return localQueue;
    }

    public boolean addDomainToQueue(String key) {
        return sniffedDomains.offer(key);
    }


    public String takeDomain() {
        try {
            return  sniffedDomains.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
