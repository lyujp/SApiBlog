package moe.lyu.sapiblog.exception;

public class UserJwtVerifyFailedException extends RuntimeException {
    public UserJwtVerifyFailedException(String message) {
        super(message);
    }
}
