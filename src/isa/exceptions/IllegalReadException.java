package isa.exceptions;

public class IllegalReadException extends RuntimeException {
    public IllegalReadException() {
        super();
    }

    public IllegalReadException(String msg) {
        super(msg);
    }
}