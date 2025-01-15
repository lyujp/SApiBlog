package moe.lyu.sapiblog.exception;

public class UserLoginFailed extends RuntimeException {
    public UserLoginFailed(String message) {
        super(message);
    }
}
