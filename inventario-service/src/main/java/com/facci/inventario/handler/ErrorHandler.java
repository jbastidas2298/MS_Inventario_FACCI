package com.facci.inventario.handler;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(NotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Articulo no encontrado...");
        return message;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(AlreadyExistsException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Articulo existente...");
        return message;
    }
}
