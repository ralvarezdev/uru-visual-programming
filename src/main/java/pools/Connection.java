package pools;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface Connection {
    boolean connect(DatabaseConfig config, boolean autoCommit);

    void disconnect();

    boolean commit();

    boolean rollback();

    boolean isClosed();

    boolean isValid();

    void createPreparedStatement(String sql);

    void closePreparedStatement();

    void setStringParameter(int paramCounter, String param) throws NullPointerException, SQLException;

    void setIntParameter(int paramCounter, int param) throws NullPointerException, SQLException;

    void setFloatParameter(int paramCounter, float param) throws NullPointerException, SQLException;

    void setDoubleParameter(int paramCounter, double param) throws NullPointerException, SQLException;

    void setBigDecimalParameter(int paramCounter, BigDecimal param) throws NullPointerException, SQLException;

    void setLongParameter(int paramCounter, long param) throws NullPointerException, SQLException;

    Integer executeUpdate() throws NullPointerException;

    <T> List<T> executeQuery(ResultSetFunction<T> func) throws NullPointerException;
}
