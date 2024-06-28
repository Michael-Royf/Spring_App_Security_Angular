package com.michael.document.exceptions.payload;

public class NotFoundException  extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super("An error occurred");
    }
}
