package moe.lyu.sapiblog.exception;

public class CategoryFieldNotFoundException extends RuntimeException {
    public CategoryFieldNotFoundException(String message) {
        super(message);
    }
}
