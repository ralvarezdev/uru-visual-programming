package sockets;

import java.io.*;
import java.net.Socket;

public class BidirectionalClientSocket {
    protected Socket clientSocket;

    protected OutputStream outputStream;
    protected PrintWriter printWriter;

    protected InputStream inputStream;
    protected InputStreamReader streamReader;
    protected BufferedReader reader;

    protected final boolean PRINT_SOCKET_MESSAGES;

    public BidirectionalClientSocket(boolean printSocketMessages) {
        PRINT_SOCKET_MESSAGES = printSocketMessages;
    }

    public void start(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);

        // Output-related
        outputStream = clientSocket.getOutputStream();
        printWriter = new PrintWriter(outputStream, true);

        // Input-related
        inputStream = clientSocket.getInputStream();
        streamReader = new InputStreamReader(inputStream);
        reader = new BufferedReader(streamReader);

        if (PRINT_SOCKET_MESSAGES)
            System.out.println("Client socket connection successfully established...");
    }

    public String send(String msg) {
        if (!clientSocket.isClosed()) {
            printWriter.println(msg);

            try {
                return reader.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void close() {
        try {
            reader.close();
            streamReader.close();
            inputStream.close();

            outputStream.close();
            clientSocket.close();

            if (PRINT_SOCKET_MESSAGES)
                System.out.println("Client socket connection successfully closed...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
