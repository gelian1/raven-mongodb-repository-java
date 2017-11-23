package raven.mongodb.repository.exceptions;

public class EmptyArgumentException extends Exception {
    public EmptyArgumentException(String message) {
        super(message);
    }
}
