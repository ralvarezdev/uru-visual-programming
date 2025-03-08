package pools;

public interface Pool {
    DatabaseConfig getDatabaseConfig();

    boolean increasePoolSize(int incrConns);

    boolean increasePoolSize();

    int getSize();

    Connection getConnection();

    void putConnection(Connection connection);

    void disconnectAll();
}
