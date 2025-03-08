package pools;

import exceptions.MissingPropertyException;
import files.PropertiesReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

public enum DatabaseTags {
    POSTGRES_PRODUCTS(SqlDrivers.POSTGRES, "producto"), MYSQL_PRODUCTS(SqlDrivers.MYSQL, "producto");

    private final static String DB_PROPS_FILENAME = "pools-db.properties";
    private final static String POOL_PROPS_FILENAME = "pools-pool.properties";

    private final SqlDrivers DRIVER;
    private final String DATABASE_TAG_NAME;

    DatabaseTags(SqlDrivers driver, String databaseName) {
        DRIVER = driver;
        DATABASE_TAG_NAME = databaseName;
    }

    public String getFieldDriverName() {
        return DRIVER.getFieldDriverName();
    }

    public String getUrlDriverName() {
        return DRIVER.getUrlDriverName();
    }

    public String getFieldDatabaseTagName() {
        return "%s_%s".formatted(getFieldDriverName(), DATABASE_TAG_NAME.toUpperCase());
    }

    public String getDatabaseTagName() {
        return "%s [%s]".formatted(getFieldDriverName(), DATABASE_TAG_NAME.toUpperCase());
    }

    private void checkProps(PropertiesReader propsReader, String propsFilename)
            throws NullPointerException {
        if (propsReader == null)
            throw new NullPointerException("Database properties reader is null.");

        if (propsFilename == null)
            throw new NullPointerException("Database properties filename is null.");
    }

    public Map<String, String> getDatabaseProperties(PropertiesReader propsReader)
            throws NullPointerException, IOException, MissingPropertyException {
        checkProps(propsReader, DB_PROPS_FILENAME);

        DatabaseProperties[] dbPropsFields = DatabaseProperties.values();
        LinkedList<String> dbPropsFieldsName = new LinkedList<>();

        for (DatabaseProperties dbPropsField : dbPropsFields)
            dbPropsFieldsName.add(dbPropsField.getFieldName(this));

        return propsReader.getProperties(DB_PROPS_FILENAME, dbPropsFieldsName);
    }

    public DatabaseConfig getDatabaseConfig(PropertiesReader propsReader)
            throws NullPointerException, IOException, MissingPropertyException {
        Map<String, String> dbProps = getDatabaseProperties(propsReader);

        // Get properties values
        String DBHOST = dbProps.get(DatabaseProperties.DBHOST.getFieldName(this));
        String DBPORT = dbProps.get(DatabaseProperties.DBPORT.getFieldName(this));
        String DBNAME = dbProps.get(DatabaseProperties.DBNAME.getFieldName(this));
        String DBUSER = dbProps.get(DatabaseProperties.DBUSER.getFieldName(this));
        String DBPASS = dbProps.get(DatabaseProperties.DBPASS.getFieldName(this));

        return new DatabaseConfig(this, DBHOST, DBPORT, DBNAME, DBUSER, DBPASS);
    }

    public Map<String, String> getDatabasePoolProperties(PropertiesReader propsReader) throws NullPointerException, IOException, MissingPropertyException {
        checkProps(propsReader, POOL_PROPS_FILENAME);

        DatabasePoolProperties[] poolPropsFields = DatabasePoolProperties.values();
        LinkedList<String> poolPropsFieldsName = new LinkedList<>();

        for (DatabasePoolProperties poolPropsField : poolPropsFields)
            poolPropsFieldsName.add(poolPropsField.getFieldName(this));

        return propsReader.getProperties(POOL_PROPS_FILENAME, poolPropsFieldsName);
    }

    public PoolConfig getDatabasePoolConfig(PropertiesReader propsReader)
            throws NullPointerException, IOException, MissingPropertyException {
        Map<String, String> poolProps = getDatabasePoolProperties(propsReader);

        // Get properties values
        int INIT_CONNS = Integer.parseInt(poolProps.get(DatabasePoolProperties.INIT_CONNS.getFieldName(this)));
        int INCR_CONNS = Integer.parseInt(poolProps.get(DatabasePoolProperties.INCR_CONNS.getFieldName(this)));
        int MAX_CONNS = Integer.parseInt(poolProps.get(DatabasePoolProperties.MAX_CONNS.getFieldName(this)));

        return new PoolConfig(INIT_CONNS, INCR_CONNS, MAX_CONNS);
    }
}
