package com.facci.configuracion.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(CustomException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public com.facci.configuracion.handler.ErrorMessage handleCustomException(CustomException ex, WebRequest request) {
        return new ErrorMessage(ex.getCodigo(), ex.getMessage());
    }
}
