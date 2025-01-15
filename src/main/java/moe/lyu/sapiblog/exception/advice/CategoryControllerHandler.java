package moe.lyu.sapiblog.exception.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import moe.lyu.sapiblog.controller.CategoryController;
import moe.lyu.sapiblog.dto.Resp;
import moe.lyu.sapiblog.exception.CategoryAlreadyExistException;
import moe.lyu.sapiblog.exception.CategoryFieldNotFoundException;
import moe.lyu.sapiblog.exception.CategoryNotFoundException;
import moe.lyu.sapiblog.exception.CategoryUnknownException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {CategoryController.class})
public class CategoryControllerHandler {

    @ExceptionHandler(CategoryFieldNotFoundException.class)
    public Resp handleCategoryFieldNotFound(CategoryFieldNotFoundException e) {
        return Resp.error(-400, "Category field '" + e.getMessage() + "' required not found");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public Resp handleCategoryNotFound(CategoryNotFoundException e) {
        return Resp.error(-400, "Category '" + e.getMessage() + "' not found");
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    public Resp handleCategoryAlreadyExist(CategoryAlreadyExistException e) {
        return Resp.error(-300, "Category '" + e.getMessage() + "' already exist");
    }

    @ExceptionHandler(CategoryUnknownException.class)
    public Resp handleCategoryUnknown(CategoryUnknownException e) {
        e.printStackTrace();
        return Resp.error(-500, "Category unknown exception");
    }

    @ExceptionHandler(JsonProcessingException.class)
    public Resp handleJsonProcessingException(JsonProcessingException e) {
        e.printStackTrace();
        return Resp.error(-500, "Category add failed and Json processing exception");
    }

}
