package grupo3.exceptions;

public class BmpException extends RuntimeException {
    public BmpException() {
    }

    public BmpException(String message) {
        super(message);
    }

    public BmpException(String message, Throwable cause) {
        super(message, cause);
    }

    public BmpException(Throwable cause) {
        super(cause);
    }

    public BmpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
