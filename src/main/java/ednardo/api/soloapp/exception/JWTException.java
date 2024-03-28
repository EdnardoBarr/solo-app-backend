package ednardo.api.soloapp.exception;

public class JWTException extends RuntimeException {

    public JWTException() {
        super();
    }

    public JWTException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JWTException(final String message) {
        super(message);
    }

    public JWTException(final Throwable cause) {
        super(cause);
    }
}
