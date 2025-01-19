package moe.lyu.sapiblog.exception;

public class UserRegisterFailedException extends RuntimeException {
    public UserRegisterFailedException(String message) {
        super(message);
    }
}
