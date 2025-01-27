package moe.lyu.sapiblog.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.controller.TagController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.TagAlreadyExistException;
import moe.lyu.sapiblog.exception.TagNotFoundException;
import moe.lyu.sapiblog.exception.TagUnknownException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {TagController.class})
@Order(0)
public class TagControllerHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public Resp handleTagNotFound(TagNotFoundException e) {
        return Resp.error(-400, e.getMessage());
    }

    @ExceptionHandler(TagAlreadyExistException.class)
    public Resp handleTagAlreadyExist(TagAlreadyExistException e) {
        return Resp.error(-200, e.getMessage());
    }

    @ExceptionHandler(TagUnknownException.class)
    public Resp handleTagUnknown(TagUnknownException e) {
        e.printStackTrace();
        return Resp.error(-500, "Tag unknown exception");
    }

    @ExceptionHandler(JsonProcessingException.class)
    public Resp handleJsonProcessingException(JsonProcessingException e) {
        e.printStackTrace();
        return Resp.error(-500, "Tag add failed and Json processing exception");
    }

}
