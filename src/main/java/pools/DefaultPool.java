package pools;

import exceptions.ConnectionException;

import java.util.LinkedList;

public abstract class DefaultPool implements Pool {
    protected final String POOL_NAME;
    protected final DatabaseConfig DB_CONFIG;
    protected final PoolConfig POOL_CONFIG;
    protected final LinkedList<Connection> CONNECTIONS;
    protected final int ATTEMPTS;
    protected final boolean AUTO_COMMIT;

    protected boolean PRINT_POOL_MESSAGES;
    protected boolean PRINT_CONNECTION_MESSAGES;

    protected int currMaxSize = 0;

    protected DefaultPool(DatabaseConfig dbConfig, PoolConfig poolConfig,
                          boolean autoCommit, boolean printPoolMessages, boolean printConnectionMessages, int attempts)
            throws NullPointerException {
        if (dbConfig == null || poolConfig == null)
            throw new NullPointerException("There are some null configurations...");

        // Set static attributes
        POOL_NAME = "%s POOL".formatted(dbConfig.tag().getDatabaseTagName());
        DB_CONFIG = dbConfig;
        POOL_CONFIG = poolConfig;
        AUTO_COMMIT = autoCommit;

        ATTEMPTS = (attempts < 1) ? 5 : attempts;

        PRINT_POOL_MESSAGES = printPoolMessages;
        PRINT_CONNECTION_MESSAGES = printConnectionMessages;

        if (PRINT_POOL_MESSAGES)
            System.out.printf("%s: Initializing pool...%n", POOL_NAME);

        // Initialize pool connections
        CONNECTIONS = new LinkedList<>();
        increasePoolSize(POOL_CONFIG.incrConns());
    }

    protected DefaultPool(DatabaseConfig dbConfig, PoolConfig poolConfig,
                          boolean autoCommit, boolean printPoolMessages, boolean printConnectionMessages)
            throws NullPointerException {
        this(dbConfig, poolConfig, autoCommit, printPoolMessages, printConnectionMessages, 5);
    }

    protected DefaultPool(DatabaseConfig dbConfig, PoolConfig poolConfig)
            throws NullPointerException {
        this(dbConfig, poolConfig, true, false, false);
    }

    public void setPrintPoolMessages(boolean printPoolMessages) {
        PRINT_POOL_MESSAGES = printPoolMessages;
    }

    public void setPrintConnectionsMessages(boolean printConnectionsMessages) {
        PRINT_CONNECTION_MESSAGES = printConnectionsMessages;
    }

    public DatabaseConfig getDatabaseConfig() {
        return DB_CONFIG;
    }

    public synchronized boolean increasePoolSize(int incrConns) {
        if (currMaxSize + incrConns > POOL_CONFIG.maxConns())
            return false;

        if (PRINT_POOL_MESSAGES)
            System.out.printf("%s: Increasing pool size from %d to %d...%n", POOL_NAME, currMaxSize,
                    currMaxSize + incrConns);

        for (int i = 0; i < incrConns; )
            try {
                CONNECTIONS.add(
                        new DefaultConnection(DB_CONFIG, AUTO_COMMIT, PRINT_CONNECTION_MESSAGES, ATTEMPTS));
                i++;
            } catch (ConnectionException e) {
                e.printStackTrace();
            }

        currMaxSize += incrConns;
        return true;
    }

    public synchronized boolean increasePoolSize() {
        return increasePoolSize(POOL_CONFIG.incrConns());
    }

    public synchronized int getSize() {
        return CONNECTIONS.size();
    }

    public synchronized Connection getConnection() {
        int size = getSize();

        if (size > 0)
            return CONNECTIONS.remove(size - 1);

        if (!increasePoolSize())
            while (size == 0)
                try {
                    wait();
                    size = getSize();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
        size = getSize();

        return CONNECTIONS.remove(size - 1);
    }

    public synchronized void putConnection(Connection connection) {
        CONNECTIONS.add(connection);
        notifyAll();
    }

    public synchronized void disconnectAll() {
        try {
            while (currMaxSize != CONNECTIONS.size()) {
                System.out.println("Waiting until some connections are returned...");
                wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        for (Connection connection : CONNECTIONS) {
            connection.disconnect();
            currMaxSize--;
        }

        if (PRINT_POOL_MESSAGES)
            System.out.printf("%s: Pool successfully disconnected...%n", POOL_NAME);
    }
}