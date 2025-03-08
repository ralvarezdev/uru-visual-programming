package pools;

import exceptions.MissingPropertyException;

import java.io.IOException;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        int MAX_THREADS = 1000;
        LinkedList<CustThread> threads = new LinkedList<>();

        Pool pool = null;

        try {
            pool = DefaultPostgresProductsPool.getInstance();
        } catch (IOException | MissingPropertyException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (int i = 1; i <= MAX_THREADS; i++) {
            PoolManager poolManager = new DefaultPoolManager(pool, false);

            CustThread thread = new CustThread(poolManager);
            threads.add(thread);

            thread.setName("" + i);
            thread.start();
        }

        // Wait for all threads to finish its tasks
        for (CustThread thread : threads)
            try {
                thread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        // Close all connections
        pool.disconnectAll();
    }
}