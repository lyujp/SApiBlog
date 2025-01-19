package moe.lyu.sapiblog.exception.advice;

import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.CategoryAddFailedException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.PostNotExistException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CategoryPostController.class})
public class CategoryPostControllerHandler {

    @ExceptionHandler(CategoryAddFailedException.class)
    public Resp handleCategoryAddFailedException(CategoryAddFailedException e) {
        return Resp.error(-500, e.getMessage());
    }

    @ExceptionHandler(PostNotExistException.class)
    public Resp handleCategoryAddFailedException(PostNotExistException e) {
        return Resp.error(-400, e.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public Resp handleCategoryAddFailedException(CategoryNotFoundException e) {
        return Resp.error(-400, e.getMessage());
    }
}
