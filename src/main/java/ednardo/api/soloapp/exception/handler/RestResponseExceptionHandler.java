package ednardo.api.soloapp.exception.handler;

import ednardo.api.soloapp.exception.TokenRefreshException;
import ednardo.api.soloapp.model.dto.ErrorDTO;
import ednardo.api.soloapp.model.dto.FieldErrorDTO;
import io.micrometer.common.lang.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseExceptionHandler() {
        super();
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleIllegalArguments(
            RuntimeException ex, WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorDTO errorDTO = ErrorDTO.builder().code(httpStatus.value()).message(ex.getMessage()).build();
        return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), httpStatus, webRequest);
    }

    @ExceptionHandler(value = {EmptyResultDataAccessException.class})
    protected ResponseEntity<Object> handleEmptyResult(RuntimeException ex, WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErrorDTO errorDTO = ErrorDTO.builder().code(httpStatus.value()).message("Entidade não encontrada").build();
        return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), httpStatus, webRequest);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticatorException(RuntimeException ex, WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ErrorDTO errorDTO = ErrorDTO.builder().code(httpStatus.value()).message(ex.getMessage()).build();
        return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), httpStatus, webRequest);
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<Object> handleTokenRefreshException(TokenRefreshException ex, WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ErrorDTO errorDTO = ErrorDTO.builder().code(httpStatus.value()).message(ex.getMessage()).build();
        return handleExceptionInternal(ex, errorDTO, new HttpHeaders(), httpStatus, webRequest);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException exception,
            @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        return this.handleBindException(exception, headers, status, request);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleBindException(
            @NonNull BindException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        List<FieldErrorDTO> errorList = ex.getAllErrors().stream().map(objectError ->
                new FieldErrorDTO(((FieldError)objectError).getField(), objectError.getDefaultMessage())
        ).collect(Collectors.toList());

        ErrorDTO dto = ErrorDTO.builder().code(httpStatus.value()).message("Argumentos inválidos na requisição").
                errors(errorList).build();

        return ResponseEntity.badRequest().body(dto);
    }

}


//@ControllerAdvice
//public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @Autowired
//    private MessageSource messages;
//
//    public RestResponseEntityExceptionHandler() {
//        super();
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleBindException (BindException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//       final BindingResult result = exception.getBindingResult();
//        GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
//
//        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler({ UserAlreadyExistsException.class })
//    public ResponseEntity<Object> handleUserAlreadyExistsException (RuntimeException exception, WebRequest request) {
//        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExists");
//
//        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
//    }
//
//    @ExceptionHandler({ UserNotFoundException.class })
//    public ResponseEntity<Object> handleUserNotFoundException (RuntimeException exception, WebRequest request) {
//        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");
//
//        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
//    }
//
//    @ExceptionHandler({ JWTException.class })
//    public ResponseEntity<Object> handleJWTException (RuntimeException exception, WebRequest request) {
//        GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("auth.message.invalidToken", null, request.getLocale()), "UserNotFound");
//
//        return handleExceptionInternal(exception, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
//    }
//}
