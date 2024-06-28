package com.michael.document.exceptions.payload;

public class ExistException  extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ExistException(String message) {
        super(message);
    }

    public ExistException() {
        super("An error occurred");
    }
}
