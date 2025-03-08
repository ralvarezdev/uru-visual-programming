package filetransfer;

import sockets.BidirectionalServerSocket;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Random;

public class FileTransferServerSocket extends BidirectionalServerSocket {
    private final String ROOT_PATH;

    public FileTransferServerSocket(String rootPath, FileTransferClientSocketBuffers bufferSize, boolean printServerMessages, boolean printSocketMessages) throws NullPointerException {
        super(printServerMessages, printSocketMessages);
        ROOT_PATH = rootPath;

        // Set client socket handler
        setSocketHandler((Socket clientSocket, String name) -> {
            Random random = new Random();

            // Print messages
            boolean PRINT_MESSAGES = name != null;

            // Output-related
            OutputStream outputStream = null;
            PrintWriter printWriter = null;

            // Input-related
            InputStream inputStream = null;
            InputStreamReader streamReader = null;
            BufferedReader reader = null;

            try {
                outputStream = clientSocket.getOutputStream();
                printWriter = new PrintWriter(outputStream, true);

                inputStream = clientSocket.getInputStream();
                streamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(streamReader);

                String line;

                if (PRINT_MESSAGES)
                    System.out.printf("%s: Waiting for socket messages...%n", NAME);

                while ((line = reader.readLine()) != null && !line.equals(FileTransferClientSocketMessages.END.toString())) {
                    String baseFilename = reader.readLine();
                    String fullFilename = String.format("%d-%s", System.currentTimeMillis(), baseFilename);
                    File filePath = new File(Path.of(ROOT_PATH, fullFilename).toString());

                    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                            byte[] buffer = new byte[bufferSize.getSize()];
                            int count;

                            while ((count = inputStream.read(buffer)) > 0)
                                bufferedOutputStream.write(buffer, 0, count);
                        }
                    }

                    if (PRINT_MESSAGES)
                        System.out.printf("%s: Received file from socket...%n", NAME);
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            if (PRINT_SOCKET_MESSAGES)
                System.out.printf("%s: Closing socket...%n", NAME);

            try {
                reader.close();
                streamReader.close();
                inputStream.close();

                printWriter.close();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public FileTransferServerSocket(String rootPath, FileTransferClientSocketBuffers bufferSize)
            throws NullPointerException {
        this(rootPath, bufferSize, false, false);
    }

    public Thread startThread(int port) {
        Runnable runnable = () -> {
            try {
                super.start(port);

            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return thread;
    }

    public Thread startThread() {
        return startThread(0);
    }
}