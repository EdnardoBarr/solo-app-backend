package ednardo.api.soloapp.exception;

public class ActivityMemberException extends RuntimeException{
    public ActivityMemberException() {
        super();
    }

    public ActivityMemberException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ActivityMemberException(final String message) {
        super(message);
    }

    public ActivityMemberException(final Throwable cause) {
        super(cause);
    }
}
