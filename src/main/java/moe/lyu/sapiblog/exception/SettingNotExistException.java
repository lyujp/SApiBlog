package moe.lyu.sapiblog.exception;

public class SettingNotExistException extends RuntimeException {
    public SettingNotExistException(String message) {
        super(message);
    }
}
