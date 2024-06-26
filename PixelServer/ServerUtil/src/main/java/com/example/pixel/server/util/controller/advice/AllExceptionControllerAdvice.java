package com.example.pixel.server.util.controller.advice;

import com.example.pixel.server.util.controller.advice.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import static com.example.pixel.server.util.controller.advice.ExceptionUtil.newException;

@RestControllerAdvice
public class AllExceptionControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object processRuntimeException(RuntimeException e, WebRequest request) {
        log.error("Unsupported Exception:", e);
        return newException(e, request);
    }

    @ResponseBody
    @ExceptionHandler({NotHaveTokenException.class, HttpClientErrorException.Unauthorized.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Object processNotHaveRefreshToken(RuntimeException e, WebRequest request) {
        return newException(e, request);
    }

    @ResponseBody
    @ExceptionHandler({UserNotFoundException.class, NotFoundException.class, HttpClientErrorException.NotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object notFoundException(RuntimeException e, WebRequest request) {
        return newException(e, request);
    }

    @ResponseBody
    @ExceptionHandler({UserAlreadyExists.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object clientException(RuntimeException e, WebRequest request) {
        return newException(e, request);
    }

    @ResponseBody
    @ExceptionHandler({ForbiddenException.class, HttpClientErrorException.Forbidden.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object forbiddenException(RuntimeException e, WebRequest request) {
        return newException(e, request);
    }

}
