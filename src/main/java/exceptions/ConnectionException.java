package exceptions;

public class ConnectionException extends Exception {
    public ConnectionException(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
