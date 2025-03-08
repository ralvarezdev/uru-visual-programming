package filereadingtransfer;

public enum FilerReadingTransferClientMessages {
    MORE("+"), FORCED_END("."), UNDEFINED("0");

    private final String message;

    FilerReadingTransferClientMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static FilerReadingTransferClientMessages fromString(String message) {
        if (message == null)
            return UNDEFINED;

        for (FilerReadingTransferClientMessages f : FilerReadingTransferClientMessages.values())
            if (f.getMessage().equalsIgnoreCase(message))
                return f;

        return UNDEFINED;
    }
}
