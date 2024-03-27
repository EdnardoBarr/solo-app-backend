package ednardo.api.soloapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(final String message) {
        super();
        this.message = message;
    }

    public GenericResponse(final String message, final String error) {
        super();
        this.message = message;
        this.error = error;
    }

    public GenericResponse(List<ObjectError> allErrors, String error) {
        this.error = error;
        String temp = allErrors.stream().map(e -> {
            if (e instanceof FieldError) {
                return "{\"field\":\"" + ((FieldError) e).getField() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            } else {
                return "{\"object\":\"" + e.getObjectName() + "\",\"defaultMessage\":\"" + e.getDefaultMessage() + "\"}";
            }
        }).collect(Collectors.joining(","));
        this.message = "[" + temp + "]";
    }

    public GenericResponse(List<FieldError> fieldErrors, List<ObjectError> globalErrors) {
        super();
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.message = mapper.writeValueAsString(fieldErrors);
            this.error = mapper.writeValueAsString(globalErrors);
        } catch (JsonProcessingException e) {
            this.message = "";
            this.error = "";
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
