package com.restkeeper.response.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e){

        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
        //包装成异常，通过ResponseAdvisor进行捕获，能很好的判断出该异常
        return exceptionResponse;
    }
}
