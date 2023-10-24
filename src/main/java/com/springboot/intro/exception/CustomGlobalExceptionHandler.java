package com.springboot.intro.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String DATE_FORMAT = "EEEE dd-MMMM-yyyy HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().format(formatter));
        body.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                        .map(this::getErrorMessage)
                        .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> entityNotFound(EntityNotFoundException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().format(formatter));
        map.put("status", HttpStatus.NOT_FOUND);
        map.put("error", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> registrationError(RegistrationException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().format(formatter));
        map.put("status", HttpStatus.BAD_REQUEST);
        map.put("error", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", LocalDateTime.now().format(formatter));
        map.put("status", HttpStatus.NOT_FOUND);
        map.put("error", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }
}
