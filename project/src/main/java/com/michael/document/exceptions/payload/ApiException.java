package com.michael.document.exceptions.payload;

public class ApiException  extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public ApiException(String message) {
        super(message);
    }

    public ApiException() {
        super("An error occurred");
    }
}
