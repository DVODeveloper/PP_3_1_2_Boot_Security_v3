package ru.kata.spring.boot_security.demo.exception_hadling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UsrGlobalExceptionHandler {

    @ExceptionHandler(NoSuchUsrException.class)
    public ResponseEntity<UsrIncorrectData> handleException(NoSuchUsrException e) {
        UsrIncorrectData data = new UsrIncorrectData();
        data.setInfo(e.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UsrIncorrectData> handleException(Exception e) {
        UsrIncorrectData data = new UsrIncorrectData();
        data.setInfo(e.getMessage());

        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

}
