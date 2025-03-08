package connections;

import files.DefaultResourceGetter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String connectionsFilename = "connections-db.properties";
        DefaultResourceGetter resourcePathGetter = new DefaultResourceGetter(Database.class);

        Database db = new Database(resourcePathGetter, connectionsFilename);
        Connection conn = db.getConnection();
        Statement statement = null;

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
            System.exit(-1);
        }

        try {
            // Create table query
            String createProdQuery = """
                    CREATE TABLE IF NOT EXISTS prod (
                    	id_producto SERIAL PRIMARY KEY,
                    	de_producto VARCHAR(1000)
                    );
                    """;

            // Execute query
            statement.execute(createProdQuery);

            // Check if table is empty
            String isEmptyQuery = """
                    SELECT COUNT(*) AS number FROM prod
                    """;

            // Execute query
            ResultSet rs = statement.executeQuery(isEmptyQuery);
            int number;

            rs.next();
            number = Integer.parseInt(rs.getString("number"));

            if (number == 0)
                for (int i = 0; i < 10000; i++) {
                    // Insert query
                    String insertQuery = "INSERT INTO prod(de_producto) VALUES ('producto_" + i + "')";

                    // Execute query
                    statement.execute(insertQuery);
                }

        } catch (SQLException e) {
            // Fatal error. Close given statement and connection
            e.printStackTrace();

            try {
                statement.close();
                conn.close();

            } catch (SQLException f) {
                f.printStackTrace();
            }
            System.exit(-1);
        }

        // Database stress test

        // Add thread upper bounds
        LinkedList<Integer> threadUpperBounds = new LinkedList<>();
        for (int i = 1; i <= 100000; i *= 10)
            threadUpperBounds.add(i);

        int threadsCounter = 0;

        for (Integer upperBound : threadUpperBounds) {
            for (; threadsCounter < upperBound; threadsCounter++) {
                CustThread threadCust = new CustThread(resourcePathGetter, connectionsFilename);
                threadCust.setName("" + threadsCounter);
                threadCust.start();
            }
            System.out.println("Already started " + threadsCounter + " threads...");
        }
    }
}
