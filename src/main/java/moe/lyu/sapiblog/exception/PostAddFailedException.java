package moe.lyu.sapiblog.exception;

public class PostAddFailedException extends RuntimeException {
    public PostAddFailedException(String message) {
        super(message);
    }
}
