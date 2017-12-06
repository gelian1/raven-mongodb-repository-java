package raven.mongodb.repository.exceptions;

public class FailedException extends Exception {
    public FailedException() {
        super("失败异常");
    }
    public FailedException(String message) {
        super(message);
    }
}
