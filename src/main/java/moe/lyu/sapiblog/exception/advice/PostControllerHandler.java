package moe.lyu.sapiblog.exception.advice;

import moe.lyu.sapiblog.controller.PostController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PostController.class)
public class PostControllerHandler {

    @ExceptionHandler(CategoryAddFailedException.class)
    public Resp handleCategoryAddFailedException(CategoryAddFailedException e) {
        return Resp.error(-300, e.getMessage());
    }

    @ExceptionHandler(PostNotExistException.class)
    public Resp handlePostNotExistException(PostNotExistException e) {
        return Resp.error(-400, e.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public Resp handleCategoryNotFoundException(CategoryNotFoundException e) {
        return Resp.error(-400, e.getMessage());
    }

    @ExceptionHandler(TagAddFailedException.class)
    public Resp handleTagAddFailedException(TagAddFailedException e) {
        return Resp.error(-300, e.getMessage());
    }

    @ExceptionHandler(TagNotFoundException.class)
    public Resp handleTagNotFoundException(TagNotFoundException e) {
        return Resp.error(-400, e.getMessage());
    }
}
