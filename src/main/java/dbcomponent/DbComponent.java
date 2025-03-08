package dbcomponent;

import exceptions.MissingPropertyException;
import pools.DatabaseTags;
import pools.ResultSetFunction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public interface DbComponent {
    DatabaseTags getDefaultDatabases() throws NullPointerException;

    void setDefaultDatabase(DatabaseTags database) throws NullPointerException;

    void loadPoolManager(DatabaseTags database)
            throws NullPointerException, IOException, MissingPropertyException;

    void getConnection(DatabaseTags database) throws NullPointerException, MissingPropertyException;

    default void getConnection() throws NullPointerException, MissingPropertyException {
        DatabaseTags database = getDefaultDatabases();
        getConnection(database);
    }

    void putConnection(DatabaseTags database) throws NullPointerException;

    default void putConnection() throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        putConnection(database);
    }

    String getSentence(String sentenceFieldName) throws NullPointerException;

    void loadSentences(String resourceFilename, List<String> sentenceFieldsName)
            throws NullPointerException, IOException, MissingPropertyException;

    default void loadSentences(String resourceFilename, String... sentenceFieldsName)
            throws NullPointerException, IOException, MissingPropertyException {
        loadSentences(resourceFilename, Arrays.asList(sentenceFieldsName));
    }

    void createPreparedStatement(DatabaseTags database, String sentenceFieldName) throws NullPointerException;

    default void createPreparedStatement(String sentenceFieldName) throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        createPreparedStatement(database, sentenceFieldName);
    }

    void closePreparedStatement(DatabaseTags database) throws NullPointerException;

    default void closePreparedStatement() throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        closePreparedStatement(database);
    }

    void setStringParameter(DatabaseTags database, int paramCounter, String param)
            throws NullPointerException, SQLException;

    default void setStringParameter(int paramCounter, String param) throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setStringParameter(database, paramCounter, param);
    }

    void setIntParameter(DatabaseTags database, int paramCounter, int param)
            throws NullPointerException, SQLException;

    default void setIntParameter(int paramCounter, int param) throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setIntParameter(database, paramCounter, param);
    }

    void setFloatParameter(DatabaseTags database, int paramCounter, float param)
            throws NullPointerException, SQLException;

    default void setFloatParameter(int paramCounter, float param) throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setFloatParameter(database, paramCounter, param);
    }

    void setDoubleParameter(DatabaseTags database, int paramCounter, double param)
            throws NullPointerException, SQLException;

    default void setDoubleParameter(int paramCounter, double param) throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setDoubleParameter(database, paramCounter, param);
    }

    void setBigDecimalParameter(DatabaseTags database, int paramCounter, BigDecimal param)
            throws NullPointerException, SQLException;

    default void setBigDecimalParameter(int paramCounter, BigDecimal param)
            throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setBigDecimalParameter(database, paramCounter, param);
    }

    void setLongParameter(DatabaseTags database, int paramCounter, long param)
            throws NullPointerException, SQLException;

    default void setLongParameter(int paramCounter, long param) throws NullPointerException, SQLException {
        DatabaseTags database = getDefaultDatabases();
        setLongParameter(database, paramCounter, param);
    }

    Integer executeUpdate(DatabaseTags database) throws NullPointerException;

    default Integer executeUpdate() throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        return executeUpdate(database);
    }

    <T> List<T> executeQuery(DatabaseTags database, ResultSetFunction<T> func) throws NullPointerException;

    default <T> List<T> executeQuery(ResultSetFunction<T> func) throws NullPointerException {
        DatabaseTags database = getDefaultDatabases();
        return executeQuery(database, func);
    }

    void disconnectAllFromDefaultPool();
}
