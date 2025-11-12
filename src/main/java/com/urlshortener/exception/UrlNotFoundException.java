package com.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException { // <--- ADICIONADO "public"
    public UrlNotFoundException(String message) {
        super(message);
    }
}