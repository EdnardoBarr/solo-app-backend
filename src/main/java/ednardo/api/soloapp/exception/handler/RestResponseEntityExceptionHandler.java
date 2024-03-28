package ednardo.api.soloapp.exception.handler;

import ednardo.api.soloapp.exception.JWTException;
import ednardo.api.soloapp.exception.UserAlreadyExistsException;
import ednardo.api.soloapp.exception.UserNotFoundException;
import ednardo.api.soloapp.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messages;

    public RestResponseEntityExceptionHandler() {
        super();
    }

    @Override
    protected ResponseEntity<Object> handleBindException (BindException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
       final BindingResult result = exception.getBindingResult();
        GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());

        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ UserAlreadyExistsException.class })
    public ResponseEntity<Object> handleUserAlreadyExistsException (RuntimeException exception, WebRequest request) {
        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExists");

        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFoundException (RuntimeException exception, WebRequest request) {
        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");

        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ JWTException.class })
    public ResponseEntity<Object> handleJWTException (RuntimeException exception, WebRequest request) {
        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("auth.message.invalidToken", null, request.getLocale()), "UserNotFound");

        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
}
