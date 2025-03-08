package connections;

import files.ResourceGetter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CustThread extends Thread {
    private final ResourceGetter resourceGetter;
    private final String resourceFilename;

    public CustThread(ResourceGetter resourceGetter, String resourceFilename) {
        this.resourceGetter = resourceGetter;
        this.resourceFilename = resourceFilename;
    }

    @Override
    public void run() {
        Database db = new Database(resourceGetter, resourceFilename);
        Connection conn = db.getConnection();
        Statement statement;

        try {
            statement = conn.createStatement();

        } catch (SQLException e) {
            // Fatal error. Close given connection
            e.printStackTrace();

            try {
                conn.close();
            } catch (SQLException f) {
                f.printStackTrace();
            }
            return;
        }

        try {
            // Select query
            String selectQuery = """
                    SELECT * FROM prod
                    """;

            // Execute query
            statement.execute(selectQuery);
            System.out.println("Thread " + this.getName() + " completed and closed successfully...");

        } catch (SQLException e) {
            // e.printStackTrace();
        } finally {
            try {
                statement.close();
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
