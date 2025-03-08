package pools;

import java.sql.SQLException;

public class CustThread extends Thread {
    private final PoolManager POOL_MANAGER;

    public CustThread(PoolManager poolManager) {
        POOL_MANAGER = poolManager;
    }

    @Override
    public void run() {
        POOL_MANAGER.getConnection();
        POOL_MANAGER.createPreparedStatement("SELECT * FROM prod");

        var prodIds = POOL_MANAGER.executeQuery((result) -> {
            try {
                return result.getString("id_producto");

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });

        POOL_MANAGER.closePreparedStatement();

        POOL_MANAGER.putConnection();

        System.out.printf("Thread %-5s: %-10d%n", this.getName(), prodIds.size());
    }
}
