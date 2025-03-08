package sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;

public abstract class BidirectionalServerSocket {
    protected final boolean PRINT_SERVER_MESSAGES;
    protected final boolean PRINT_SOCKET_MESSAGES;

    protected String NAME;
    protected java.net.ServerSocket SERVER_SOCKET;
    protected int PORT;
    protected BiConsumer<Socket, String> SOCKET_HANDLER = null;

    public BidirectionalServerSocket(boolean printServerMessages, boolean printSocketMessages) {
        PRINT_SERVER_MESSAGES = printServerMessages;
        PRINT_SOCKET_MESSAGES = printSocketMessages;
    }

    public BidirectionalServerSocket() {
        this(false, false);
    }

    protected void setSocketHandler(BiConsumer<Socket, String> socketHandler) {
        SOCKET_HANDLER = socketHandler;

        if (PRINT_SERVER_MESSAGES)
            System.out.printf("SERVER SOCKET: Successfully set socket handler...%n");
    }

    protected void start(int port) throws NullPointerException, IOException {
        SERVER_SOCKET = new java.net.ServerSocket(port);
        PORT = (port != 0) ? port : SERVER_SOCKET.getLocalPort();
        NAME = "SERVER SOCKET %d".formatted(PORT);

        if (PRINT_SERVER_MESSAGES)
            System.out.printf("Initializing server socket at port %d%n", PORT);

        if (SOCKET_HANDLER == null)
            throw new NullPointerException("Socket handler hasn't been set...");

        while (true) {
            Socket socket;

            try {
                socket = SERVER_SOCKET.accept();

            } catch (IOException e) {
                if (PRINT_SERVER_MESSAGES)
                    System.out.printf("%s: Server socket successfully closed...%n", NAME);

                break;
            }

            if (PRINT_SERVER_MESSAGES)
                System.out.printf("%s: Accepted incoming connection...%n", NAME);

            new BidirectionalClientSocketHandler(socket, SOCKET_HANDLER, PRINT_SOCKET_MESSAGES).start();
        }
    }

    public abstract Thread startThread(int port);

    public abstract Thread startThread();

    public void close() {
        if (SERVER_SOCKET == null)
            throw new NullPointerException("Server socket hasn't been started...");

        try {
            if (!SERVER_SOCKET.isClosed()) {
                if (PRINT_SERVER_MESSAGES)
                    System.out.printf("%s: Closing server socket...%n", NAME);

                SERVER_SOCKET.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}