package grupo3.exceptions;

public class FileExtensionNotFoundException extends Exception {

    public FileExtensionNotFoundException() {
    }

    public FileExtensionNotFoundException(String message) {
        super(message);
    }

    public FileExtensionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileExtensionNotFoundException(Throwable cause) {
        super(cause);
    }

    public FileExtensionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}