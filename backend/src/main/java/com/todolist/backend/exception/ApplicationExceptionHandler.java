package com.todolist.backend.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> numberFormatExceptionHandler(NumberFormatException e) {
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("errorMessage", String.format("Must send a Number value (%s)", e.getLocalizedMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map<String, Object>> numberInvalidFormatException(InvalidFormatException e) {
        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("errorMessage", e.getOriginalMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        BindingResult result = ex.getBindingResult();

        List<String> errorList = new ArrayList<>();
        result.getFieldErrors().forEach((fieldError) ->
                errorList.add(fieldError.getField() + " : " + fieldError.getDefaultMessage() + " : rejected value [" + fieldError.getRejectedValue() + "]"));
        result.getGlobalErrors().forEach((fieldError) ->
                errorList.add(fieldError.getObjectName() + " : " + fieldError.getDefaultMessage()));

        errorResponse.put("errorList", errorList);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
