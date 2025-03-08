package dbcomponent;

import exceptions.MissingPropertyException;
import pools.DatabaseTags;
import pools.ResultSetFunction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean PRINT_POOL_MANAGER_MESSAGES = true;

        DatabaseTags DB = DatabaseTags.POSTGRES_PRODUCTS;
        String DB_NAME = DB.getDatabaseTagName();

        String SELECT_ALL = "SELECT_ALL";
        String SELECT_ALL_BYID = "SELECT_ALL_BYID";
        String INSERT = "INSERT";

        // Initialize DB Component
        DefaultDbComponent dbComponent = new DefaultDbComponent(PRINT_POOL_MANAGER_MESSAGES);

        try {
            dbComponent.loadPoolManager(DB);
            dbComponent.getConnection();

        } catch (IOException | MissingPropertyException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // - Execute Sentences
        List<String[]> results;

        ResultSetFunction<String[]> func = (result) -> {
            try {
                return new String[]{result.getString("id_producto"), result.getString("de_producto")};

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        try {
            // Select all sentence
            dbComponent.createPreparedStatement(SELECT_ALL);
            results = dbComponent.executeQuery(func);
            dbComponent.closePreparedStatement();

            System.out.printf("- Select all sentence from %s%n", DB_NAME);
            int lastID = results.size();

            if (lastID > 0) {
                System.out.println(Arrays.deepToString(results.getFirst()));
                System.out.println("...");
                System.out.println(Arrays.deepToString(results.getLast()));
            }
            System.out.println();

/*
              // Insert sentence
                dbComponent.createPreparedStatement(INSERT);
              dbComponent.setStringParameter(1, "Inserted from DB Component");
              dbComponent.executeUpdate(); dbComponent.closePreparedStatement(DB);

              System.out.printf("- Inserting to %s%n", DB_NAME);
              System.out.println(); lastID++;

 */


            // Select all by ID sentence
            dbComponent.createPreparedStatement(SELECT_ALL_BYID);
            dbComponent.setIntParameter(1, lastID);
            results = dbComponent.executeQuery(func);
            dbComponent.closePreparedStatement();

            System.out.printf("- Select all by ID sentence from %s%n", DB_NAME);

            if (!results.isEmpty())
                System.out.println(Arrays.deepToString(results.getFirst()));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Put connection
        dbComponent.putConnection();
        DefaultDbComponent.disconnectAll();
    }
}
