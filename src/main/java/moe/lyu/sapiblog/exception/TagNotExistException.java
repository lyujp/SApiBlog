package moe.lyu.sapiblog.exception;

public class TagNotExistException extends RuntimeException {
    public TagNotExistException(String message) {
        super(message);
    }
}
