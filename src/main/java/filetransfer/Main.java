package filetransfer;

import exceptions.MissingPropertyException;
import files.DefaultDataPathGetter;
import files.DefaultPropertiesReader;
import sockets.BidirectionalServerSocket;

import java.io.IOException;

public class Main {
    private static FileTransferClientSocketBuffers BUFFER_SIZE;

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

        BUFFER_SIZE = FileTransferClientSocketBuffers.MEDIUM;
        FileTransferServerSocket serverSocket = null;

        try {
            // Initialize server socket
            serverSocket = getServerSocket();
            serverSocket.startThread(port);

            // Wait n milliseconds to start client socket
            Thread.sleep(CLIENT_SOCKET_DELAY);

        } catch (InterruptedException | IOException e) {
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

    public static FileTransferServerSocket getServerSocket() throws IOException {
        boolean PRINT_SERVER_MESSAGES = true;
        boolean PRINT_SOCKET_MESSAGES = false;

        DefaultDataPathGetter dataPathGetter = new DefaultDataPathGetter();
        String rootPath = dataPathGetter.getTargetDataPath().toString();

        return new FileTransferServerSocket(rootPath, BUFFER_SIZE, PRINT_SERVER_MESSAGES, PRINT_SOCKET_MESSAGES);
    }

    public static void initClientSocket(String ip, int port) throws NullPointerException, IOException {
        String PEOPLE_CSV_FILENAME = "people.csv";

        boolean PRINT_SOCKET_MESSAGES = true;

        FileTransferClientSocket clientSocket = null;

        // Get file content
        try {
            clientSocket = new FileTransferClientSocket(PRINT_SOCKET_MESSAGES);
            DefaultDataPathGetter dataPathGetter = new DefaultDataPathGetter();
            String rootPath = dataPathGetter.getSrcDataPath().toString();

            clientSocket.start(ip, port);
            clientSocket.send(rootPath, PEOPLE_CSV_FILENAME, BUFFER_SIZE);

        } catch (IOException e) {
            e.printStackTrace();
        }

        clientSocket.close();
    }
}
