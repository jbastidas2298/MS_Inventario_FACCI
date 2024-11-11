package com.facci.configuracion.handler;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(NotFoundUserException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Cliente no encontrado...");
        return message;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(UserAlreadyExistsException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage("Cliente ya se encuentra registrado...");
        return message;
    }
}
