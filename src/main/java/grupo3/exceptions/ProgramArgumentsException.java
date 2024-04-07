package grupo3.exceptions;

public class ProgramArgumentsException extends Exception {
    public ProgramArgumentsException() {
    }

    public ProgramArgumentsException(String message) {
        super(message);
    }

    public ProgramArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramArgumentsException(Throwable cause) {
        super(cause);
    }

    public ProgramArgumentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
