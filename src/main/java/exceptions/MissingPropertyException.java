package exceptions;

public class MissingPropertyException extends Exception {
    public MissingPropertyException(String errorMessage) {
        super(errorMessage);
    }

    public MissingPropertyException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
