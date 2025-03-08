package pools;

import exceptions.MissingPropertyException;
import files.DefaultPropertiesReader;

import java.io.IOException;

public final class DefaultPostgresProductsPool extends DefaultPool {
    private static DefaultPostgresProductsPool INSTANCE;

    private DefaultPostgresProductsPool(DatabaseConfig dbConfig, PoolConfig poolConfig, boolean autoCommit,
                                        boolean printPoolMessages, boolean printConnectionMessages) {
        super(dbConfig, poolConfig, autoCommit, printPoolMessages,
                printConnectionMessages);
    }

    public synchronized static DefaultPostgresProductsPool getInstance()
            throws NullPointerException, IOException, MissingPropertyException {
        if (INSTANCE == null) {
            DefaultPropertiesReader propsReader = new DefaultPropertiesReader(Pool.class);
            DatabaseConfig dbConfig = DatabaseTags.POSTGRES_PRODUCTS.getDatabaseConfig(propsReader);
            PoolConfig poolConfig = DatabaseTags.POSTGRES_PRODUCTS.getDatabasePoolConfig(propsReader);

            INSTANCE = new DefaultPostgresProductsPool(dbConfig, poolConfig, true, true, false);
        }

        return INSTANCE;
    }
}
