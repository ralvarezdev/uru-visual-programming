package pools;

import java.sql.ResultSet;
import java.util.function.Function;

public interface ResultSetFunction<T> extends Function<ResultSet, T> {
}
