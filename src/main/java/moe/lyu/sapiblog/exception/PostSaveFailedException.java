package moe.lyu.sapiblog.exception;

public class PostSaveFailedException extends RuntimeException {
    public PostSaveFailedException(String message) {
        super(message);
    }
}
