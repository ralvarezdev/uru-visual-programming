package sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.function.BiConsumer;

public class BidirectionalClientSocketHandler extends Thread {
    private final Socket CLIENT_SOCKET;
    private final BiConsumer<Socket, String> HANDLER;
    private final boolean PRINT_SOCKET_MESSAGES;

    public BidirectionalClientSocketHandler(Socket clientSocket, BiConsumer<Socket, String> handler, boolean printSocketMessages) {
        if (clientSocket == null)
            throw new NullPointerException("%s: Client socket is null...".formatted(getName()));

        if (handler == null)
            throw new NullPointerException("%s: Client socket handler is null...".formatted(getName()));

        CLIENT_SOCKET = clientSocket;
        HANDLER = handler;
        PRINT_SOCKET_MESSAGES = printSocketMessages;
    }

    public void run() {
        String name = PRINT_SOCKET_MESSAGES ? getName() : null;
        HANDLER.accept(CLIENT_SOCKET, name);

        try {
            CLIENT_SOCKET.close();

            if (PRINT_SOCKET_MESSAGES)
                System.out.printf("%s: Client socket successfully closed...%n", getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
