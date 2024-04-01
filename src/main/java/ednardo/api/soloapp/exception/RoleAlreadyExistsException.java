package ednardo.api.soloapp.exception;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException() {
        super();
    }

    public RoleAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RoleAlreadyExistsException(final String message) {
        super(message);
    }

    public RoleAlreadyExistsException(final Throwable cause) {
        super(cause);
    }
}
