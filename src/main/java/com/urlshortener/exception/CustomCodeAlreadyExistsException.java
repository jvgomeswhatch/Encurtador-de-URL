package com.urlshortener.exception;

public class CustomCodeAlreadyExistsException extends RuntimeException { // <--- ADICIONADO "public"
    public CustomCodeAlreadyExistsException(String message) {
        super(message);
    }
    }