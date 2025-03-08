package filereadingtransfer;

import exceptions.MissingPropertyException;
import files.DefaultDataPathGetter;
import files.DefaultFileReader;
import files.DefaultFileWriter;
import files.DefaultPropertiesReader;
import sockets.BidirectionalServerSocket;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        String SERVER_PROPERTIES = "sockets-server.properties";
        int CLIENT_SOCKET_DELAY = 1000;

        String PORT_FIELDNAME = "SERVER_PORT";
        String IP_FIELDNAME = "SERVER_IP";

        DefaultPropertiesReader propsReader = new DefaultPropertiesReader(BidirectionalServerSocket.class);
        String ip = null;
        int port = 0;

        try {
            ip = propsReader.getProperty(SERVER_PROPERTIES, IP_FIELDNAME);
            String portString = propsReader.getProperty(SERVER_PROPERTIES, PORT_FIELDNAME);
            port = Integer.parseInt(portString);

        } catch (IOException | NumberFormatException | MissingPropertyException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Initialize server socket
        FileReadingTransferServerSocket serverSocket = getServerSocket();
        serverSocket.startThread(port);

        // Wait n milliseconds to start client socket
        try {
            Thread.sleep(CLIENT_SOCKET_DELAY);

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Initialize client socket and get file content
        try {
            initClientSocket(ip, port);

        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        serverSocket.close();
    }

    public static FileReadingTransferServerSocket getServerSocket() {
        String PEOPLE_CSV_FILENAME = "people.csv";
        int MAX_CHAR = 128;

        boolean PRINT_SERVER_MESSAGES = true;
        boolean PRINT_SOCKET_MESSAGES = false;

        FileReadingTransferServerSocket serverSocket = null;

        try {
            HashMap<String, String> FILES_PATH = new HashMap<>();
            DefaultFileReader fileReader = new DefaultFileReader();
            DefaultDataPathGetter dataPathGetter = new DefaultDataPathGetter();

            FILES_PATH.put("people", dataPathGetter.getSrcDataPath(PEOPLE_CSV_FILENAME).toString());

            serverSocket = new FileReadingTransferServerSocket(fileReader, FILES_PATH, MAX_CHAR, PRINT_SERVER_MESSAGES,
                    PRINT_SOCKET_MESSAGES);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return serverSocket;
    }

    public static void initClientSocket(String ip, int port) throws NullPointerException, IOException {
        String KEY = "people";
        String READ_CSV_FILENAME = "file-reading-transfer-people.csv";

        boolean PRINT_SOCKET_MESSAGES = true;

        FileReadingTransferClientSocket clientSocket = null;
        String content = null;

        // Get file content
        try {
            clientSocket = new FileReadingTransferClientSocket(PRINT_SOCKET_MESSAGES);
            clientSocket.start(ip, port);
            content = clientSocket.getFileContent(KEY);

        } catch (IOException e) {
            e.printStackTrace();
        }

        clientSocket.close();

        // Write file content
        DefaultFileWriter fileWriter = new DefaultFileWriter();
        DefaultDataPathGetter dataPathGetter = new DefaultDataPathGetter();
        fileWriter.overwriteTargetDataFileContent(dataPathGetter, READ_CSV_FILENAME, content);
    }
}
