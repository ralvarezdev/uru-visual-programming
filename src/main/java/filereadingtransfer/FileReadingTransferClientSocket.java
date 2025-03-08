package filereadingtransfer;

import sockets.BidirectionalClientSocket;

public class FileReadingTransferClientSocket extends BidirectionalClientSocket {
    public FileReadingTransferClientSocket(boolean printSocketMessages) {
        super(printSocketMessages);
    }

    private String send(FilerReadingTransferClientMessages f) {
        return send(f.getMessage());
    }

    public String getFileContent(String filename) {
        String serverResponseString = send(filename);
        FileReadingTransferServerMessages serverResponse = FileReadingTransferServerMessages.fromString(serverResponseString);

        if (serverResponse == FileReadingTransferServerMessages.NOT_FOUND) {
            System.out.printf("File '%s' not found on server...%n", filename);
            return null;
        }

        if (serverResponse != FileReadingTransferServerMessages.FOUND) {
            System.out.println("Unregistered response from server...");
            return null;
        }

        StringBuilder content = new StringBuilder();
        StringBuilder line = null;

        while (true) {

            serverResponseString = send(FilerReadingTransferClientMessages.MORE);
            serverResponse = FileReadingTransferServerMessages.fromString(serverResponseString);

            if (serverResponse == FileReadingTransferServerMessages.END_FILE) {
                if (PRINT_SOCKET_MESSAGES)
                    System.out.println("File content successfully read from server...");
                break;
            }

            if (serverResponse == FileReadingTransferServerMessages.START_LINE) {
                line = new StringBuilder();

                if (PRINT_SOCKET_MESSAGES) {
                    // System.out.println();
                    System.out.println("Getting line from server...");
                }
            } else if (serverResponse != FileReadingTransferServerMessages.END_LINE) {
                assert line != null;
                line.append(serverResponseString);

                /*
                 * if (PRINT_SOCKET_MESSAGES) System.out.println(serverResponseString);
                 */
            } else {
                assert line != null;
                line.append("\n");
                content.append(line);
            }

        }

        return content.toString();
    }
}
