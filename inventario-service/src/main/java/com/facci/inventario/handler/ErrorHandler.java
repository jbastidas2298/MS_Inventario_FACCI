package com.facci.inventario.handler;

import com.facci.comun.handler.CustomException;
import com.facci.comun.handler.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(CustomException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleCustomException(CustomException ex, WebRequest request) {
        return new ErrorMessage(ex.getCodigo(), ex.getMessage());
    }
}
