package ednardo.api.soloapp.exception;

public class ActivityValidationException extends RuntimeException{

    public ActivityValidationException() {
        super();
    }

    public ActivityValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ActivityValidationException(final String message) {
        super(message);
    }

    public ActivityValidationException(final Throwable cause) {
        super(cause);
    }
}
