package dbcomponent;

import exceptions.MissingPropertyException;
import files.DefaultPropertiesReader;
import files.PropertiesReader;
import pools.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class DefaultDbComponent implements DbComponent {
    private final static String DATABASE_SENTENCES_FILENAME = "dbcomponent-mysql-sentences.properties";

    private final static PropertiesReader DBCOMPONENT_PROPS_READER;
    private final static List<DatabaseTags> DBCOMPONENT_DB;
    private final static HashMap<DatabaseTags, Map<String, String>> DB_SENTENCES;
    private final static HashMap<DatabaseTags, Pool> POOLS;

    private final HashMap<DatabaseTags, PoolManager> POOL_MANAGERS;
    private final boolean PRINT_POOL_MANAGER_MESSAGES;

    private DatabaseTags DEFAULT_DB = null;

    static {
        DBCOMPONENT_PROPS_READER = new DefaultPropertiesReader(DbComponent.class);
        DBCOMPONENT_DB = new LinkedList<>(Arrays.asList(DatabaseTags.values()));
        DB_SENTENCES = new HashMap<>();
        POOLS = new HashMap<>();

        try {
            for (DatabaseTags database : DBCOMPONENT_DB) {
                DB_SENTENCES.put(database, new HashMap<>());

                // Load pools
                loadPool(database);

                // Load sentences
                loadSentences(database, DATABASE_SENTENCES_FILENAME, "SELECT_ALL", "SELECT_ALL_BYID", "INSERT");
            }
        } catch (IOException | MissingPropertyException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public DefaultDbComponent(boolean printPoolManagerMessages)
            throws NullPointerException {
        POOL_MANAGERS = new HashMap<>();
        PRINT_POOL_MANAGER_MESSAGES = printPoolManagerMessages;
    }

    public DefaultDbComponent(List<DatabaseTags> databases, boolean printPoolManagerMessages) throws NullPointerException, IOException, MissingPropertyException {
        this(printPoolManagerMessages);

        var databasesSet = Set.copyOf(databases);

        for (DatabaseTags database : databasesSet)
            loadPoolManager(database);
    }

    public DefaultDbComponent(DatabaseTags[] databases, boolean printPoolManagerMessages) throws NullPointerException, IOException, MissingPropertyException {
        this(Arrays.asList(databases), printPoolManagerMessages);
    }

    private static void checkDatabase(DatabaseTags database) throws NullPointerException {
        if (database == null)
            throw new NullPointerException("Database is null.");
    }

    private static boolean hasDatabase(DatabaseTags database) {
        return DBCOMPONENT_DB.contains(database);
    }

    private static void checkPool(DatabaseTags database) throws NullPointerException {
        checkDatabase(database);

        if (!POOLS.containsKey(database))
            throw new NullPointerException("Database pool hasn't been loaded.");
    }

    private synchronized void checkPoolManager(DatabaseTags database) throws NullPointerException {
        checkDatabase(database);

        if (!POOL_MANAGERS.containsKey(database))
            throw new NullPointerException("Database pool manager hasn't been loaded.");
    }

    private static void checkDatabaseSentences(DatabaseTags database) throws NullPointerException {
        checkDatabase(database);

        if (!DB_SENTENCES.containsKey(database))
            throw new NullPointerException("Database sentences haven't been loaded.");
    }

    private synchronized void checkDefaultDatabase() throws NullPointerException {
        if (DEFAULT_DB == null)
            throw new NullPointerException("Default database hasn't been set.");
    }

    private synchronized PoolManager getPoolManager(DatabaseTags database) throws NullPointerException {
        checkPoolManager(database);
        return POOL_MANAGERS.get(database);
    }

    private static Pool getPool(DatabaseTags database) throws NullPointerException {
        checkPool(database);
        return POOLS.get(database);
    }

    public synchronized DatabaseTags getDefaultDatabases() throws NullPointerException {
        checkDefaultDatabase();
        return DEFAULT_DB;
    }

    public synchronized void setDefaultDatabase(DatabaseTags database) throws NullPointerException {
        checkPoolManager(database);
        DEFAULT_DB = database;
    }

    private static void loadPool(DatabaseTags database)
            throws NullPointerException, IOException, MissingPropertyException {
        checkDatabase(database);

        Pool pool = switch (database) {
            case POSTGRES_PRODUCTS -> DefaultPostgresProductsPool.getInstance();
            case MYSQL_PRODUCTS -> DefaultMySqlProductsPool.getInstance();
            default -> throw new NullPointerException("Database pool not found.");
        };

        POOLS.put(database, pool);
    }

    public synchronized void loadPoolManager(DatabaseTags database)
            throws NullPointerException, IOException, MissingPropertyException {
        checkDatabase(database);

        if (POOL_MANAGERS.containsKey(database))
            return;

        if (!hasDatabase(database)) {
            DBCOMPONENT_DB.add(database);
            loadPool(database);
        }

        PoolManager poolManager = new DefaultPoolManager(POOLS.get(database), PRINT_POOL_MANAGER_MESSAGES);
        POOL_MANAGERS.put(database, poolManager);

        if (POOL_MANAGERS.size() == 1)
            setDefaultDatabase(database);
    }

    public void getConnection(DatabaseTags database) throws NullPointerException {
        getPoolManager(database).getConnection();
    }

    public void putConnection(DatabaseTags database) throws NullPointerException {
        getPoolManager(database).putConnection();
    }

    public static void loadSentences(DatabaseTags database, String resourceFilename, List<String> sentenceFieldsName)
            throws NullPointerException, IOException, MissingPropertyException {
        Map<String, String> sentencesMap = DBCOMPONENT_PROPS_READER.getProperties(resourceFilename, sentenceFieldsName);
        DB_SENTENCES.put(database, sentencesMap);
    }

    public static void loadSentences(DatabaseTags database, String resourceFilename, String... sentenceFieldsName)
            throws NullPointerException, IOException, MissingPropertyException {
        loadSentences(database, resourceFilename, Arrays.asList(sentenceFieldsName));
    }

    public void loadSentences(String resourceFilename, List<String> sentenceFieldsName)
            throws NullPointerException, IOException, MissingPropertyException {
        DatabaseTags database = getDefaultDatabases();
        loadSentences(database, resourceFilename, sentenceFieldsName);
    }

    public static String getSentence(DatabaseTags database, String sentenceFieldName) throws NullPointerException {
        checkDatabaseSentences(database);

        String sentenceFieldValue = DB_SENTENCES.get(database).get(sentenceFieldName);

        if (sentenceFieldValue != null)
            return sentenceFieldValue;

        throw new NullPointerException("Database sentence not found.");
    }

    public String getSentence(String sentenceFieldName) throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        return getSentence(database, sentenceFieldName);
    }

    public synchronized void createPreparedStatement(DatabaseTags database, String sentenceFieldName)
            throws NullPointerException {
        String sql = getSentence(database, sentenceFieldName);
        getPoolManager(database).createPreparedStatement(sql);
    }

    public synchronized void closePreparedStatement(DatabaseTags database) throws NullPointerException {
        getPoolManager(database).closePreparedStatement();
    }

    public synchronized void setStringParameter(DatabaseTags database, int paramCounter, String param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setStringParameter(paramCounter, param);
    }

    public synchronized void setIntParameter(DatabaseTags database, int paramCounter, int param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setIntParameter(paramCounter, param);
    }

    public synchronized void setFloatParameter(DatabaseTags database, int paramCounter, float param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setFloatParameter(paramCounter, param);
    }

    public synchronized void setDoubleParameter(DatabaseTags database, int paramCounter, double param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setDoubleParameter(paramCounter, param);
    }

    public synchronized void setBigDecimalParameter(DatabaseTags database, int paramCounter, BigDecimal param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setBigDecimalParameter(paramCounter, param);
    }

    public synchronized void setLongParameter(DatabaseTags database, int paramCounter, long param)
            throws NullPointerException, SQLException {
        getPoolManager(database).setLongParameter(paramCounter, param);
    }

    public Integer executeUpdate(DatabaseTags database) throws NullPointerException {
        return getPoolManager(database).executeUpdate();
    }

    public <T> List<T> executeQuery(DatabaseTags database, ResultSetFunction<T> func) throws NullPointerException {
        return getPoolManager(database).executeQuery(func);
    }

    public static void disconnectAll(DatabaseTags database) {
        getPool(database).disconnectAll();
    }

    public static void disconnectAll() {
        for (DatabaseTags database : DBCOMPONENT_DB)
            disconnectAll(database);
    }

    public void disconnectAllFromDefaultPool() {
        DatabaseTags database = getDefaultDatabases();
        disconnectAll(database);
    }
}
