package ru.kata.spring.boot_security.demo.exception_hadling;

public class NoSuchUsrException extends RuntimeException {

    public NoSuchUsrException(String message) {
        super(message);
    }
}
