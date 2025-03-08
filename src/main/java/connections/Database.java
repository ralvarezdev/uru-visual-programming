package connections;

import exceptions.MissingPropertyException;
import files.ResourceGetter;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class Database {
    private final static String DB_PROPS_FILENAME = "connections-db.properties";

    private static boolean loadedProps = false;
    private static String DBHOST;
    private static String DBPORT;
    private static String DBNAME;
    private static String DBUSER;
    private static String DBPASS;

    private static boolean connected = false;
    private static Connection connection = null;

    public Database(ResourceGetter resourceGetter, String connectionsFilename) {
        if (!connected) {
            if (!loadedProps) {
                try {
                    String dbPropertiesPath = resourceGetter.getResourcePath(connectionsFilename);

                    Properties appProps = new Properties();
                    appProps.load(new FileInputStream(dbPropertiesPath));

                    DBHOST = appProps.getProperty("DBHOST");
                    DBPORT = appProps.getProperty("DBPORT");
                    DBNAME = appProps.getProperty("DBNAME");
                    DBUSER = appProps.getProperty("DBUSER");
                    DBPASS = appProps.getProperty("DBPASS");

                    List<String> paramsList = Arrays.asList(DBHOST, DBPORT, DBNAME, DBUSER, DBPASS);
                    LinkedList<String> paramsLinkedList = new LinkedList<>(paramsList);

                    for (String param : paramsLinkedList)
                        if (param == null)
                            throw new MissingPropertyException("Missing some database properties.");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }

                loadedProps = true;
            }

            // Open Connection to Database
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://" + DBHOST + ":" + DBPORT + "/" + DBNAME,
                        DBUSER, DBPASS);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Next line is commented just for testing purposes of
            // how the database stresses on unlimited connections
            // connected = true;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}