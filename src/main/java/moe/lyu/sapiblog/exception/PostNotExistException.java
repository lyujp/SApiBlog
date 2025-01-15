package moe.lyu.sapiblog.exception;

public class PostNotExistException extends RuntimeException {
    public PostNotExistException(String message) {
        super(message);
    }
}
