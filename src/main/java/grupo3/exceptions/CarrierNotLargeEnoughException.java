package grupo3.exceptions;

public class CarrierNotLargeEnoughException extends RuntimeException {
    public CarrierNotLargeEnoughException() {
    }

    public CarrierNotLargeEnoughException(String message) {
        super(message);
    }

    public CarrierNotLargeEnoughException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarrierNotLargeEnoughException(Throwable cause) {
        super(cause);
    }

    public CarrierNotLargeEnoughException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
