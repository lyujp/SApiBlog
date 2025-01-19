package moe.lyu.sapiblog.exception.advice;

import moe.lyu.sapiblog.controller.TagPostController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.PostNotExistException;
import moe.lyu.sapiblog.exception.TagAddFailedException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {TagPostController.class})
public class TagPostControllerHandler {

    @ExceptionHandler(TagAddFailedException.class)
    public Resp handleTagAddFailedException(TagAddFailedException e) {
        return Resp.error(-500, e.getMessage());
    }

    @ExceptionHandler(PostNotExistException.class)
    public Resp handleTagAddFailedException(PostNotExistException e) {
        return Resp.error(-400, e.getMessage());
    }

    @ExceptionHandler(TagNotFoundException.class)
    public Resp handleTagAddFailedException(TagNotFoundException e) {
        return Resp.error(-400, e.getMessage());
    }
}
