package pools;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public final class DefaultPoolManager implements PoolManager {
    private final Pool POOL;
    private final String POOL_MANAGER_NAME;
    private final boolean PRINT_MESSAGES;

    private Connection connection;

    public DefaultPoolManager(Pool pool, boolean printMessages) throws NullPointerException {
        if (pool == null)
            throw new NullPointerException("Pool is null.");

        POOL = pool;
        POOL_MANAGER_NAME = "%s POOL MANAGER".formatted(pool.getDatabaseConfig().tag().getDatabaseTagName());
        PRINT_MESSAGES = printMessages;
    }

    private synchronized boolean isNull() {
        return connection == null;
    }

    public synchronized boolean isValid() {
        if (connection != null)
            return connection.isValid();
        return false;
    }

    public synchronized void getConnection() {
        if (PRINT_MESSAGES)
            System.out.printf("%s: Getting connection from pool...%n", POOL_MANAGER_NAME);

        connection = POOL.getConnection();
    }

    public synchronized void putConnection() {
        if (!isNull()) {
            if (PRINT_MESSAGES)
                System.out.printf("%s: Returning connection to pool...%n", POOL_MANAGER_NAME);

            POOL.putConnection(connection);
            connection = null;
        }
    }

    public synchronized void commit() {
        if (isValid()) {
            if (PRINT_MESSAGES)
                System.out.printf("%s: Comitting...%n", POOL_MANAGER_NAME);

            connection.commit();
        }
    }

    public synchronized void rollback() {
        if (isValid()) {
            if (PRINT_MESSAGES)
                System.out.printf("%s: Rolling back...%n", POOL_MANAGER_NAME);

            connection.rollback();
        }
    }

    public synchronized void createPreparedStatement(String sql) {
        if (isValid())
            connection.createPreparedStatement(sql);
    }

    public synchronized void closePreparedStatement() {
        if (isValid())
            connection.closePreparedStatement();
    }

    public synchronized void setStringParameter(int paramCounter, String param)
            throws NullPointerException, SQLException {
        if (isValid())
            connection.setStringParameter(paramCounter, param);
    }

    public synchronized void setIntParameter(int paramCounter, int param) throws NullPointerException, SQLException {
        if (isValid())
            connection.setIntParameter(paramCounter, param);
    }

    public synchronized void setFloatParameter(int paramCounter, float param)
            throws NullPointerException, SQLException {
        if (isValid())
            connection.setFloatParameter(paramCounter, param);
    }

    public synchronized void setDoubleParameter(int paramCounter, double param)
            throws NullPointerException, SQLException {
        if (isValid())
            connection.setDoubleParameter(paramCounter, param);
    }

    public synchronized void setBigDecimalParameter(int paramCounter, BigDecimal param)
            throws NullPointerException, SQLException {
        if (isValid())
            connection.setBigDecimalParameter(paramCounter, param);
    }

    public synchronized void setLongParameter(int paramCounter, long param) throws NullPointerException, SQLException {
        if (isValid())
            connection.setLongParameter(paramCounter, param);
    }

    public synchronized Integer executeUpdate() {
        if (isValid()) {
            if (PRINT_MESSAGES)
                System.out.printf("%s: Executing update...%n", POOL_MANAGER_NAME);

            return connection.executeUpdate();
        }
        return null;
    }

    public synchronized <T> List<T> executeQuery(ResultSetFunction<T> func) {
        if (isValid()) {
            if (PRINT_MESSAGES)
                System.out.printf("%s: Executing query...%n", POOL_MANAGER_NAME);

            return connection.executeQuery(func);
        }
        return null;
    }
}
