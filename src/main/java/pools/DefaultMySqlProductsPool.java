package pools;

import exceptions.MissingPropertyException;
import files.DefaultPropertiesReader;

import java.io.IOException;

public final class DefaultMySqlProductsPool extends DefaultPool {
    private static DefaultMySqlProductsPool INSTANCE;

    private DefaultMySqlProductsPool(DatabaseConfig dbConfig, PoolConfig poolConfig, boolean autoCommit,
                                     boolean printPoolMessages, boolean printConnectionMessages) {
        super(dbConfig, poolConfig, autoCommit, printPoolMessages, printConnectionMessages);
    }

    public synchronized static DefaultMySqlProductsPool getInstance()
            throws NullPointerException, IOException, MissingPropertyException {
        if (INSTANCE == null) {
            DefaultPropertiesReader propsReader = new DefaultPropertiesReader(Pool.class);
            DatabaseConfig dbConfig = DatabaseTags.MYSQL_PRODUCTS.getDatabaseConfig(propsReader);
            PoolConfig poolConfig = DatabaseTags.MYSQL_PRODUCTS.getDatabasePoolConfig(propsReader);

            INSTANCE = new DefaultMySqlProductsPool(dbConfig, poolConfig, true, true, false);
        }

        return INSTANCE;
    }
}