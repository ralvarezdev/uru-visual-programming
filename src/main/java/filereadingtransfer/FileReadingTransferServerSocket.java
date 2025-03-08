package filereadingtransfer;

import files.DefaultFileReader;
import sockets.BidirectionalServerSocket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FileReadingTransferServerSocket extends BidirectionalServerSocket {
    private final Map<String, String[]> FILES_CONTENT;
    private final int MAX_CHAR;

    private PrintWriter printWriter;

    public FileReadingTransferServerSocket(DefaultFileReader fileReader, Map<String, String> filesPath, int maxChar,
                                           boolean printServerMessages, boolean printSocketMessages) throws NullPointerException, IOException {
        super(printServerMessages, printSocketMessages);

        if (filesPath == null)
            throw new NullPointerException("Files path are null...");

        if (fileReader == null)
            throw new NullPointerException("File reader is null...");

        FILES_CONTENT = new HashMap<>();
        MAX_CHAR = maxChar;

        for (String filename : filesPath.keySet()) {
            String filePath = filesPath.get(filename);

            if (filename == null || filePath == null)
                continue;

            StringBuilder content = fileReader.getFileContent(filePath);
            FILES_CONTENT.put(filename, content.toString().split("\n"));
        }

        // Set client socket handler
        setSocketHandler((Socket clientSocket, String name) -> {
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
                this.printWriter = new PrintWriter(outputStream, true);

                inputStream = clientSocket.getInputStream();
                streamReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(streamReader);

                String inputLine, filename;
                String[] fileContent = null;
                int index = 0, lineIndex = 0;
                FileReadingTransferServerStatus status = FileReadingTransferServerStatus.START_CONN;

                if (PRINT_MESSAGES)
                    System.out.printf("%s: Waiting for socket messages...%n", NAME);

                while ((inputLine = reader.readLine()) != null && status != FileReadingTransferServerStatus.END_CONN) {

                    if (PRINT_MESSAGES)
                        System.out.printf("%s: Received message from socket...%n", NAME);

                    FilerReadingTransferClientMessages clientResponse = FilerReadingTransferClientMessages.fromString(inputLine);

                    if (clientResponse == FilerReadingTransferClientMessages.FORCED_END)
                        status = onForcedEnd();

                    else if (clientResponse == FilerReadingTransferClientMessages.MORE) {
                        status = onMore(status, fileContent, index, lineIndex);

                        if (status == FileReadingTransferServerStatus.ONGOING_LINE)
                            lineIndex += MAX_CHAR;

                        else if (status == FileReadingTransferServerStatus.END_LINE) {
                            index++;
                            lineIndex = 0;
                        }
                    } else if (!FILES_CONTENT.containsKey(inputLine))
                        status = onFileNotFound();

                    else {
                        filename = inputLine;
                        fileContent = FILES_CONTENT.get(filename);
                        index = 0;
                        status = onFileFound();
                    }
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

                this.printWriter.close();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public FileReadingTransferServerSocket(DefaultFileReader fileReader, Map<String, String> filesPath, int maxChar)
            throws NullPointerException, IOException {
        this(fileReader, filesPath, maxChar, false, false);
    }

    public FileReadingTransferServerSocket(DefaultFileReader fileReader, Map<String, String> filesPath)
            throws NullPointerException, IOException {
        this(fileReader, filesPath, 128);
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

    private void send(String message) {
        printWriter.println(message);
    }

    private void send(FileReadingTransferServerMessages f) {
        send(f.getMessage());
    }

    private FileReadingTransferServerStatus onForcedEnd() {
        send("Closing client socket...");
        return FileReadingTransferServerStatus.END_CONN;
    }

    private FileReadingTransferServerStatus onMore(FileReadingTransferServerStatus status, String[] fileContent,
                                                   int index, int lineIndex) {
        if (status == FileReadingTransferServerStatus.START_CONN || status == FileReadingTransferServerStatus.NOT_FOUND) {
            send("Filename must be set first...");
            return FileReadingTransferServerStatus.NOT_FOUND;
        }

        if (index >= fileContent.length) {
            send(FileReadingTransferServerMessages.END_FILE);
            return FileReadingTransferServerStatus.END_FILE;
        }

        if (status == FileReadingTransferServerStatus.FOUND || status == FileReadingTransferServerStatus.END_LINE) {
            send(FileReadingTransferServerMessages.START_LINE);
            return FileReadingTransferServerStatus.START_LINE;
        }

        String line = fileContent[index];

        if (lineIndex >= line.length()) {
            send(FileReadingTransferServerMessages.END_LINE);
            return FileReadingTransferServerStatus.END_LINE;
        }

        int substringUpperIndex = lineIndex + MAX_CHAR;

        if (substringUpperIndex < line.length())
            send(line.substring(lineIndex, substringUpperIndex));

        else
            send(line.substring(lineIndex));

        return FileReadingTransferServerStatus.ONGOING_LINE;
    }

    private FileReadingTransferServerStatus onFileFound() {
        send(FileReadingTransferServerMessages.FOUND);
        return FileReadingTransferServerStatus.FOUND;
    }

    private FileReadingTransferServerStatus onFileNotFound() {
        send(FileReadingTransferServerMessages.NOT_FOUND);
        return FileReadingTransferServerStatus.NOT_FOUND;
    }
}