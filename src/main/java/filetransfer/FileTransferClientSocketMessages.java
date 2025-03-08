package filetransfer;

public enum FileTransferClientSocketMessages {
    SEND("+"), END("."), UNDEFINED("0");

    private final String message;

    FileTransferClientSocketMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static FileTransferClientSocketMessages fromString(String message) {
        if (message == null)
            return UNDEFINED;

        for (FileTransferClientSocketMessages f : FileTransferClientSocketMessages.values())
            if (f.getMessage().equalsIgnoreCase(message))
                return f;

        return UNDEFINED;
    }
}
