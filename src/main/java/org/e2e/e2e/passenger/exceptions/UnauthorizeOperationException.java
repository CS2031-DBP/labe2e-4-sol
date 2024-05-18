package org.e2e.e2e.passenger.exceptions;

public class UnauthorizeOperationException extends RuntimeException{
    public UnauthorizeOperationException(String message) {
        super(message);
    }
}
