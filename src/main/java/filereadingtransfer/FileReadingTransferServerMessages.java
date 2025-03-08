package filereadingtransfer;

public enum FileReadingTransferServerMessages {
    FOUND("!"), NOT_FOUND("?"), START_LINE("{"), END_LINE("}"), END_FILE("."), UNDEFINED("0");

    private final String message;

    FileReadingTransferServerMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static FileReadingTransferServerMessages fromString(String message) {
        if (message == null)
            return UNDEFINED;

        for (FileReadingTransferServerMessages f : FileReadingTransferServerMessages.values())
            if (f.getMessage().equalsIgnoreCase(message))
                return f;

        return UNDEFINED;
    }
}
